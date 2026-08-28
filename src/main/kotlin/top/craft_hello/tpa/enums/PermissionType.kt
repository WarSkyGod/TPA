package top.craft_hello.tpa.enums

import org.bukkit.command.CommandSender

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
            return sender.hasPermission(ADMIN.permissionName) || sender.hasPermission(permissionType.permissionName)
        }

        // 获取玩家所属的等级（用于家数量上限、传送延迟、命令间隔），从高到低逐级判断
        fun getLevel(sender: CommandSender): PermissionType {
            return when {
                hasPermission(sender, ADMIN) -> ADMIN
                hasPermission(sender, MVP_PLUS_PLUS) -> MVP_PLUS_PLUS
                hasPermission(sender, MVP_PLUS) -> MVP_PLUS
                hasPermission(sender, MVP) -> MVP
                hasPermission(sender, VIP_PLUS) -> VIP_PLUS
                hasPermission(sender, VIP) -> VIP
                else -> DEFAULT
            }
        }
    }
}
