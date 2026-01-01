package top.craft_hello.tpa.enums

enum class CommandType(val commandName: String) {
    TPA("tpa"),
    TP_HERE("tphere"),
    RTP("rtp"),
    TP_ALL("tpall"),
    TP_LOGOUT("tplogout"),
    TP_ACCEPT("tpaccept"),
    TP_DENY("tpdeny"),
    DENYS("denys"),
    WARP("warp"),
    SET_WARP("setwarp"),
    DEL_WARP("delwarp"),
    HOME("home"),
    HOMES("homes"),
    SET_HOME("sethome"),
    SET_DEFAULT_HOME("setdefaulthome"),
    DEL_HOME("delhome"),
    SPAWN("spawn"),
    SET_SPAWN("setspawn"),
    DEL_SPAWN("delspawn"),
    BACK("back"),
    RELOAD("reload"),
    SET_LANG("setlang"),
    VERSION("version")
}