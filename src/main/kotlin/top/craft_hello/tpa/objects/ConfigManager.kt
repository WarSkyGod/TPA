package top.craft_hello.tpa.objects

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.Config
import top.craft_hello.tpa.datas.SpawnConfig
import top.craft_hello.tpa.utils.SendMessageUtil

object ConfigManager {
    val plugin = TPA.plugin
    var config: Config
    var spawnConfig: SpawnConfig

    init {
        plugin.saveDefaultConfig()
        config = Config(plugin.config)
        spawnConfig = SpawnConfig(plugin)
    }

    fun reloadConfig() {
        plugin.reloadConfig()
        config = Config(plugin.config)
    }

    fun reloadSpawnConfig() { spawnConfig = SpawnConfig(plugin) }

    fun reloadLanguages() { LanguageManager.reloadLanguage() }

    fun reloadAllConfig() {
        reloadConfig()
        reloadSpawnConfig()
        reloadLanguages()
    }
}