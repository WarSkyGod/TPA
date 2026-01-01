package top.craft_hello.tpa.datas

import cn.handyplus.lib.adapter.HandyRunnable
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.RequestType

data class TeleportRequest(val requester: Player, val requestType: RequestType, val target: Target, val targetName: String, val timer: HandyRunnable?)