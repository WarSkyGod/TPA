package top.craft_hello.tpa.datas

import cn.handyplus.lib.adapter.HandyRunnable
import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bukkit.Location
import org.bukkit.Material
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

        // /rtp：随机传送。随机点生成是异步链（Folia 禁止跨 region 同步取 chunk，
        // getHighestBlockYAt 必须在目标 chunk 所属 region 线程上调用），完成后继续入队与传送
        fun rtpRequest(sender: Player): Boolean {
            if (checkRequestPending(sender)) return false
            val world = sender.world
            if (ConfigManager.config.isRtpDisableWorld(world)) {
                SendMessageUtil.worldDisabledError(sender)
                return false
            }
            SendMessageUtil.generateRandomLocationMessage(sender)
            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleGenerateRandomLocationMessage(sender)
            val attempts = ConfigManager.config.rtpGenerateAttempts
            // 对齐 3.x：以玩家当前位置为中心随机；随机范围实时收缩到玩家位置与世界边界的可达距离内
            attemptRandomLocation(world, sender.location.x, sender.location.z, ConfigManager.config.rtpLimitX, ConfigManager.config.rtpLimitZ, attempts, attempts) { location ->
                if (!sender.isOnline) return@attemptRandomLocation
                if (location == null) {
                    SendMessageUtil.rtpFailedError(sender)
                    return@attemptRandomLocation
                }
                val request = TeleportRequest(RequestType.RTP, sender, null, location, "rtp_name")
                requestQueue[sender.uniqueId] = request
                val checkMove = !ConfigManager.config.nonTpaOrTphereDisableCheck
                if (ConfigManager.config.isEnableTeleportDelay(sender) && checkMove) {
                    delayedTeleport(request, location)
                } else {
                    finishTeleport(request, location)
                }
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

        // 危险位置：脚下/所在/头部任一命中即拒绝（岩浆、水、火、灵魂火、岩浆块；虚空由边界与 solid 检查兜底）
        private val dangerousMaterials = setOf(
            Material.LAVA, Material.WATER, Material.FIRE, Material.SOUL_FIRE, Material.MAGMA_BLOCK
        )

        // y 为脚部层：ground(y-1) 必须为实体且不危险，feet(y)/head(y+1) 必须可穿越且不危险
        private fun isSafeStanding(world: World, x: Int, y: Int, z: Int): Boolean {
            val ground = world.getBlockAt(x, y - 1, z)
            val feet = world.getBlockAt(x, y, z)
            val head = world.getBlockAt(x, y + 1, z)
            if (!ground.isSolid || ground.type in dangerousMaterials) return false
            if (feet.isSolid || feet.type in dangerousMaterials) return false
            if (head.isSolid || head.type in dangerousMaterials) return false
            return true
        }

        // 寻找随机传送点（异步链，Folia 安全）：
        // - 以玩家当前位置为中心 ±limit，随机范围实时收缩到玩家位置与当前世界边界的可达距离内
        // - 必须先 getChunkAtAsync 异步加载目标列所在 chunk（Folia 禁止跨 region 同步取 chunk，
        //   加载完成后回调在该 chunk 所属 region 线程执行，此时读方块合法）
        // - 主世界取地表最高点，下界/末地扫描首个安全柱；最多 maxAttempts 次，全部失败回调 null
        private fun attemptRandomLocation(
            world: World, centerX: Double, centerZ: Double,
            limitX: Int, limitZ: Int, maxAttempts: Int, remaining: Int,
            onResult: (Location?) -> Unit
        ) {
            if (remaining <= 0) {
                onResult(null)
                return
            }
            val isScanningWorld = world.environment == World.Environment.NETHER || world.environment == World.Environment.THE_END
            // 实时可传送半径：当前世界边界半径 - 16 格安全边距（未设边界时即坐标极限范围）
            val halfSize = (world.worldBorder.size / 2.0 - 16.0).coerceAtLeast(1.0)
            val borderMinX = world.worldBorder.center.x - halfSize
            val borderMaxX = world.worldBorder.center.x + halfSize
            val borderMinZ = world.worldBorder.center.z - halfSize
            val borderMaxZ = world.worldBorder.center.z + halfSize
            // 玩家当前位置与边界之间的实时最大可传送距离：随机范围据此收缩，落点永不越界
            val effLimitX = minOf(limitX.toDouble(), maxOf(1.0, minOf(centerX - borderMinX, borderMaxX - centerX)))
            val effLimitZ = minOf(limitZ.toDouble(), maxOf(1.0, minOf(centerZ - borderMinZ, borderMaxZ - centerZ)))
            val x = (centerX + (Math.random() * 2 - 1) * effLimitX).toInt()
            val z = (centerZ + (Math.random() * 2 - 1) * effLimitZ).toInt()
            world.getChunkAtAsync(x shr 4, z shr 4).thenAccept { _ ->
                var found: Location? = null
                if (!isScanningWorld) {
                    val y = world.getHighestBlockYAt(x, z)
                    // 虚空：整列为空或低于世界最低可用高度
                    if (y > world.minHeight && isSafeStanding(world, x, y, z)) {
                        found = Location(world, x + 0.5, y.toDouble(), z + 0.5, Math.random().toFloat() * 360f, 0f)
                    }
                } else {
                    // 下界/末地：同一 chunk 列内自低向高找首个安全落点（岩浆海等危险柱被 isSafeStanding 排除）
                    var y = world.minHeight + 1
                    while (y < world.maxHeight - 2) {
                        if (isSafeStanding(world, x, y, z)) {
                            found = Location(world, x + 0.5, y.toDouble(), z + 0.5, Math.random().toFloat() * 360f, 0f)
                            break
                        }
                        y++
                    }
                }
                if (found != null) {
                    onResult(found)
                } else {
                    // 本列不可用，异步尝试下一列
                    attemptRandomLocation(world, centerX, centerZ, limitX, limitZ, maxAttempts, remaining - 1, onResult)
                }
            }
        }
    }
}
