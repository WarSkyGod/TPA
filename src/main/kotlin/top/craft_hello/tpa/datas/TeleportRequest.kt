package top.craft_hello.tpa.datas

import cn.handyplus.lib.adapter.HandyRunnable
import cn.handyplus.lib.adapter.HandySchedulerUtil
import cn.handyplus.lib.adapter.PlayerSchedulerUtil
import kotlinx.coroutines.Delay
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil

data class TeleportRequest(val requester: Player, val requestType: RequestType, val target: Target, val targetName: String) {

    init {
        var config = ConfigManager.config
        when (requestType) {
            RequestType.TPA -> TODO()
            RequestType.TPA_HERE -> TODO()
            RequestType.WARP -> TODO()
            RequestType.HOME -> TODO()
            RequestType.SPAWN -> {
                //if (COMMAND_DELAY_QUEUE.containsKey(requestPlayer)) throw new ErrorCommandCooldownException(requestPlayer, COMMAND_DELAY_QUEUE.get(requestPlayer));
                if (!config.isEnableTeleportDelay(requester)) {
                    teleport()
                }
            }
            RequestType.BACK -> TODO()
            RequestType.RTP -> TODO()
            RequestType.TP_ALL -> TODO()
            RequestType.TP_LOGOUT -> TODO()
        }
    }

    fun teleport() {
        when (requestType) {
            RequestType.TPA -> TODO()
            RequestType.TPA_HERE -> TODO()
            RequestType.WARP -> TODO()
            RequestType.HOME -> TODO()
            RequestType.SPAWN -> {
                PlayerSchedulerUtil.syncTeleport(requester, target.location)
                SendMessageUtil.backSpawnSuccessMessage(requester)
            }
            RequestType.BACK -> TODO()
            RequestType.RTP -> TODO()
            RequestType.TP_ALL -> TODO()
            RequestType.TP_LOGOUT -> TODO()
        }
    }

    fun setTimer(){

    }

    companion object {
        val requestQueue = HashMap<Player, TeleportRequest>()
        val commandDelayQueue = HashMap<Player, String>()
    }
}