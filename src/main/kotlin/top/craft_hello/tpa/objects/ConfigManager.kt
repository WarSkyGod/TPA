package top.craft_hello.tpa.objects

import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.Config
import top.craft_hello.tpa.datas.SpawnConfig
import top.craft_hello.tpa.datas.WarpConfig

object ConfigManager {
    val plugin = TPA.plugin
    var config: Config
    var spawnConfig: SpawnConfig
    var warpConfig: WarpConfig

    init {
        plugin.saveDefaultConfig()
        config = Config(plugin.config)
        spawnConfig = SpawnConfig(plugin)
        warpConfig = WarpConfig(plugin)
    }

    fun reloadConfig() {
        plugin.reloadConfig()
        config = Config(plugin.config)
    }

    fun reloadSpawnConfig() { spawnConfig = SpawnConfig(plugin) }

    fun reloadWarpConfig() { warpConfig = WarpConfig(plugin) }

    fun reloadLanguages() { LanguageManager.reloadLanguage() }

    fun reloadAllConfig() {
        reloadConfig()
        reloadSpawnConfig()
        reloadWarpConfig()
        reloadLanguages()
    }
}
