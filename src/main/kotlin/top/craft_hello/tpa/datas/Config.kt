package top.craft_hello.tpa.datas

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import java.util.Locale

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
    // 传送音效组：倒计时/成功/失败/取消（非法名称回退默认）
    var soundCountdown: Sound = loadSound("sound.countdown.name", "ENTITY_EXPERIENCE_ORB_PICKUP")
    var soundCountdownVolume = config.getDouble("sound.countdown.volume", 1.0).toFloat()
    var soundCountdownPitch = config.getDouble("sound.countdown.pitch", 1.0).toFloat()
    var soundSuccess: Sound = loadSound("sound.success.name", "ENTITY_ENDERMAN_TELEPORT")
    var soundSuccessVolume = config.getDouble("sound.success.volume", 1.0).toFloat()
    var soundSuccessPitch = config.getDouble("sound.success.pitch", 1.0).toFloat()
    var soundFail: Sound = loadSound("sound.fail.name", "ENTITY_VILLAGER_NO")
    var soundFailVolume = config.getDouble("sound.fail.volume", 1.0).toFloat()
    var soundFailPitch = config.getDouble("sound.fail.pitch", 1.0).toFloat()
    var soundCancel: Sound = loadSound("sound.cancel.name", "BLOCK_NOTE_BLOCK_BASS")
    var soundCancelVolume = config.getDouble("sound.cancel.volume", 1.0).toFloat()
    var soundCancelPitch = config.getDouble("sound.cancel.pitch", 1.0).toFloat()
    var acceptDelay = config.getInt("delay.accept").coerceAtLeast(3)
    var enableTeleportDelay = config.getBoolean("delay.enable_teleport")
    var enableCommandDelay = config.getBoolean("delay.enable_command")
    var nonTpaOrTphereDisableCheck = config.getBoolean("delay.non_tpa_or_tphere_disable_check")
    var teleportDelays = mutableMapOf(
        PermissionType.DEFAULT to config.getInt("delay.default.teleport"),
        PermissionType.VIP to config.getInt("delay.vip.teleport"),
        PermissionType.VIP_PLUS to config.getInt("delay.vip+.teleport"),
        PermissionType.MVP to config.getInt("delay.mvp.teleport"),
        PermissionType.MVP_PLUS to config.getInt("delay.mvp+.teleport"),
        PermissionType.MVP_PLUS_PLUS to config.getInt("delay.mvp++.teleport"),
        PermissionType.ADMIN to config.getInt("delay.admin.teleport")
    )
    var commandDelays = mutableMapOf(
        PermissionType.DEFAULT to config.getInt("delay.default.command"),
        PermissionType.VIP to config.getInt("delay.vip.command"),
        PermissionType.VIP_PLUS to config.getInt("delay.vip+.command"),
        PermissionType.MVP to config.getInt("delay.mvp.command"),
        PermissionType.MVP_PLUS to config.getInt("delay.mvp+.command"),
        PermissionType.MVP_PLUS_PLUS to config.getInt("delay.mvp++.command"),
        PermissionType.ADMIN to config.getInt("delay.admin.command")
    )
    var enableCommands = mutableMapOf(
        CommandType.TPA to (config.getBoolean("tpa.enable")),
        CommandType.TP_HERE to (config.getBoolean("tphere.enable")),
        CommandType.TP_ACCEPT to (config.getBoolean("tpa.enable") or config.getBoolean("tphere.enable")),
        CommandType.TP_DENY to (config.getBoolean("tpa.enable") or config.getBoolean("tphere.enable")),
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
    // 旧配置文件缺失此键时回退默认 5 次
    var rtpGenerateAttempts = config.getInt("rtp.generate_attempts", 5).coerceAtLeast(1)
    // 黑名单方块列表：非法名称忽略；旧配置缺失本项时使用默认危险方块规则；显式空列表 = 关闭黑名单
    var rtpBlacklistedBlocks: Set<Material> = if (config.contains("rtp.blacklisted_blocks")) {
        config.getStringList("rtp.blacklisted_blocks")
            .mapNotNull { name -> runCatching { Material.valueOf(name.uppercase(Locale.ROOT)) }.getOrNull() }
            .toSet()
    } else {
        DEFAULT_RTP_BLACKLIST
    }
    // 随机传送中心：true=玩家当前位置（默认），false=世界出生点
    var rtpCenterOnPlayer = config.getBoolean("rtp.center_on_player", true)

    companion object {
        // rtp 黑名单方块默认规则（与 config.yml 默认项保持一致）：
        // 站立/触碰会受伤、产生负面效果、误触发传送或被困的方块
        val DEFAULT_RTP_BLACKLIST = setOf(
            Material.LAVA,              // 岩浆：烧灼
            Material.WATER,             // 水：溺水/湿身
            Material.FIRE,              // 火：烧灼
            Material.SOUL_FIRE,         // 灵魂火：烧灼
            Material.MAGMA_BLOCK,       // 岩浆块：站立烧灼
            Material.BEDROCK,           // 基岩：基岩层上方无可站立空间
            Material.CACTUS,            // 仙人掌：站立扎伤
            Material.CAMPFIRE,          // 营火：站立烧灼
            Material.SOUL_CAMPFIRE,     // 灵魂营火：站立烧灼
            Material.POINTED_DRIPSTONE, // 滴水石锥：坠落刺伤
            Material.POWDER_SNOW,       // 细雪：陷入冻伤
            Material.SWEET_BERRY_BUSH,  // 甜浆果丛：穿行扎伤
            Material.WITHER_ROSE,       // 凋零玫瑰：凋零效果
            Material.NETHER_PORTAL,     // 下界传送门：误触发维度传送
            Material.END_PORTAL,        // 末地传送门：误触发维度传送
            Material.END_GATEWAY,       // 末地折跃门：误触发维度传送
            Material.BUBBLE_COLUMN,     // 气泡柱：卷入水柱
            Material.COBWEB,            // 蜘蛛网：被困
        )
    }


    // 读取配置中的音效名称，非法或缺失回退默认
    private fun loadSound(path: String, defaultName: String): Sound =
        runCatching { Sound.valueOf(config.getString(path, defaultName)!!.uppercase(Locale.ROOT)) }.getOrDefault(Sound.valueOf(defaultName))

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
        return enableTeleportDelay and !PermissionType.hasPermission(sender, PermissionType.NO_DELAY) and (getTeleportDelay(sender) != 0)
    }

    fun isEnableCommandDelay(sender: CommandSender) : Boolean {
        return enableCommandDelay and !PermissionType.hasPermission(sender, PermissionType.NO_DELAY) and (getCommandDelay(sender) != 0)
    }

    fun isNonTpaOrTphereDisableCheck(): Boolean {
        return nonTpaOrTphereDisableCheck
    }

    fun isRtpDisableWorld(world: World): Boolean {
        for (worldName in rtpDisableWorlds) if (worldName.equals(world.name, ignoreCase = true)) return true
        return false
    }


    // 玩家所属等级的传送前等待时间（秒）
    fun getTeleportDelay(sender: CommandSender): Int {
        var teleportDelay: Int = teleportDelays[PermissionType.DEFAULT] ?: 5
        if (PermissionType.hasPermission(sender, PermissionType.VIP)) teleportDelay = teleportDelays[PermissionType.VIP] ?: teleportDelay
        if (PermissionType.hasPermission(sender, PermissionType.VIP_PLUS)) teleportDelay = teleportDelays[PermissionType.VIP_PLUS] ?: teleportDelay
        if (PermissionType.hasPermission(sender, PermissionType.MVP)) teleportDelay = teleportDelays[PermissionType.MVP] ?: teleportDelay
        if (PermissionType.hasPermission(sender, PermissionType.MVP_PLUS)) teleportDelay = teleportDelays[PermissionType.MVP_PLUS] ?: teleportDelay
        if (PermissionType.hasPermission(sender, PermissionType.MVP_PLUS_PLUS)) teleportDelay = teleportDelays[PermissionType.MVP_PLUS_PLUS] ?: teleportDelay
        if (PermissionType.hasPermission(sender, PermissionType.ADMIN)) teleportDelay = teleportDelays[PermissionType.ADMIN] ?: teleportDelay
        return teleportDelay
    }

    // 玩家所属等级的命令执行间隔（秒）
    fun getCommandDelay(sender: CommandSender): Int {
        var commandDelay: Int = commandDelays[PermissionType.DEFAULT] ?: 30
        if (PermissionType.hasPermission(sender, PermissionType.VIP)) commandDelay = commandDelays[PermissionType.VIP] ?: commandDelay
        if (PermissionType.hasPermission(sender, PermissionType.VIP_PLUS)) commandDelay = commandDelays[PermissionType.VIP_PLUS] ?: commandDelay
        if (PermissionType.hasPermission(sender, PermissionType.MVP)) commandDelay = commandDelays[PermissionType.MVP] ?: commandDelay
        if (PermissionType.hasPermission(sender, PermissionType.MVP_PLUS)) commandDelay = commandDelays[PermissionType.MVP_PLUS] ?: commandDelay
        if (PermissionType.hasPermission(sender, PermissionType.MVP_PLUS_PLUS)) commandDelay = commandDelays[PermissionType.MVP_PLUS_PLUS] ?: commandDelay
        if (PermissionType.hasPermission(sender, PermissionType.ADMIN)) commandDelay = commandDelays[PermissionType.ADMIN] ?: commandDelay
        return commandDelay
    }

    // 玩家所属等级的可设置家数量上限（-1 为不限制）
    fun homeAmountMax(sender: CommandSender): Int = homeAmountMax(PermissionType.getLevel(sender))

    fun homeAmountMax(permissionType: PermissionType): Int {
        return homeAmounts[permissionType] ?: -1
    }

    // 是否超出家的数量上限
    fun isHomeAmountExceeded(sender: CommandSender, currentAmount: Int): Boolean {
        val max = homeAmountMax(sender)
        return max != -1 && currentAmount >= max
    }
}
