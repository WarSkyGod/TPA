package top.craft_hello.tpa.utils

import cn.handyplus.lib.adapter.EntitySchedulerUtil
import cn.handyplus.lib.adapter.HandyRunnable
import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil

// 传送工具：立即传送（Folia 安全）与延迟传送（倒计时 + 移动检测）
object TeleportUtil {

    // 立即传送（对齐 3.x：异步调度到实体所属 region 执行，禁止在 join 等事件同步栈内
    // 直接 teleportAsync——placeNewPlayer 阶段玩家尚未完全进入 chunk loader，会触发
    // "Player is already removed from player chunk loader" 并导致 region tick 崩溃）
    // onDone：传送真正完成后回调（Folia 走 teleportAsync 完成future；跨 region 迁移
    // 已结束，此时再调度 playSound 等玩家实体任务不会被迁移窗口吞掉）
    fun teleport(player: Player, location: Location, onDone: () -> Unit = {}) {
        // 传送前必须解除两种会"吞掉"传送的实体状态，且只能在实体所属 region 线程做
        //（Folia 的 Moonrise TickThread 校验会拒绝跨线程读实体状态）：
        //  - 睡眠：CraftBukkit 的 ServerPlayer#teleport(TeleportTransition) 在
        //    isSleeping() 时直接 return null（SPIGOT-3154），teleportAsync 因此
        //    静默失败，玩家看到"已接受/已传送"提示却原地不动；
        //  - 骑乘：乘客坐标每 tick 由载具的 positionRider 覆写，传送即使执行成功也会
        //    下一 tick 被拉回载具，表现出来同样是"传送结束但人没动"。
        EntitySchedulerUtil.runSafeOnEntityScheduler(player, Runnable {
            // 单步清理失败（例如低版本没有对应 API）不能阻断后续传送
            runCatching { prepareForTeleport(player) }
            val blocked = runCatching { player.isSleeping || player.isInsideVehicle }.getOrDefault(false)
            if (blocked) {
                // 状态没能清掉（例如 PlayerBedLeaveEvent 被别的插件取消、下车失败）：
                // 异步路径必然被服务端拒绝，只能在 region 线程上同步搏一次
                teleportSync(player, location, onDone)
            } else {
                // 状态已经干净，走异步传送：Folia 只有 teleportAsync 能完成跨 region 的
                // 归属迁移，同步的 snapTo 只改坐标、不会把玩家搬到新的 region
                doTeleport(player, location, onDone)
            }
        }, true)
    }

    // 同步传送：调用方必须已经处在实体的 region 线程上（Folia 会校验）。
    // 同步路径失败（例如目标跨 region）时退回异步路径，由后者再决定是否降级。
    private fun teleportSync(player: Player, location: Location, onDone: () -> Unit) {
        val ok = runCatching { player.teleport(location) }.getOrDefault(false)
        if (ok) {
            // 同世界传送在 CraftEntity 里走的是 snapTo，不会解除睡眠状态：
            // 位置已经落到目标，但玩家仍被标记为 sleeping，客户端视角还停在床上。
            // 这里补一次状态清理。
            if (runCatching { player.isSleeping }.getOrDefault(false)) forceStopSleeping(player)
            // 跨世界 + 唤醒失败时 CraftEntity.teleport 会 return true 却根本没移动，
            // 必须校验落点，别让这种失败静默
            if (arrived(player, location)) {
                clearFallDistance(player)
                onDone()
                return
            }
            warnTeleportMissed(player, location)
        }
        // 没落到目标：退回异步路径（Folia 的 cross-region 迁移只有 teleportAsync 能做）
        doTeleport(player, location, onDone)
    }

    private fun arrived(player: Player, location: Location): Boolean = runCatching {
        val now = player.location
        now.world?.name == location.world?.name && now.distanceSquared(location) < 4.0
    }.getOrDefault(false)

    // 记录"传送调用返回成功但玩家没动"的情况，便于区分插件自身的失败与
    // 其他插件取消 PlayerTeleportEvent / 拦截 PlayerBedLeaveEvent 导致的中断
    private fun warnTeleportMissed(player: Player, location: Location) {
        runCatching {
            TPA.plugin.logger.warning(
                "[TPA] 传送未生效: ${player.name} sleeping=${player.isSleeping} " +
                    "riding=${player.isInsideVehicle} 目标世界=${location.world?.name}"
            )
        }
    }

    // 解除会干扰传送的实体状态。这两个方法都不在 1.8 编译期 API 里，只能反射：
    //  - Player#wakeup()（1.11.2+）：更低的版本没有 teleportAsync，同步 teleportTo
    //    内部本就会 stopSleepInBed
    //  - Entity#leaveVehicle()（1.11+）：这里只把自己放下车；不能用 eject()，
    //    它会连坐骑上的其他乘客一起弹出去
    // 反射失败不阻断流程，返回值表示"原本是否处于需要特殊处理的姿态"。
    private fun prepareForTeleport(player: Player): Boolean {
        var handled = false
        if (runCatching { player.isSleeping }.getOrDefault(false)) {
            handled = true
            runCatching { player.javaClass.getMethod("wakeup").invoke(player) }
            // wakeup() 未必一次就成：它内部走 stopSleepInBed，可能被别的插件取消
            // PlayerBedLeaveEvent 而提前 return。再走一次 NMS 兜底。
            if (runCatching { player.isSleeping }.getOrDefault(false)) forceStopSleeping(player)
        }
        if (runCatching { player.isInsideVehicle }.getOrDefault(false)) {
            handled = true
            runCatching { player.javaClass.getMethod("leaveVehicle").invoke(player) }
        }
        return handled
    }

