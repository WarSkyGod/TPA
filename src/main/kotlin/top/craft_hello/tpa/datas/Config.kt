package top.craft_hello.tpa.datas

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import kotlin.math.max


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
    var enablePermissions = mutableMapOf<PermissionType, Boolean>(
        PermissionType.TPA to (config.getBoolean("tpa.permission") == true),
        PermissionType.TP_HERE to (config.getBoolean("tphere.permission") == true),
        PermissionType.DENYS to (config.getBoolean("denys.permission") == true),
        PermissionType.RTP to (config.getBoolean("rtp.permission") == true),
        PermissionType.WARP to (config.getBoolean("warp.permission") == true),
        PermissionType.HOME to (config.getBoolean("home.permission") == true),
        PermissionType.HOMES to (config.getBoolean("home.permission") == true),
        PermissionType.SET_HOME to (config.getBoolean("home.permission") == true),
        PermissionType.SET_DEFAULT_HOME to (config.getBoolean("home.permission") == true),
        PermissionType.DEL_HOME to (config.getBoolean("home.permission") == true),
        PermissionType.SPAWN to (config.getBoolean("spawn.permission") == true),
        PermissionType.BACK to (config.getBoolean("back.permission") == true)
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

    fun isEnableCommand(vararg commandTypes: CommandType): Boolean {
        for (commandType in commandTypes) {
            if (!enableCommands.containsKey(commandType) or (enableCommands[commandType] == true)) return true
        }
        return false
    }

    fun isEnablePermission(permissionType: PermissionType): Boolean {
        // if (ENABLE_PERMISSIONS.containsKey(permissionType)) return ENABLE_PERMISSIONS.get(permissionType);
        if (enablePermissions.containsKey(permissionType)) return enablePermissions[permissionType] == true
        return true
    }

    fun hasPermission(sender: CommandSender, permissionType: PermissionType): Boolean {
        return !isEnablePermission(permissionType) or PermissionType.hasPermission(sender, PermissionType.ADMIN) or PermissionType.hasPermission(sender, permissionType)
    }

    fun isEnableTeleportDelay(sender: CommandSender) : Boolean {
        return  enableTeleportDelay and !hasPermission(sender, PermissionType.NO_DELAY) and (getTeleportDelay(sender) != 0)
    }

    fun getTeleportDelay(sender: CommandSender): Int {
        var teleportDelay: Int = teleportDelays[PermissionType.DEFAULT] ?: 5
        if (hasPermission(sender, PermissionType.VIP)) teleportDelays[PermissionType.VIP] ?: 5
        if (hasPermission(sender, PermissionType.VIP_PLUS)) teleportDelay = teleportDelays[PermissionType.VIP_PLUS] ?: 5
        if (hasPermission(sender, PermissionType.MVP)) teleportDelay = teleportDelays[PermissionType.MVP] ?: 5
        if (hasPermission(sender, PermissionType.MVP_PLUS)) teleportDelay = teleportDelays[PermissionType.MVP_PLUS] ?: 5
        if (hasPermission(sender, PermissionType.MVP_PLUS_PLUS)) teleportDelays[PermissionType.MVP_PLUS_PLUS] ?: 5
        if (hasPermission(sender, PermissionType.ADMIN)) teleportDelay = teleportDelays[PermissionType.ADMIN] ?: 0
        return teleportDelay
    }
}
