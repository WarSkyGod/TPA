package top.craft_hello.tpa.objects

import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.Config

object ConfigManager {
    val plugin = TPA.plugin
    var config: Config

    init {
        plugin.saveDefaultConfig()
        config = Config(plugin.config)
    }
}