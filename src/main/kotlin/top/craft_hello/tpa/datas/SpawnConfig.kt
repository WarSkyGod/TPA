package top.craft_hello.tpa.datas

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PointStore

// 主城存储（薄壳）：实际读写委托 PointStore（yml：spawn.yml / 数据库：tpa_points 表）
// 内存缓存 spawnLocation；worldName/x/y/z/yaw/pitch 保留为兼容字段
data class SpawnConfig(val plugin: TPA) {
    var worldName: String = "null"
    var world: World = Bukkit.getWorlds()[0]
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    var yaw: Float = 0.0f
    var pitch: Float = 0.0f
    var spawnLocation: Location? = null
    private var store: PointStore

    init {
        store = ConfigManager.createPointStore()
        loadFromStore()
    }

    private fun loadFromStore() {
        val location = store.loadSpawn() ?: return
        worldName = location.world.name
        world = location.world
        x = location.x
        y = location.y
        z = location.z
        yaw = location.yaw
        pitch = location.pitch
        spawnLocation = location
    }

    fun setLocation(location: Location) {
        worldName = location.world.name
        world = location.world
        x = location.x
        y = location.y
        z = location.z
        yaw = location.yaw
        pitch = location.pitch
        spawnLocation = location
        store.saveSpawn(location)
    }

    fun delLocation(): Boolean {
        if (worldName == "null" && spawnLocation == null) return false
        worldName = "null"
        world = Bukkit.getWorlds()[0]
        x = 0.0
        y = 0.0
        z = 0.0
        yaw = 0.0f
        pitch = 0.0f
        spawnLocation = null
        store.deleteSpawn()
        return true
    }

    fun getLocation(): Location? {
        return spawnLocation
    }
}
