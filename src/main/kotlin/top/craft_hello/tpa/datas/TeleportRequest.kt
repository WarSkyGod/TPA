package top.craft_hello.tpa.datas

import cn.handyplus.lib.adapter.HandyRunnable
import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.TeleportUtil
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

// 传送请求生命周期：
// 玩家对玩家（tpa/tphere）：发起校验 -> 入队（键 = 需点击接受的一方）-> 超时自动失效 -> 接受/拒绝 -> 延迟传送（移动取消）
// 玩家对位置（warp/home/spawn/back/rtp/tplogout）：校验 -> 锁定请求队列 -> 延迟传送（移动取消）-> 传送成功
class TeleportRequest private constructor(
    val requestType: RequestType,
    val requester: Player,
    val targetPlayer: Player?,
    val targetLocation: Location?,
    val targetName: String
) {
    private var timeoutTimer: HandyRunnable? = null

    // 请求超时自动失效
    fun startTimeoutTimer(accepterId: UUID, delay: Int) {
        val request = this
        val timer = object : HandyRunnable() {
            override fun run() {
                val current = requestQueue[accepterId]
                if (current === request) {
                    requestQueue.remove(accepterId)
                    val target = request.targetPlayer
                    if (target != null && target.isOnline && request.requester.isOnline) {
                        SendMessageUtil.timeOverDeny(request.requester, target)
                    }
                }
                cancel()
            }
        }
        timeoutTimer = timer
        HandySchedulerUtil.runTaskLaterAsynchronously(timer, delay * 20L)
    }

    fun cancelTimeout() {
        try {
            timeoutTimer?.cancel()
        } catch (ignored: Exception) {
        }
        timeoutTimer = null
    }

    companion object {
        // 待处理的传送请求（键 = 需要点击接受/或被锁定传送的玩家）
        val requestQueue = ConcurrentHashMap<UUID, TeleportRequest>()
        // 命令冷却队列（剩余秒数）
        val commandDelayQueue = ConcurrentHashMap<UUID, Int>()

        fun isQueued(uuid: UUID): Boolean = requestQueue.containsKey(uuid)

        fun getQueued(uuid: UUID): TeleportRequest? = requestQueue[uuid]

        // /tpa：请求方传送到目标
        fun tpRequest(requester: Player, target: Player): Boolean {
            if (checkSelf(requester, target) || checkRequestPending(requester, target)) return false
            val requesterData = PlayerDataManager.get(requester)
            if (requesterData.isDeny(target.uniqueId.toString())) {
                SendMessageUtil.alreadyBlacklistedError(requester)
                return false
            }
            if (PlayerDataManager.get(target).isDeny(requester.uniqueId.toString())) {
                SendMessageUtil.blockedByTargetError(requester)
                return false
            }
            val request = TeleportRequest(RequestType.TPA, requester, target, null, target.name)
            requestQueue[target.uniqueId] = request
            val delay = ConfigManager.config.acceptDelay
            SendMessageUtil.requestTeleportToTarget(requester, target, delay.toString())
            request.startTimeoutTimer(target.uniqueId, delay)
            return true
        }

        // /tphere：目标传送到请求方
        fun tpHereRequest(requester: Player, target: Player): Boolean {
            if (checkSelf(requester, target) || checkRequestPending(requester, target)) return false
            val requesterData = PlayerDataManager.get(requester)
            if (requesterData.isDeny(target.uniqueId.toString())) {
                SendMessageUtil.alreadyBlacklistedError(requester)
                return false
            }
            if (PlayerDataManager.get(target).isDeny(requester.uniqueId.toString())) {
                SendMessageUtil.blockedByTargetError(requester)
                return false
            }
            val request = TeleportRequest(RequestType.TPA_HERE, requester, target, null, requester.name)
            requestQueue[target.uniqueId] = request
            val delay = ConfigManager.config.acceptDelay
            SendMessageUtil.requestTargetTeleportToHere(requester, target, delay.toString())
            request.startTimeoutTimer(target.uniqueId, delay)
            return true
        }

        // /tpaccept
        fun tpaccept(accepter: Player): Boolean {
            val request = requestQueue.remove(accepter.uniqueId) ?: run {
                SendMessageUtil.noPendingRequestError(accepter)
                return false
            }
            request.cancelTimeout()
            val requester = request.requester
            if (!requester.isOnline) {
                SendMessageUtil.targetOfflineError(accepter, requester.name)
                return true
            }
            SendMessageUtil.acceptMessage(requester, accepter)
            when (request.requestType) {
                RequestType.TPA -> delayedTeleportTo(mover = requester, destination = accepter.location, opponent = accepter)
                RequestType.TPA_HERE -> delayedTeleportTo(mover = accepter, destination = requester.location, opponent = requester)
                else -> {}
            }
            return true
        }

        // /tpdeny
        fun tpdeny(accepter: Player): Boolean {
            val request = requestQueue.remove(accepter.uniqueId) ?: run {
                SendMessageUtil.noPendingRequestError(accepter)
                return false
            }
            request.cancelTimeout()
            SendMessageUtil.denyMessage(request.requester, accepter)
            return true
        }

        // 位置类传送（warp/home/spawn/back/tplogout）
        fun locationRequest(sender: Player, requestType: RequestType, location: Location, targetName: String): Boolean {
            if (checkRequestPending(sender)) return false
            val request = TeleportRequest(requestType, sender, null, location, targetName)
            requestQueue[sender.uniqueId] = request
            // 移动检测：non_tpa_or_tphere_disable_check 为 true 时跳过
            val checkMove = !ConfigManager.config.nonTpaOrTphereDisableCheck
            if (ConfigManager.config.isEnableTeleportDelay(sender) && checkMove) {
                delayedTeleport(request, location)
            } else {
                finishTeleport(request, location)
            }
            return true
        }

        // /rtp：随机传送
        fun rtpRequest(sender: Player): Boolean {
            if (checkRequestPending(sender)) return false
            val world = sender.world
            if (ConfigManager.config.isRtpDisableWorld(world)) {
                SendMessageUtil.worldDisabledError(sender)
                return false
            }
            SendMessageUtil.generateRandomLocationMessage(sender)
            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleGenerateRandomLocationMessage(sender)
            val location = findRandomLocation(world, ConfigManager.config.rtpLimitX, ConfigManager.config.rtpLimitZ)
            if (location == null) {
                SendMessageUtil.rtpFailedError(sender)
                return false
            }
            val request = TeleportRequest(RequestType.RTP, sender, null, location, "rtp_name")
            requestQueue[sender.uniqueId] = request
            val checkMove = !ConfigManager.config.nonTpaOrTphereDisableCheck
            if (ConfigManager.config.isEnableTeleportDelay(sender) && checkMove) {
                delayedTeleport(request, location)
            } else {
                finishTeleport(request, location)
            }
            return true
        }

        // 玩家退出时清理其请求
        fun clearQueue(uuid: UUID) {
            requestQueue.remove(uuid)?.cancelTimeout()
        }

        // =============== 命令冷却 ===============

        // 命令执行成功后启动冷却计时
        fun startCommandDelay(player: Player) {
            val delay = ConfigManager.config.getCommandDelay(player)
            if (delay <= 0) return
            commandDelayQueue[player.uniqueId] = delay
            val timer = object : HandyRunnable() {
                override fun run() {
                    val remaining = commandDelayQueue[player.uniqueId]
                    if (remaining == null || !player.isOnline || remaining <= 1) {
                        commandDelayQueue.remove(player.uniqueId)
                        cancel()
                        return
                    }
                    commandDelayQueue[player.uniqueId] = remaining - 1
                }
            }
            HandySchedulerUtil.runTaskTimerAsynchronously(timer, 20L, 20L)
        }

        // 是否在命令冷却中
        fun isInCommandDelay(uuid: UUID): Boolean = commandDelayQueue.containsKey(uuid)

        fun getCommandDelayRemaining(uuid: UUID): Int = commandDelayQueue[uuid] ?: 0

        // =============== 内部 ===============

        private fun checkSelf(requester: Player, target: Player): Boolean {
            if (requester == target) {
                SendMessageUtil.selfOperationError(requester)
                return true
            }
            return false
        }

        // 对齐 3.x：执行者或目标任一方有未处理请求，"对方或您有待处理的请求"都发给执行者
        private fun checkRequestPending(requester: Player, vararg others: Player): Boolean {
            if (requestQueue.containsKey(requester.uniqueId) || others.any { requestQueue.containsKey(it.uniqueId) }) {
                SendMessageUtil.requestPendingError(requester)
                return true
            }
            return false
        }

        // 玩家对玩家的延迟传送（tpaccept 后）：移动者倒计时，移动取消则通知双方
        private fun delayedTeleportTo(mover: Player, destination: Location, opponent: Player) {
            val opponentName = opponent.name
            if (ConfigManager.config.isEnableTeleportDelay(mover)) {
                TeleportUtil.delayTeleport(
                    mover, destination, opponentName,
                    onComplete = {
                        TeleportUtil.teleport(mover, destination)
                        startCommandDelay(mover)
                    },
                    onCancel = {
                        SendMessageUtil.move(mover, opponent)
                    }
                )
            } else {
                TeleportUtil.teleport(mover, destination)
                SendMessageUtil.youTeleportedToMessage(mover, opponentName)
                startCommandDelay(mover)
            }
        }

        // 位置类请求的延迟传送：完成后出队 + 发成功消息
        private fun delayedTeleport(request: TeleportRequest, location: Location) {
            val sender = request.requester
            TeleportUtil.delayTeleport(
                sender, location, request.targetName,
                onComplete = {
                    finishTeleport(request, location)
                },
                onCancel = {
                    requestQueue.remove(sender.uniqueId)
                    SendMessageUtil.move(sender, null)
                }
            )
        }

        // 完成传送：出队 + 传送 + 成功消息 + 命令冷却
        private fun finishTeleport(request: TeleportRequest, location: Location) {
            val sender = request.requester
            requestQueue.remove(sender.uniqueId)
            if (!sender.isOnline) return
            val successMessage: (Player, String) -> Unit = when (request.requestType) {
                RequestType.WARP -> SendMessageUtil::tpToWarpMessage
                RequestType.HOME -> SendMessageUtil::tpToHomeMessage
                RequestType.SPAWN -> { player, _ -> SendMessageUtil.backSpawnSuccessMessage(player) }
                RequestType.BACK -> { player, _ -> SendMessageUtil.backLastLocationSuccessMessage(player) }
                RequestType.RTP -> { player, _ -> SendMessageUtil.rtpSuccessMessage(player) }
                RequestType.TP_LOGOUT -> { player, name -> SendMessageUtil.tpLogoutCommandSuccess(player, name) }
                else -> SendMessageUtil::youTeleportedToMessage
            }
            TeleportUtil.teleport(sender, location)
            successMessage(sender, request.targetName)
            startCommandDelay(sender)
        }

        // 寻找随机传送点：主世界取地表最高点；下界/末地扫描首个 solid+双空气结构
        private fun findRandomLocation(world: World, limitX: Int, limitZ: Int): Location? {
            val isScanningWorld = world.environment == World.Environment.NETHER || world.environment == World.Environment.THE_END
            var found: Location? = null
            var attempts = 0
            while (attempts < 50 && found == null) {
                attempts++
                val x = (Math.random() * limitX * 2 - limitX).toInt()
                val z = (Math.random() * limitZ * 2 - limitZ).toInt()
                if (!isScanningWorld) {
                    val y = world.getHighestBlockYAt(x, z)
                    if (y <= world.minHeight) continue
                    val feetBlock = world.getBlockAt(x, y, z)
                    val groundBlock = world.getBlockAt(x, y - 1, z)
                    // 落点不能在实体方块内，且脚下必须为实体方块
                    if (feetBlock.isSolid || !groundBlock.isSolid) continue
                    if (world.getBlockAt(x, y + 1, z).isSolid) continue
                    found = Location(world, x + 0.5, y.toDouble(), z + 0.5, Math.random().toFloat() * 360f, 0f)
                } else {
                    // 下界/末地：从最低点向上找 solid 上方有两格空气的位置
                    var y = world.minHeight
                    while (y < world.maxHeight - 2 && found == null) {
                        val ground = world.getBlockAt(x, y, z)
                        if (ground.isSolid && !world.getBlockAt(x, y + 1, z).isSolid && !world.getBlockAt(x, y + 2, z).isSolid) {
                            found = Location(world, x + 0.5, y + 1.0, z + 0.5, Math.random().toFloat() * 360f, 0f)
                        }
                        y++
                    }
                }
            }
            return found
        }
    }
}
