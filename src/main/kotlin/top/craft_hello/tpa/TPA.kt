package top.craft_hello.tpa

import cn.handyplus.lib.adapter.HandySchedulerUtil
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import top.craft_hello.tpa.commands.DelSpawnCommand
import top.craft_hello.tpa.commands.SetSpawnCommand
import top.craft_hello.tpa.commands.SpawnCommand
import top.craft_hello.tpa.commands.TpaCommand
import top.craft_hello.tpa.commands.TpacCommand
import top.craft_hello.tpa.datas.Language
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.DatabaseManager
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.utils.SendMessageUtil

class TPA : JavaPlugin() {
    private lateinit var language: Language
    val console = Bukkit.getConsoleSender()

    override fun onEnable() {
        // 插件加载时执行
        HandySchedulerUtil.init(this)
        registerCommands()
        HandySchedulerUtil.runTaskAsynchronously {
            plugin = this
            val pluginId = 26417
            Metrics(this, pluginId)
            language = LanguageManager.getLanguage(ConfigManager.config.language)
            DatabaseManager(this).setupDatabase()
            SendMessageUtil.pluginLoaded(console, pluginMeta.version)
        }
    }

    private fun registerCommands(){
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            val registrar = it.registrar()
            TpaCommand.tpaCommand(registrar)
            TpacCommand.tpacCommand(registrar)
            SpawnCommand.spawnCommand(registrar)
            SetSpawnCommand.setSpawnCommand(registrar)
            DelSpawnCommand.delSpawnCommand(registrar)
        }
    }

    override fun onDisable() {
        // 插件卸载时执行
        DatabaseManager(this).closeDataSource()
        SendMessageUtil.pluginUnLoaded(console)
    }

    companion object {
        lateinit var plugin: TPA
    }
}
