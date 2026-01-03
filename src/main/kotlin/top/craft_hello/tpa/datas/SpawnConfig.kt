package top.craft_hello.tpa.datas

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import java.io.File

data class SpawnConfig(val plugin : TPA) {
    var configFile = File(plugin.dataFolder, "spawn.yml")
    lateinit var config : FileConfiguration
    var worldName : String = "null"
    var world : World = Bukkit.getWorlds()[0]
    var x : Double = 0.0
    var y : Double = 0.0
    var z : Double = 0.0
    var yaw : Float = 0.0f
    var pitch : Float = 0.0f
    var spawnLocation: Location? = null
    init {
        loadConfig()
    }

    fun loadConfig(isReplace : Boolean = false) {
        if (isReplace || !configFile.exists()) {
            plugin.saveResource("spawn.yml", isReplace)
            configFile = File(configFile.absolutePath)
        }
        config = YamlConfiguration.loadConfiguration(configFile)
        worldName = config.getString("spawn.world") ?: "null"
        if (worldName != "null") {
            world = Bukkit.getWorld(worldName) ?: Bukkit.getWorlds()[0]
            x = config.getDouble("spawn.x")
            y = config.getDouble("spawn.y")
            z = config.getDouble("spawn.z")
            yaw = config.getDouble("spawn.yaw").toFloat()
            pitch = config.getDouble("spawn.pitch").toFloat()
            spawnLocation = Location(world, x, y, z, yaw, pitch)
            return
        }
    }

    fun setLocation(location : Location) {
        worldName = location.world.name
        world = location.world
        x = location.x
        y = location.y
        z = location.z
        yaw = location.yaw
        pitch = location.pitch
        spawnLocation = location
        saveConfig()
    }

    fun delLocation() : Boolean {
        if (worldName == "null") return false
        worldName = "null"
        world = Bukkit.getWorlds()[0]
        x = 0.0
        y = 0.0
        z = 0.0
        yaw = 0.0f
        pitch = 0.0f
        spawnLocation = null
        saveConfig(true)
        return true
    }

    fun saveConfig(isDelete : Boolean = false) {
        if (isDelete) {
            config.set("spawn", null)
        } else {
            config.set("spawn.world", worldName)
            config.set("spawn.x", x)
            config.set("spawn.y", y)
            config.set("spawn.z", z)
            config.set("spawn.yaw", yaw.toDouble())
            config.set("spawn.pitch", pitch.toDouble())
        }
        config.save(configFile)
    }

    fun getLocation() : Location? {
        return spawnLocation
    }
}
