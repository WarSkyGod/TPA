package top.craft_hello.tpa.utils

import cn.handyplus.lib.adapter.EntitySchedulerUtil
import cn.handyplus.lib.adapter.HandyRunnable
import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bukkit.Location
import org.bukkit.entity.Player
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
                    future.thenAccept { result -> if (result) onDone() }
                    return@runTaskAsynchronously
                }
            }
            // Folia 之外的普通服务器（含 1.8.8）：同步传送
            EntitySchedulerUtil.syncTeleport(player, location)
            onDone()
        }
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
                val now = player.location
                if (now.blockX != lastX || now.blockY != lastY || now.blockZ != lastZ) {
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
                if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownMessage(player, targetName, remaining.toString())
                SendMessageUtil.playTeleportCountdownSound(player)
            }
        }
        HandySchedulerUtil.runTaskTimerAsynchronously(timer, 20L, 20L)
    }
}
