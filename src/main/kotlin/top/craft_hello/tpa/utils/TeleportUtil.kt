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

    // 立即传送（Folia 调度器包装）
    fun teleport(player: Player, location: Location) {
        EntitySchedulerUtil.syncTeleport(player, location)
    }

    /**
     * 延迟传送：发送倒计时消息，按玩家等级延迟传送；期间移动（方块坐标变化）则取消。
     * @param targetName 显示名（可传语言键 last_location / rtp_name / spawn_name）
     * @param onComplete 传送完成回调
     * @param onCancel 因移动取消的回调
     */
    fun delayTeleport(player: Player, location: Location, targetName: String, onComplete: () -> Unit, onCancel: () -> Unit) {
        val delay = ConfigManager.config.getTeleportDelay(player)
        if (delay <= 0) {
            onComplete()
            return
        }
        SendMessageUtil.teleportCountdown(player, targetName, delay.toString())
        if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownMessage(player, targetName, delay.toString())
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
                    if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownOverMessage(player, targetName)
                    onComplete()
                    return
                }
                if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownMessage(player, targetName, remaining.toString())
            }
        }
        HandySchedulerUtil.runTaskTimerAsynchronously(timer, 20L, 20L)
    }
}
