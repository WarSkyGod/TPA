package top.craft_hello.tpa.datas

import cn.handyplus.lib.adapter.HandyRunnable
import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.objects.TeleportCost
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.TeleportUtil
import top.craft_hello.tpa.utils.TpaVersion
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
            // 传送费用发起预检：余额不足直接报错，不进入请求流程
            if (!TeleportCost.precheck(requester, "tpa")) return false
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
            // 传送费用发起预检：发起者付费
            if (!TeleportCost.precheck(requester, "tphere")) return false
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
                RequestType.TPA -> delayedTeleportTo(mover = requester, destination = accepter.location, opponent = accepter, costPayer = requester, costKey = "tpa")
                RequestType.TPA_HERE -> delayedTeleportTo(mover = accepter, destination = requester.location, opponent = requester, costPayer = requester, costKey = "tphere")
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
            // 传送费用发起预检：余额不足直接报错，不进入请求流程
            val costKey = TeleportCost.costKeyOf(requestType)
            if (costKey != null && !TeleportCost.precheck(sender, costKey)) return false
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
            // 传送费用发起预检
            if (!TeleportCost.precheck(sender, "rtp")) return false
            val world = sender.world
            if (ConfigManager.config.isRtpDisableWorld(world)) {
                SendMessageUtil.worldDisabledError(sender)
                return false
            }
            SendMessageUtil.generateRandomLocationMessage(sender)
            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleGenerateRandomLocationMessage(sender)
            val attempts = ConfigManager.config.rtpGenerateAttempts
            // 随机中心可配置：玩家当前位置（默认，对齐 3.x）或世界出生点
            val center = if (ConfigManager.config.rtpCenterOnPlayer) sender.location else world.spawnLocation
            attemptRandomLocation(world, center.x, center.z, ConfigManager.config.rtpLimitX, ConfigManager.config.rtpLimitZ, attempts, attempts) { location ->
                if (!sender.isOnline) return@attemptRandomLocation
                if (location == null) {
                    SendMessageUtil.playTeleportFailSound(sender)
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

        // 玩家对玩家的延迟传送（tpaccept 后）：移动者倒计时，移动取消则通知双方。
        // costPayer/costKey：传送执行前扣费的付费人与费用键（发起者付费，mover 可能≠付费人）
        private fun delayedTeleportTo(mover: Player, destination: Location, opponent: Player, costPayer: Player?, costKey: String?) {
            val opponentName = opponent.name
            if (ConfigManager.config.isEnableTeleportDelay(mover)) {
                TeleportUtil.delayTeleport(
                    mover, destination, opponentName,
                    onComplete = {
                        // 倒计时结束：先扣费，扣费失败则取消传送（付费人与移动者不是同一人时分别提示）
                        if (costPayer == null || costKey == null || TeleportCost.charge(costPayer, costKey)) {
                            TeleportUtil.teleport(mover, destination) { SendMessageUtil.playTeleportSuccessSound(mover) }
                            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownOverMessage(mover, opponentName)
                            startCommandDelay(mover)
                        } else if (mover !== costPayer) {
                            SendMessageUtil.costTeleportBlockedError(mover, costPayer.name)
                        }
                    },
                    onCancel = {
                        SendMessageUtil.playTeleportCancelSound(mover)
                        SendMessageUtil.move(mover, opponent)
                    }
                )
            } else {
                // 0 秒直达：传送前扣费，失败则取消传送
                if (costPayer != null && costKey != null && !TeleportCost.charge(costPayer, costKey)) {
                    if (mover !== costPayer) SendMessageUtil.costTeleportBlockedError(mover, costPayer.name)
                    return
                }
                TeleportUtil.teleport(mover, destination) { SendMessageUtil.playTeleportSuccessSound(mover) }
                if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownOverMessage(mover, opponentName)
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
                    SendMessageUtil.playTeleportCancelSound(sender)
                    SendMessageUtil.move(sender, null)
                }
            )
        }

        // 完成传送：出队 + 传送 + 成功消息 + 命令冷却（"已传送至 xx"标题在此统一发送，
        // 覆盖 0 秒直达与倒计时结束两条路径）。传送前扣费，失败则请求作废不传送。
        private fun finishTeleport(request: TeleportRequest, location: Location) {
            val sender = request.requester
            requestQueue.remove(sender.uniqueId)
            if (!sender.isOnline) return
            val costKey = TeleportCost.costKeyOf(request.requestType)
            if (costKey != null && !TeleportCost.charge(sender, costKey)) return
            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownOverMessage(sender, request.targetName)
            val successMessage: (Player, String) -> Unit = when (request.requestType) {
                RequestType.WARP -> SendMessageUtil::tpToWarpMessage
                RequestType.HOME -> SendMessageUtil::tpToHomeMessage
                RequestType.SPAWN -> { player, _ -> SendMessageUtil.backSpawnSuccessMessage(player) }
                RequestType.BACK -> { player, _ -> SendMessageUtil.backLastLocationSuccessMessage(player) }
                RequestType.RTP -> { player, _ -> SendMessageUtil.rtpSuccessMessage(player) }
                RequestType.TP_LOGOUT -> { player, name -> SendMessageUtil.tpLogoutCommandSuccess(player, name) }
                else -> SendMessageUtil::youTeleportedToMessage
            }
            TeleportUtil.teleport(sender, location) { SendMessageUtil.playTeleportSuccessSound(sender) }
            successMessage(sender, request.targetName)
            startCommandDelay(sender)
        }

        // Minecraft chunk 系统方块坐标硬极限与安全边距
        private const val WORLD_MAX_COORD = 29_999_984.0
        private const val WORLD_SAFE_MARGIN = 16.0
        private const val WORLD_SAFE_LIMIT = WORLD_MAX_COORD - WORLD_SAFE_MARGIN
        private const val WORLD_SAFE_LIMIT_INT = 29_999_968

        // 黑名单方块由配置 rtp.blacklisted_blocks 提供（默认排除岩浆/水/火/灵魂火/岩浆块）
        // y 为脚部层：ground(y-1) 必须为实体且不在黑名单，feet(y)/head(y+1) 必须可穿越且不在黑名单
        // 方块实体判定走 Material.isSolid（Block.isSolid 为新版本 API，1.8 不可用）
        private fun isSafeStanding(world: World, x: Int, y: Int, z: Int): Boolean {
            val blacklisted = ConfigManager.config.rtpBlacklistedBlocks
            val ground = world.getBlockAt(x, y - 1, z)
            val feet = world.getBlockAt(x, y, z)
            val head = world.getBlockAt(x, y + 1, z)
            if (!ground.type.isSolid || ground.type in blacklisted) return false
            if (feet.type.isSolid || feet.type in blacklisted) return false
            if (head.type.isSolid || head.type in blacklisted) return false
            return true
        }

        // 寻找随机传送点（异步链，Folia 安全）：
        // - 以玩家当前位置为中心 ±limit，随机范围实时收缩到玩家位置与世界边界/硬极限的可达距离内
        // - Minecraft chunk 系统硬极限（±29,999,984 方块）是最终兜底：world border 与
        //   max-world-size 只能收紧范围、永远不能放宽（border 尺寸被配置得异常巨大时防崩）
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
            // 硬极限可达距离：玩家位置到 ±29,999,968（硬极限 - 16 格安全边距）的距离
            val hardLimitX = maxOf(1.0, minOf(WORLD_SAFE_LIMIT - centerX, centerX + WORLD_SAFE_LIMIT))
            val hardLimitZ = maxOf(1.0, minOf(WORLD_SAFE_LIMIT - centerZ, centerZ + WORLD_SAFE_LIMIT))
            // 世界边界可达距离（border 尺寸异常巨大时必然大于硬极限层，由硬极限兜住）
            val halfSize = (world.worldBorder.size / 2.0 - WORLD_SAFE_MARGIN).coerceAtLeast(1.0)
            val borderLimitX = maxOf(1.0, minOf(world.worldBorder.center.x + halfSize - centerX, centerX - (world.worldBorder.center.x - halfSize)))
            val borderLimitZ = maxOf(1.0, minOf(world.worldBorder.center.z + halfSize - centerZ, centerZ - (world.worldBorder.center.z - halfSize)))
            // 实际随机范围 = min(配置 limit, border 可达距离, 硬极限可达距离)
            val effLimitX = minOf(limitX.toDouble(), borderLimitX, hardLimitX)
            val effLimitZ = minOf(limitZ.toDouble(), borderLimitZ, hardLimitZ)
            // 硬夹双保险：无论上层收缩如何失效，落点物理上不可能超过 chunk 系统合法范围
            val x = (centerX + (Math.random() * 2 - 1) * effLimitX).toInt().coerceIn(-WORLD_SAFE_LIMIT_INT, WORLD_SAFE_LIMIT_INT)
            val z = (centerZ + (Math.random() * 2 - 1) * effLimitZ).toInt().coerceIn(-WORLD_SAFE_LIMIT_INT, WORLD_SAFE_LIMIT_INT)
            // 末地虚空列占比极高（1.8-1.15 仅小主岛、无外岛，大范围随机点几乎必然落空），
            // 重试时随机范围以玩家为中心逐次减半，让后续尝试收敛到有地形的区域；
            // 下界地形连续、主世界取地表最高点，均不收缩
            val isEndWorld = world.environment == World.Environment.THE_END
            val nextLimitX = if (isEndWorld) maxOf(16, limitX / 2) else limitX
            val nextLimitZ = if (isEndWorld) maxOf(16, limitZ / 2) else limitZ
            if (TpaVersion.supportsAsyncChunk) {
                // 必须先异步加载目标列所在 chunk（Folia 禁止跨 region 同步取 chunk；
                // World.getChunkAtAsync 为 Paper 1.13+ API，反射调用兼容 1.8 编译兜底）
                val future = runCatching {
                    world.javaClass
                        .getMethod("getChunkAtAsync", Integer.TYPE, Integer.TYPE)
                        .invoke(world, x shr 4, z shr 4) as? java.util.concurrent.CompletableFuture<*>
                }.getOrNull()
                if (future != null) {
                    future.thenAccept { _ ->
                        scanColumn(world, x, z, isScanningWorld, onResult) {
                            attemptRandomLocation(world, centerX, centerZ, nextLimitX, nextLimitZ, maxAttempts, remaining - 1, onResult)
                        }
                    }
                    return
                }
            }
            // 1.8-1.12 无 getChunkAtAsync：调度回主线程同步扫描（同步取 chunk 自动加载）
            HandySchedulerUtil.runTask {
                scanColumn(world, x, z, isScanningWorld, onResult) {
                    attemptRandomLocation(world, centerX, centerZ, nextLimitX, nextLimitZ, maxAttempts, remaining - 1, onResult)
                }
            }
        }

        // 扫描单个 chunk 列：主世界取地表最高点，下界/末地扫描首个安全柱；不可用则走 retry 尝试下一列
        private fun scanColumn(world: World, x: Int, z: Int, isScanningWorld: Boolean, onResult: (Location?) -> Unit, retry: () -> Unit) {
            var found: Location? = null
            val minHeight = worldMinHeight(world)
            if (!isScanningWorld) {
                // getHighestBlockYAt 的 y 语义跨版本不同：1.8-1.12 返回 heightMap 值（最高
                // 方块上方一格，即站立层）；1.13+ 返回最高方块自身 y（站立层需再 +1）。
                // 两个候选层各探测一次、任一安全即用：现代语义下第一候选即中，
                // 1.8 语义下按现代取层会把站立层当地面判层（ground=空气）导致全部失败
                val scanY = world.getHighestBlockYAt(x, z)
                for (y in intArrayOf(scanY + 1, scanY)) {
                    if (y > minHeight && isSafeStanding(world, x, y, z)) {
                        found = Location(world, x + 0.5, y.toDouble(), z + 0.5, Math.random().toFloat() * 360f, 0f)
                        break
                    }
                }
            } else {
                // 下界/末地：同一 chunk 列内自低向高找首个安全落点（岩浆海等危险柱被 isSafeStanding 排除）
                var y = minHeight + 1
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
                retry()
            }
        }

        // 世界下限：1.17+ 用世界真实下限（反射，World.getMinHeight 为 1.17+ API），
        // 旧版本恒为 0
        private fun worldMinHeight(world: World): Int {
            if (!TpaVersion.supportsWorldMinHeight) return 0
            return runCatching {
                world.javaClass.getMethod("getMinHeight").invoke(world) as? Int
            }.getOrNull() ?: 0
        }
    }
}
