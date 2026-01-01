package top.craft_hello.tpa.datas

import org.bukkit.configuration.file.FileConfiguration
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import kotlin.Boolean
import kotlin.Int

data class Config(var config: FileConfiguration) {
    var version = config.getString("version") ?: "1.0"
    var language = config.getString("language") ?: "zh_CN"
    var debug = config.getBoolean("debug") == true
    var updateCheck = config.getBoolean("update_check") != false
    var useDatabase = config.getBoolean("use_database") == true
    var databaseType = config.getString("database_type") ?: "sqlite"
    var databaseAddress = config.getString("database_address") ?: "localhost"
    var databasePort = config.getInt("database_port")
    var databaseName = config.getString("database_name") ?: "database"
    var databaseUsername = config.getString("database_username") ?: "root"
    var databasePassword = config.getString("database_password") ?: ""
    var forceSpawn = config.getBoolean("force_spawn") != false
    var enableTitleMessage = config.getBoolean("enable_title_message") != false
    var enableSound = config.getBoolean("enable_sound") != false
    var acceptDelay = config.getInt("delay.accept")
    var enableTeleportDelay = config.getBoolean("delay.enable_teleport") != false
    var enableCommandDelay = config.getBoolean("delay.enable_command") != false
    var nonTpaOrTphereDisableDelayCheck = config.getBoolean("delay.non_tpa_or_tphere_disable_check") == true
    var teleportDelays = mutableMapOf<PermissionType, Int>(
        PermissionType.DEFAULT to config.getInt("delay.default.teleport"),
        PermissionType.VIP to config.getInt("delay.vip.teleport"),
        PermissionType.VIP_PLUS to config.getInt("delay.vip+.teleport"),
        PermissionType.MVP to config.getInt("delay.mvp.teleport"),
        PermissionType.MVP_PLUS to config.getInt("delay.mvp+.teleport"),
        PermissionType.MVP_PLUS_PLUS to config.getInt("delay.mvp++.teleport"),
        PermissionType.ADMIN to config.getInt("delay.admin.teleport")
    )
    var enableCommands = mutableMapOf<CommandType, Boolean>(
        CommandType.TPA to (config.getBoolean("tpa.enable") != false),
        CommandType.TP_HERE to (config.getBoolean("tphere.enable") != false),
        CommandType.DENYS to ((config.getBoolean("tpa.enable") != false) or (config.getBoolean("tphere.enable") != false)),
        CommandType.RTP to (config.getBoolean("rtp.enable") != false),
        CommandType.WARP to (config.getBoolean("warp.enable") != false),
        CommandType.SET_WARP to (config.getBoolean("warp.enable") != false),
        CommandType.DEL_WARP to (config.getBoolean("warp.enable") != false),
        CommandType.HOME to (config.getBoolean("home.enable") != false),
        CommandType.HOMES to (config.getBoolean("home.enable") != false),
        CommandType.SET_HOME to (config.getBoolean("home.enable") != false),
        CommandType.SET_DEFAULT_HOME to (config.getBoolean("home.enable") != false),
        CommandType.DEL_HOME to (config.getBoolean("home.enable") != false),
        CommandType.SPAWN to (config.getBoolean("spawn.enable") != false),
        CommandType.SET_SPAWN to (config.getBoolean("spawn.enable") != false),
        CommandType.DEL_SPAWN to (config.getBoolean("spawn.enable") != false),
        CommandType.BACK to (config.getBoolean("back.enable") != false)
    )
    var enablePermissions = mutableMapOf<CommandType, Boolean>(
        CommandType.TPA to (config.getBoolean("tpa.permission") == true),
        CommandType.TP_HERE to (config.getBoolean("tphere.permission") == true),
        CommandType.DENYS to (config.getBoolean("denys.permission") == true),
        CommandType.RTP to (config.getBoolean("rtp.permission") == true),
        CommandType.WARP to (config.getBoolean("warp.permission") == true),
        CommandType.HOME to (config.getBoolean("home.permission") == true),
        CommandType.HOMES to (config.getBoolean("home.permission") == true),
        CommandType.SET_HOME to (config.getBoolean("home.permission") == true),
        CommandType.SET_DEFAULT_HOME to (config.getBoolean("home.permission") == true),
        CommandType.DEL_HOME to (config.getBoolean("home.permission") == true),
        CommandType.SPAWN to (config.getBoolean("spawn.permission") == true),
        CommandType.BACK to (config.getBoolean("back.permission") == true)
    )
    var homeAmounts = mutableMapOf<PermissionType, Int>(
        PermissionType.DEFAULT to config.getInt("home.amount.default"),
        PermissionType.VIP to config.getInt("home.amount.vip"),
        PermissionType.VIP_PLUS to config.getInt("home.amount.vip+"),
        PermissionType.MVP to config.getInt("home.amount.mvp"),
        PermissionType.MVP_PLUS to config.getInt("home.amount.mvp+"),
        PermissionType.MVP_PLUS_PLUS to config.getInt("home.amount.mvp++"),
        PermissionType.ADMIN to config.getInt("home.amount.admin"),
    )
    var rtpDisableWorlds = config.getStringList("rtp.disable_worlds")
    var rtpLimitX = config.getInt("rtp.limit.x")
    var rtpLimitZ = config.getInt("rtp.limit.z")
}
