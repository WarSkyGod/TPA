package top.craft_hello.tpa.datas

import org.bukkit.Location
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PointStore

// 传送点存储（薄壳）：实际读写委托 PointStore（yml：warp.yml / 数据库：tpa_points 表）
// 内存缓存 warps；世界缺失的条目不进缓存（传送时报世界不存在），但 names/contains 仍可用
data class WarpConfig(val plugin: TPA) {
    private var store: PointStore
    private var warps: MutableMap<String, Location> = linkedMapOf()
    private var allNames: MutableSet<String> = linkedSetOf()

    init {
        store = ConfigManager.createPointStore()
        loadFromStore()
    }

    private fun loadFromStore() {
        warps = linkedMapOf()
        allNames = linkedSetOf()
        allNames.addAll(store.warpNames())
        warps.putAll(store.loadWarps())
    }

    // 传送用：解析出可用 Location（世界缺失返回 null 由调用方报错）
    fun getWarpLocation(name: String): Location? {
        return warps[name]
    }

    // 存在性：名称在存储中即可（不依赖世界可用性）
    fun containsWarpLocation(name: String): Boolean {
        return allNames.contains(name)
    }

    fun getWarpNames(): List<String> {
        return allNames.toList()
    }

    fun setWarpLocation(name: String, location: Location) {
        store.saveWarp(name, location)
        warps[name] = location
        allNames.add(name)
    }

    fun delWarpLocation(name: String): Boolean {
        if (!allNames.contains(name)) return false
        store.deleteWarp(name)
        warps.remove(name)
        allNames.remove(name)
        return true
    }
}
