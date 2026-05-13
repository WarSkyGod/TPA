package top.craft_hello.tpa.datas

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType


data class Config(var config: FileConfiguration) {
    var version = config.getString("version") ?: "1.0"
    var language = config.getString("language") ?: "zh_CN"
    var debug = config.getBoolean("debug")
    var updateCheck = config.getBoolean("update_check")
    var useDatabase = config.getBoolean("use_database")
    var databaseType = config.getString("database_type") ?: "sqlite"
    var databaseAddress = config.getString("database_address") ?: "localhost"
    var databasePort = config.getInt("database_port")
    var databaseName = config.getString("database_name") ?: "database"
    var databaseUsername = config.getString("database_username") ?: "root"
    var databasePassword = config.getString("database_password") ?: ""
    var forceSpawn = config.getBoolean("force_spawn")
    var enableTitleMessage = config.getBoolean("enable_title_message")
    var enableSound = config.getBoolean("enable_sound")
    var acceptDelay = config.getInt("delay.accept")
    var enableTeleportDelay = config.getBoolean("delay.enable_teleport")
    var enableCommandDelay = config.getBoolean("delay.enable_command")
    var nonTpaOrTphereDisableDelayCheck = config.getBoolean("delay.non_tpa_or_tphere_disable_check")
    var teleportDelays = mutableMapOf(
        PermissionType.DEFAULT to config.getInt("delay.default.teleport"),
        PermissionType.VIP to config.getInt("delay.vip.teleport"),
        PermissionType.VIP_PLUS to config.getInt("delay.vip+.teleport"),
        PermissionType.MVP to config.getInt("delay.mvp.teleport"),
        PermissionType.MVP_PLUS to config.getInt("delay.mvp+.teleport"),
        PermissionType.MVP_PLUS_PLUS to config.getInt("delay.mvp++.teleport"),
        PermissionType.ADMIN to config.getInt("delay.admin.teleport")
    )
    var enableCommands = mutableMapOf(
        CommandType.TPA to (config.getBoolean("tpa.enable")),
        CommandType.TP_HERE to (config.getBoolean("tphere.enable")),
        CommandType.DENYS to ((config.getBoolean("tpa.enable")) or (config.getBoolean("tphere.enable"))),
        CommandType.RTP to (config.getBoolean("rtp.enable")),
        CommandType.WARP to (config.getBoolean("warp.enable")),
        CommandType.SET_WARP to (config.getBoolean("warp.enable")),
        CommandType.DEL_WARP to (config.getBoolean("warp.enable")),
        CommandType.HOME to (config.getBoolean("home.enable")),
        CommandType.HOMES to (config.getBoolean("home.enable")),
        CommandType.SET_HOME to (config.getBoolean("home.enable")),
        CommandType.SET_DEFAULT_HOME to (config.getBoolean("home.enable")),
        CommandType.DEL_HOME to (config.getBoolean("home.enable")),
        CommandType.SPAWN to (config.getBoolean("spawn.enable")),
        CommandType.SET_SPAWN to (config.getBoolean("spawn.enable")),
        CommandType.DEL_SPAWN to (config.getBoolean("spawn.enable")),
        CommandType.BACK to (config.getBoolean("back.enable"))
    )
    var enablePermissions = mutableMapOf(
        PermissionType.TPA to (config.getBoolean("tpa.permission")),
        PermissionType.TP_HERE to (config.getBoolean("tphere.permission")),
        PermissionType.DENYS to (config.getBoolean("denys.permission")),
        PermissionType.RTP to (config.getBoolean("rtp.permission")),
        PermissionType.WARP to (config.getBoolean("warp.permission")),
        PermissionType.HOME to (config.getBoolean("home.permission")),
        PermissionType.HOMES to (config.getBoolean("home.permission")),
        PermissionType.SET_HOME to (config.getBoolean("home.permission")),
        PermissionType.SET_DEFAULT_HOME to (config.getBoolean("home.permission")),
        PermissionType.DEL_HOME to (config.getBoolean("home.permission")),
        PermissionType.SPAWN to (config.getBoolean("spawn.permission")),
        PermissionType.BACK to (config.getBoolean("back.permission"))
    )
    var homeAmounts = mutableMapOf(
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
        for (commandType in commandTypes) if ((enableCommands[commandType] ?: true)) return true
        return false
    }

    fun isEnablePermission(permissionType: PermissionType): Boolean {
        return (enablePermissions[permissionType] ?: true)
    }

    fun hasPermission(sender: CommandSender, permissionType: PermissionType): Boolean {
        return !isEnablePermission(permissionType) ||
                PermissionType.hasPermission(sender, PermissionType.ADMIN) ||
                PermissionType.hasPermission(sender, permissionType)
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
