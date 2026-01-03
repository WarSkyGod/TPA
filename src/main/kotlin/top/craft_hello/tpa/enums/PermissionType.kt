package top.craft_hello.tpa.enums

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

enum class PermissionType(val permissionName: String) {
    DEFAULT("tpa.default"),
    VIP("tpa.vip"),
    VIP_PLUS("tpa.vip+"),
    MVP("tpa.mvp"),
    MVP_PLUS("tpa.mvp+"),
    MVP_PLUS_PLUS("tpa.mvp++"),
    ADMIN("tpa.admin"),
    RELOAD("tpa.reload"),
    VERSION("tpa.version"),
    WARP("tpa.warp"),
    SET_WARP("tpa.setwarp"),
    DEL_WARP("tpa.delwarp"),
    HOME("tpa.home"),
    HOMES("tpa.home"),
    SET_HOME("tpa.home"),
    SET_DEFAULT_HOME("tpa.home"),
    DEL_HOME("tpa.home"),
    SPAWN("tpa.spawn"),
    SET_SPAWN("tpa.setspawn"),
    DEL_SPAWN("tpa.delspawn"),
    TPA("tpa.tpa"),
    TP_HERE("tpa.tphere"),
    TP_ALL("tpa.tpall"),
    TP_LOGOUT("tpa.tplogout"),
    RTP("tpa.rtp"),
    DENYS("tpa.denys"),
    BACK("tpa.back"),
    NO_DELAY("tpa.nodelay");

    companion object {
        fun hasPermission(sender: CommandSender, permissionType: PermissionType): Boolean {
            return sender.hasPermission(ADMIN.name) or sender.hasPermission(permissionType.name)
        }
    }
}