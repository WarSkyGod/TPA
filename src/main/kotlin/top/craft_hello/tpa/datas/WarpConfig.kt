package top.craft_hello.tpa.datas

import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import java.io.File

// 传送点存储（warp.yml），结构：warps.<名称>.world/x/y/z/yaw/pitch
data class WarpConfig(val plugin: TPA) {
    var warpConfigFile = File(plugin.dataFolder, "warp.yml")
    lateinit var warpConfig : YamlConfiguration
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

    fun containsWarpLocation(name: String): Boolean {
        return warpConfig.contains("warps.$name.world")
    }

    fun getWarpLocation(name: String): Location? {
        return warpConfig.getLocation("warps.$name")
    }

    fun getWarpNames(): List<String> {
        return warpConfig.getConfigurationSection("warps")?.getKeys(false)?.toList() ?: emptyList()
    }

    fun setWarpLocation(name: String, location: Location) {
        warpConfig.set("warps.$name", location)
        save()
    }

    fun delWarpLocation(name: String): Boolean {
        if (!containsWarpLocation(name)) return false
        warpConfig.set("warps.$name", null)
        save()
        return true
    }

    fun save() {
        warpConfig.save(warpConfigFile)
    }
}
