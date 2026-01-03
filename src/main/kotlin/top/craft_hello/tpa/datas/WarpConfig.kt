package top.craft_hello.tpa.datas

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import java.io.File

data class WarpConfig(val plugin: TPA) {
    var warpConfigFile = File(plugin.dataFolder, "warp.yml")
    lateinit var warpConfig : FileConfiguration
    init {
        loadConfig()
    }

    fun loadConfig(isReplace : Boolean = false) {
        if (isReplace || !warpConfigFile.exists()) {
            plugin.saveResource("warp.yml", isReplace)
            warpConfigFile = File(warpConfigFile.absolutePath)
        }
        warpConfig = YamlConfiguration.loadConfiguration(warpConfigFile)
    }
}
