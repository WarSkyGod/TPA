package top.craft_hello.tpa.objects

import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.Config
import top.craft_hello.tpa.datas.SpawnConfig

object ConfigManager {
    val plugin = TPA.plugin
    var config: Config
    var spawnConfig: SpawnConfig

    init {
        plugin.saveDefaultConfig()
        config = Config(plugin.config)
        spawnConfig = SpawnConfig(plugin)
    }
}