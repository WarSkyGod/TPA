package top.craft_hello.tpa.utils

import cn.handyplus.lib.adapter.EntitySchedulerUtil
import cn.handyplus.lib.adapter.HandyRunnable
import cn.handyplus.lib.adapter.HandySchedulerUtil
import cn.handyplus.lib.adapter.PlayerSchedulerUtil
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import top.craft_hello.tpa.objects.ConfigManager


class TeleportUtil {
    companion object {

        fun delayTimer(delay : Long, executor: Player, target: Player) {
            val delayTimer: HandyRunnable = object : HandyRunnable() {
                var sec: Long = delay
                override fun run() {
                    try {
                        if (sec > 0) {
                            SendMessageUtil.teleportCountdown(executor, target.name, sec.toString())
                        }
                        if (ConfigManager.config.enableTitleMessage) {
                            SendMessageUtil.titleCountdownMessage(executor, target.name, sec.toString())
                            if (ConfigManager.config.enableSound) PlayerSchedulerUtil.playSound(
                                executor,
                                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                1f,
                                1f
                            )
                        }
                        if (--sec < 0) {
                            //SendMessageUtil.titleCountdownOverMessage(executor, target)
                            this.cancel()
                        }
                    } catch (ignored: Exception) {
                        this.cancel()
                    }
                }
            }
            HandySchedulerUtil.runTaskTimerAsynchronously(delayTimer, 0, 20)
        }

        fun checkMoveTimer() {
            // val checkMoveTimer: HandyRunnable = object : HandyRunnable() {

            // }
            // HandySchedulerUtil.runTaskTimerAsynchronously(checkMoveTimer, 0, 1)
        }


        fun delayTeleport(executor: Player, target: Player, delay: Long, checkMove: Boolean = false) {

        }

        fun delayTeleport(executor: Player, target: Location, delay: Long, checkMove: Boolean = false) {

        }

        fun teleport(executor: Player, target: Player) {
            teleport(executor, target.location)
        }

        fun teleport(executor: Player, target: Location) {
            EntitySchedulerUtil.syncTeleport(executor, target)
        }
    }
}