    // 直接调用 NMS 的 stopSleepInBed(boolean, boolean)：1.13–1.21 方法名与签名稳定，
    // 1.8–1.12 不存在（那些版本也没有 teleportAsync），失败即忽略。
    // 必须保证玩家真的醒了 —— 跨世界传送走的是 ServerPlayer#teleport(TeleportTransition)，
    // 它在 isSleeping() 时直接 return null，而 CraftEntity.teleport 对此仍然返回 true，
    // 于是表现为"提示传送成功但人没动"。
    private fun forceStopSleeping(player: Player) {
        runCatching {
            val handle = player.javaClass.getMethod("getHandle").invoke(player)
            handle.javaClass
                .getMethod("stopSleepInBed", java.lang.Boolean.TYPE, java.lang.Boolean.TYPE)
                .invoke(handle, true, true)
        }
    }

    private fun doTeleport(player: Player, location: Location, onDone: () -> Unit) {
        HandySchedulerUtil.runTaskAsynchronously {
            if (TpaVersion.supportsAsyncChunk) {
                // teleportAsync 为 Paper API（1.9+ 提供编译期签名）：
                // 反射调用兼容 1.8 编译兜底（Player 接口解析到 1.8 版，直接引用会编译失败）
                val future = runCatching {
                    player.javaClass
                        .getMethod("teleportAsync", Location::class.java)
                        .invoke(player, location) as? java.util.concurrent.CompletableFuture<Boolean>
                }.getOrNull()
                if (future != null) {
                    future.thenAccept { result ->
                        if (result == true) {
                            clearFallDistance(player)
                            onDone()
                            return@thenAccept
                        }
                        // 异步传送被服务端拒绝（状态尚未解除干净，或被其他插件的
                        // PlayerTeleportEvent 取消）：退化为一次同步传送再试，
                        // 避免"倒计时结束却原地不动"的静默失败
                        EntitySchedulerUtil.runSafeOnEntityScheduler(player, Runnable {
                            EntitySchedulerUtil.syncTeleport(player, location)
                            clearFallDistance(player)
                            onDone()
                        }, true)
                    }
                    return@runTaskAsynchronously
                }
            }
            // Folia 之外的普通服务器（含 1.8.8）：同步传送
            EntitySchedulerUtil.syncTeleport(player, location)
            clearFallDistance(player)
            onDone()
        }
    }

    // issue #62 配套：落点可能悬空（目标是站在快乐恶魂等飞行实体上的玩家），
    // 传送后清零坠落距离，避免把传送前累积的下落量叠加到新位置一并结算
    private fun clearFallDistance(player: Player) {
        EntitySchedulerUtil.runSafeOnEntityScheduler(player, Runnable { player.fallDistance = 0f }, true)
    }

    /**
     * 延迟传送：发送倒计时消息，按玩家等级延迟传送；期间移动（方块坐标变化）则取消。
     * @param targetName 显示名（可传语言键 last_location / rtp_name / spawn_name）
     * @param onComplete 传送完成回调
     * @param onCancel 因移动取消的回调
     */
    fun delayTeleport(player: Player, location: Location, targetName: String, onComplete: () -> Unit, onCancel: () -> Unit) {
        val delay = ConfigManager.config.getTeleportDelay(player)
        // "已传送至 xx"的完成标题由传送执行点统一发送（finishTeleport / delayedTeleportTo），
        // 保证 0 秒玩家（管理员/无延迟权限）同样显示且不与倒计时结束段重复
        if (delay <= 0) {
            onComplete()
            return
        }
        SendMessageUtil.teleportCountdown(player, targetName, delay.toString())
        if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownMessage(player, targetName, delay.toString())
        SendMessageUtil.playTeleportCountdownSound(player)
        var lastX = player.location.blockX
        var lastY = player.location.blockY
        var lastZ = player.location.blockZ
        var remaining = delay
        val timer = object : HandyRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancel()
                    onCancel()
                    return
                }
                // 骑在载具上的玩家坐标每 tick 由载具的 positionRider 覆写，位置一直在变，
                // 这不是玩家主动走动；若照常比对会被误判为移动而取消传送（表现为倒计时
                // 走完却什么都没发生）。骑乘期间只同步基准点，不判定移动。
                val riding = runCatching { player.isInsideVehicle }.getOrDefault(false)
                val now = player.location
                if (riding) {
                    lastX = now.blockX
                    lastY = now.blockY
                    lastZ = now.blockZ
                } else if (now.blockX != lastX || now.blockY != lastY || now.blockZ != lastZ) {
                    cancel()
                    onCancel()
                    return
                }
                remaining--
                if (remaining <= 0) {
                    cancel()
                    onComplete()
                    return
                }
                // issue #58：聊天栏倒计时此前只在开始时发一次，每秒同步刷新剩余秒数
                //（title 与音效原本就在此处按秒刷新）
                SendMessageUtil.teleportCountdownUpdate(player, targetName, remaining.toString())
                if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownMessage(player, targetName, remaining.toString())
                SendMessageUtil.playTeleportCountdownSound(player)
            }
        }
        HandySchedulerUtil.runTaskTimerAsynchronously(timer, 20L, 20L)
    }
}
