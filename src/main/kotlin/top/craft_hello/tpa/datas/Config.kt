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
    // 传送音效组：倒计时/成功/失败/取消（非法名称回退默认；默认名也不被当前服务器
    // 支持时（如 1.8.x 无 ENTITY_ 前缀新命名）返回 null，播放点判空跳过，不再崩溃）
    var soundCountdown: Sound? = loadSound("sound.countdown.name", "ENTITY_EXPERIENCE_ORB_PICKUP")
    var soundCountdownVolume = config.getDouble("sound.countdown.volume", 1.0).toFloat()
    var soundCountdownPitch = config.getDouble("sound.countdown.pitch", 1.0).toFloat()
    var soundSuccess: Sound? = loadSound("sound.success.name", "ENTITY_ENDERMAN_TELEPORT")
    var soundSuccessVolume = config.getDouble("sound.success.volume", 1.0).toFloat()
    var soundSuccessPitch = config.getDouble("sound.success.pitch", 1.0).toFloat()
    var soundFail: Sound? = loadSound("sound.fail.name", "ENTITY_VILLAGER_NO")
    var soundFailVolume = config.getDouble("sound.fail.volume", 1.0).toFloat()
    var soundFailPitch = config.getDouble("sound.fail.pitch", 1.0).toFloat()
    var soundCancel: Sound? = loadSound("sound.cancel.name", "BLOCK_NOTE_BLOCK_BASS")
    var soundCancelVolume = config.getDouble("sound.cancel.volume", 1.0).toFloat()
    var soundCancelPitch = config.getDouble("sound.cancel.pitch", 1.0).toFloat()
    // 传送费用（非管理传送命令）：money=Vault 金钱 / points=PlayerPoints 点券；
    // 前置未安装时费用功能自动跳过（免费）。会员折扣沿用 tpa.vip 体系权限。
    var costEnable = config.getBoolean("cost.enable")
    var costCurrency = config.getString("cost.currency") ?: "money"
    // 会员优惠模式：percent=vip_discount 值为实付百分比 / amount=值为固定减免金额
    var costDiscountMode = config.getString("cost.discount_mode") ?: "percent"
    var costPrices: Map<String, Double> = buildMap {
        for (key in listOf("tpa", "tphere", "home", "warp", "spawn", "back", "rtp")) {
            put(key, config.getDouble("cost.price.$key"))
        }
    }
    var costVipDiscount: Map<String, Double> = buildMap {
        config.getConfigurationSection("cost.vip_discount")?.getKeys(false)?.forEach { level ->
            put(level, config.getDouble("cost.vip_discount.$level", 100.0))
        }
    }
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
    // 黑名单方块列表：非法名称忽略；旧配置缺失本项时使用默认危险方块规则；显式空列表 = 关闭黑名单。
    // 旧服务器水/岩浆分流动/静止两种 Material，命中任一形态时同步扩展另一形态（见 expandLegacyLiquids）
    var rtpBlacklistedBlocks: Set<Material> = if (config.contains("rtp.blacklisted_blocks")) {
        config.getStringList("rtp.blacklisted_blocks")
            .mapNotNull { name -> resolveMaterial(name.uppercase(Locale.ROOT)) }
            .toSet()
    } else {
        DEFAULT_RTP_BLACKLIST
    }.let { expandLegacyLiquids(it) }
    // 随机传送中心：true=玩家当前位置（默认），false=世界出生点
    var rtpCenterOnPlayer = config.getBoolean("rtp.center_on_player", true)

    companion object {
        // 跨版本枚举改名对照（新名 → 旧名候选）：仅覆盖"同一方块"的枚举改名，不含语义替代；
        // 低版本确实不存在的方块（营火/细雪/滴水石锥等）无旧名可回退，安全跳过（不参与黑名单）
        private val MATERIAL_LEGACY_NAMES: Map<String, List<String>> = mapOf(
            "MAGMA_BLOCK" to listOf("MAGMA"),         // 1.10-1.12: MAGMA（1.8 无岩浆块，跳过）
            "NETHER_PORTAL" to listOf("PORTAL"),      // 1.8-1.12: PORTAL
            "COBWEB" to listOf("WEB"),                // 1.8-1.12: WEB
            "END_PORTAL" to listOf("ENDER_PORTAL"),   // 1.9+ 改名 END_PORTAL（1.8: ENDER_PORTAL）
        )

        // 逐级尝试的 Material 名称解析：本名 → 跨版本旧名对照 → 安全返回 null。
        // 读到当前版本没有的方块名时先查对照表换旧名再试，低版本没有对应方块则直接略过；
        // 绝不让配置解析抛异常拖垮配置初始化或 RTP 流程
        private fun resolveMaterial(vararg names: String): Material? {
            for (name in names) {
                runCatching { return Material.valueOf(name) }
                for (legacy in MATERIAL_LEGACY_NAMES[name].orEmpty()) {
                    runCatching { return Material.valueOf(legacy) }
                }
            }
            return null
        }

        // 1.8-1.12 的水/岩浆分"流动/静止"两种 Material（1.13+ 合并为 WATER/LAVA）：
        // 黑名单含流动/静止任一形态时，同步排除旧服务器的另一形态——否则落点脚/头层的
        // 静止岩浆（isSolid=false 且不在黑名单）会漏判，1.8 下界概率性传送进岩浆海。
        // 新版服务器 STATIONARY_* 枚举不存在，resolveMaterial 返回 null 自然跳过
        private fun expandLegacyLiquids(blocks: Set<Material>): Set<Material> {
            val expanded = blocks.toMutableSet()
            if (Material.WATER in blocks) resolveMaterial("STATIONARY_WATER")?.let { expanded.add(it) }
            if (Material.LAVA in blocks) resolveMaterial("STATIONARY_LAVA")?.let { expanded.add(it) }
            return expanded
        }

        // rtp 黑名单方块默认规则（与 config.yml 默认项保持一致）：
        // 站立/触碰会受伤、产生负面效果、误触发传送或被困的方块
        // 括号内标注各名字在新旧服务器的可用范围，1.8.8 起全版本安全加载
        val DEFAULT_RTP_BLACKLIST: Set<Material> = listOfNotNull(
            resolveMaterial("LAVA"),                    // 岩浆：烧灼
            resolveMaterial("WATER"),                   // 水：溺水/湿身
            resolveMaterial("FIRE"),                    // 火：烧灼
            resolveMaterial("SOUL_FIRE"),               // 灵魂火：烧灼（1.16+）
            resolveMaterial("MAGMA_BLOCK", "MAGMA"),    // 岩浆块：站立烧灼（1.10-1.12: MAGMA，1.8 无此方块）
            resolveMaterial("BEDROCK"),                 // 基岩：基岩层上方无可站立空间
            resolveMaterial("CACTUS"),                  // 仙人掌：站立扎伤
            resolveMaterial("CAMPFIRE"),                // 营火：站立烧灼（1.14+）
            resolveMaterial("SOUL_CAMPFIRE"),           // 灵魂营火：站立烧灼（1.16+）
            resolveMaterial("POINTED_DRIPSTONE"),       // 滴水石锥：坠落刺伤（1.17+）
            resolveMaterial("POWDER_SNOW"),             // 细雪：陷入冻伤（1.17+）
            resolveMaterial("SWEET_BERRY_BUSH"),        // 甜浆果丛：穿行扎伤（1.14+）
            resolveMaterial("WITHER_ROSE"),             // 凋零玫瑰：凋零效果（1.14+）
            resolveMaterial("NETHER_PORTAL", "PORTAL"), // 下界传送门：误触发维度传送（1.8-1.12: PORTAL）
            resolveMaterial("END_PORTAL"),              // 末地传送门：误触发维度传送（1.8: ENDER_PORTAL，对照表自动换名）
            resolveMaterial("END_GATEWAY"),             // 末地折跃门：误触发维度传送（1.9+）
            resolveMaterial("BUBBLE_COLUMN"),           // 气泡柱：卷入水柱（1.13+）
            resolveMaterial("COBWEB", "WEB"),           // 蜘蛛网：被困（1.8-1.12: WEB）
        ).toSet()
    }


    // 读取配置中的音效名称，非法或缺失回退默认
    private fun loadSound(path: String, defaultName: String): Sound? {
        // 候选顺序：配置值 → 默认名 → 各自的旧版名（1.8.x 无 ENTITY_/BLOCK_ 前缀新命名）
        // 全部无效时返回 null（播放点判空跳过），避免 Sound.valueOf 抛异常拖垮配置初始化
        val candidates = buildList {
            config.getString(path)?.trim()?.takeIf { it.isNotEmpty() }?.uppercase(Locale.ROOT)?.let { add(it) }
            add(defaultName.uppercase(Locale.ROOT))
            addAll(legacySoundNames(defaultName))
            addAll(legacySoundNames(config.getString(path) ?: ""))
        }
        for (name in candidates.distinct()) {
            runCatching { return Sound.valueOf(name) }
        }
        return null
    }

    // 1.9 音效改名对照：新枚举名去掉实体/方块前缀并按旧规则简化（覆盖配置可再自定义的常见场景）
    private fun legacySoundNames(name: String): List<String> {
        val upper = name.uppercase(Locale.ROOT)
        return when (upper) {
            "ENTITY_EXPERIENCE_ORB_PICKUP" -> listOf("EXPERIENCE_ORB_PICKUP")
            "ENTITY_ENDERMAN_TELEPORT" -> listOf("ENDERMAN_TELEPORT")
            "ENTITY_VILLAGER_NO" -> listOf("VILLAGER_NO")
            "BLOCK_NOTE_BLOCK_BASS" -> listOf("NOTE_BASS")
            "BLOCK_NOTE_BLOCK_PLING" -> listOf("NOTE_PLING")
            else -> listOfNotNull(
                upper.removePrefix("ENTITY_").takeIf { it != upper },
                upper.removePrefix("BLOCK_").takeIf { it != upper },
                upper.removePrefix("AMBIENT_").takeIf { it != upper }
            )
        }
    }

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
