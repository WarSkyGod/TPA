package top.craft_hello.tpa

import cn.handyplus.lib.adapter.HandySchedulerUtil
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import top.craft_hello.tpa.commands.BackCommand
import top.craft_hello.tpa.commands.DelHomeCommand
import top.craft_hello.tpa.commands.DelSpawnCommand
import top.craft_hello.tpa.commands.DelWarpCommand
import top.craft_hello.tpa.commands.DenysCommand
import top.craft_hello.tpa.commands.HomeCommand
import top.craft_hello.tpa.commands.HomesCommand
import top.craft_hello.tpa.commands.RtpCommand
import top.craft_hello.tpa.commands.SetDefaultHomeCommand
import top.craft_hello.tpa.commands.SetHomeCommand
import top.craft_hello.tpa.commands.SetSpawnCommand
import top.craft_hello.tpa.commands.SetWarpCommand
import top.craft_hello.tpa.commands.SpawnCommand
import top.craft_hello.tpa.commands.TpaCommand
import top.craft_hello.tpa.commands.TpacCommand
import top.craft_hello.tpa.commands.TpacceptCommand
import top.craft_hello.tpa.commands.TpAllCommand
import top.craft_hello.tpa.commands.TpdenyCommand
import top.craft_hello.tpa.commands.TphereCommand
import top.craft_hello.tpa.commands.TpLogoutCommand
import top.craft_hello.tpa.commands.WarpCommand
import top.craft_hello.tpa.events.TPAPlayerDeathEvent
import top.craft_hello.tpa.events.TPAPlayerJoinEvent
import top.craft_hello.tpa.events.TPAPlayerLocaleChangeEvent
import top.craft_hello.tpa.events.TPAPlayerQuitEvent
import top.craft_hello.tpa.events.TPAPlayerRespawnEvent
import top.craft_hello.tpa.events.TPAPlayerTeleportEvent
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.DatabaseManager
import top.craft_hello.tpa.objects.EconomyHook
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.objects.StorageMigrator
import top.craft_hello.tpa.utils.BedrockFormHook
import top.craft_hello.tpa.utils.PapiHook
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.VersionUtil

class TPA : JavaPlugin() {

    override fun onEnable() {
        plugin = this
        HandySchedulerUtil.init(this)
        Metrics(this, 26417)

        // 配置与语言
        ConfigManager
        LanguageManager.loadAllLanguage()

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
            logger.warning("传送费用已启用（cost.enable: true）但货币系统（${ConfigManager.config.costCurrency}）不可用：$needed 未安装或未提供经济/点券服务，费用将不收取")
        }

        // PlaceholderAPI（可选依赖）
        PapiHook.registerExpansion()

        // debug 模式开启提醒（控制台）
        SendMessageUtil.debugWarningNotice()

        // 更新检查（GitHub Releases Latest）
        VersionUtil.init(this)

        // 注册命令（Paper Brigadier，LifecycleEvents）
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val registrar = event.registrar()
            registrar.register(TpaCommand.registerCommands())
            registrar.register(TpacCommand.registerCommands())
            registrar.register(TphereCommand.registerCommands())
            registrar.register(TpacceptCommand.registerCommands())
            registrar.register(TpdenyCommand.registerCommands())
            registrar.register(DenysCommand.registerCommands())
            registrar.register(TpAllCommand.registerCommands())
            registrar.register(TpLogoutCommand.registerCommands())
            registrar.register(RtpCommand.registerCommands())
            registrar.register(BackCommand.registerCommands())
            registrar.register(WarpCommand.registerCommands())
            registrar.register(SetWarpCommand.registerCommands())
            registrar.register(DelWarpCommand.registerCommands())
            registrar.register(HomeCommand.registerCommands())
            registrar.register(HomesCommand.registerCommands())
            registrar.register(SetHomeCommand.registerCommands())
            registrar.register(SetDefaultHomeCommand.registerCommands())
            registrar.register(DelHomeCommand.registerCommands())
            registrar.register(SpawnCommand.registerCommands())
            registrar.register(SetSpawnCommand.registerCommands())
            registrar.register(DelSpawnCommand.registerCommands())
        }

        // 注册事件监听
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(TPAPlayerDeathEvent, this)
        pluginManager.registerEvents(TPAPlayerJoinEvent, this)
        pluginManager.registerEvents(TPAPlayerQuitEvent, this)
        pluginManager.registerEvents(TPAPlayerRespawnEvent, this)
        pluginManager.registerEvents(TPAPlayerTeleportEvent, this)
        pluginManager.registerEvents(TPAPlayerLocaleChangeEvent, this)

        // 启动更新检查
        VersionUtil.startAsyncUpdateCheck()

        SendMessageUtil.pluginLoaded(Bukkit.getConsoleSender(), pluginMeta.version)
    }

    override fun onDisable() {
        PlayerDataManager.unloadAll()
        DatabaseManager.closeDataSource()
        SendMessageUtil.pluginUnLoaded(Bukkit.getConsoleSender())
    }

    companion object {
        lateinit var plugin: TPA
    }
}
