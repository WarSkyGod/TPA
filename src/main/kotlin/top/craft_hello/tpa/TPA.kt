package top.craft_hello.tpa

import cn.handyplus.lib.adapter.HandySchedulerUtil
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import top.craft_hello.tpa.commands.LegacyCommandRouter
import top.craft_hello.tpa.events.TPAPlayerDeathEvent
import top.craft_hello.tpa.events.TPAPlayerJoinEvent
import top.craft_hello.tpa.events.TPAPlayerLocaleChangeEvent
import top.craft_hello.tpa.events.TPAPlayerQuitEvent
import top.craft_hello.tpa.events.TPAPlayerRespawnEvent
import top.craft_hello.tpa.events.TPAPlayerTeleportEvent
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.ConfigUpdater
import top.craft_hello.tpa.objects.DatabaseManager
import top.craft_hello.tpa.objects.EconomyHook
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.objects.StorageMigrator
import top.craft_hello.tpa.utils.AdventureBridge
import top.craft_hello.tpa.utils.BedrockFormHook
import top.craft_hello.tpa.utils.PapiHook
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.TpaVersion
import top.craft_hello.tpa.utils.VersionUtil
import top.craft_hello.tpa.utils.YamlIO
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class TPA : JavaPlugin() {

    // 1.8-1.12 的 JavaPlugin.getConfig/reloadConfig/saveConfig 按 JVM 系统编码
    // （中文 Windows 为 GBK）读写 config.yml，UTF-8 中文值会读乱/写坏；
    // 覆盖为 YamlIO 统一 UTF-8 读取（含 3.x 系统编码旧档回退）
    private var utf8Config: FileConfiguration? = null

    override fun getConfig(): FileConfiguration {
        if (utf8Config == null) reloadConfig()
        return utf8Config!!
    }

    override fun reloadConfig() {
        val config = YamlIO.load(File(dataFolder, "config.yml"))
        getResource("config.yml")?.let { stream ->
            config.setDefaults(
                YamlConfiguration.loadConfiguration(InputStreamReader(stream, StandardCharsets.UTF_8))
            )
        }
        utf8Config = config
    }

    override fun saveConfig() {
        utf8Config?.let { YamlIO.save(it, File(dataFolder, "config.yml")) }
    }

    override fun onEnable() {
        plugin = this
        HandySchedulerUtil.init(this)
        // Adventure 桥接（1.8.8+ 全版本统一消息发送层）
        AdventureBridge.init(this)
        Metrics(this, 26417)

        // 老版本配置自动迁移（version 键判断，幂等）：必须在 ConfigManager/LanguageManager
        // 初始化前执行，迁移内部最后会刷新 ConfigManager，确保后续流程使用迁移后的新值
        ConfigUpdater.migrateIfNeeded(this)

        // 配置与语言
        ConfigManager
        LanguageManager.loadAllLanguage()

        // PlaceholderAPI（可选依赖）
        PapiHook.registerExpansion()

        // 玩家数据存储（yml 默认 / 数据库可选）
        DatabaseManager.setupDatabase(this)
        StorageMigrator.init(this)
        // 自动迁移：降级库恢复 / yml↔数据库双向（迁前备份到 backup/）
        StorageMigrator.migrateAll()
        PlayerDataManager.init(this)
        // 传送点存储按数据库状态重建（迁移完成后库中数据为最新）
        ConfigManager.reinitDataStores()

        // 经济前置挂钩（Vault/PlayerPoints 可选）：费用启用但货币系统缺失时警告
        EconomyHook.init(this)
        // 基岩弹窗交互挂钩（Floodgate 可选）：基岩玩家点击类消息改为弹窗按钮
        BedrockFormHook.init(this)
        if (ConfigManager.config.costEnable && !EconomyHook.isCurrencyAvailable(ConfigManager.config.costCurrency)) {
            val needed = if (ConfigManager.config.costCurrency == "points") "PlayerPoints" else "Vault"
            logger.warning(SendMessageUtil.consoleLog("system.log.cost_without_currency", ConfigManager.config.costCurrency, needed))
        }

        // PlaceholderAPI（可选依赖）
        PapiHook.registerExpansion()

        // debug 模式开启提醒（控制台）
        SendMessageUtil.debugWarningNotice()

        // 更新检查（GitHub Releases Latest）
        VersionUtil.init(this)

        // 注册命令：全版本统一走 plugin.yml 声明 + LegacyCommandRouter 传统路由
        // （Paper 1.19+ 自动把 plugin.yml 命令桥接进 Brigadier 树，TabCompleter 补全正常；
        //   1.8-1.18 为原生 SimpleCommandMap 路径；业务实现两路共用）
        LegacyCommandRouter.register(this)

        // 注册事件监听（PlayerLocaleChangeEvent 为 1.12+ API，低版本跳过注册以免类加载失败）
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(TPAPlayerDeathEvent, this)
        pluginManager.registerEvents(TPAPlayerJoinEvent, this)
        pluginManager.registerEvents(TPAPlayerQuitEvent, this)
        pluginManager.registerEvents(TPAPlayerRespawnEvent, this)
        pluginManager.registerEvents(TPAPlayerTeleportEvent, this)
        if (TpaVersion.supportsLocaleEvent) pluginManager.registerEvents(TPAPlayerLocaleChangeEvent, this)

        // 启动更新检查
        VersionUtil.startAsyncUpdateCheck()

        SendMessageUtil.pluginLoaded(Bukkit.getConsoleSender(), description.version)
    }

    override fun onDisable() {
        PlayerDataManager.unloadAll()
        DatabaseManager.closeDataSource()
        SendMessageUtil.pluginUnLoaded(Bukkit.getConsoleSender())
        AdventureBridge.close()
    }

    companion object {
        lateinit var plugin: TPA
    }
}
