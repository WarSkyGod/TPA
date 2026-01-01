package top.craft_hello.tpa

import cn.handyplus.lib.adapter.HandySchedulerUtil
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import top.craft_hello.tpa.commands.TpaCommand
import top.craft_hello.tpa.commands.TpacCommand
import top.craft_hello.tpa.datas.Language
import top.craft_hello.tpa.enums.LanguageType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.DatabaseManager
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.utils.SendMessageUtil

class TPA : JavaPlugin() {
    private lateinit var language: Language
    val CONSOLE = Bukkit.getConsoleSender()

    override fun onEnable() {
        // 插件加载时执行
        HandySchedulerUtil.init(this)
        registerCommands()
        HandySchedulerUtil.runTaskAsynchronously(Runnable {
            plugin = this
            val pluginId = 26417
            Metrics(this, pluginId)
            language = LanguageManager.getLanguage(ConfigManager.config.language)
            DatabaseManager(this).setupDatabase()
            SendMessageUtil().pluginLoaded(CONSOLE, pluginMeta.version)
        })
    }

    private fun registerCommands(){
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            val registrar = it.registrar()
            TpaCommand.tpaCommand(registrar)
            TpacCommand.tpacCommand(registrar)
        }
    }

    override fun onDisable() {
        // 插件卸载时执行
        SendMessageUtil().pluginUnLoaded(CONSOLE)
    }

    companion object {
        lateinit var plugin: TPA
    }
}
