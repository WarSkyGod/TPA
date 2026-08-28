package top.craft_hello.tpa.datas

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import java.io.File

// 传送点存储（warp.yml）
// 4.0 格式：warps.<名称> = Bukkit Location 序列化节点（==: org.bukkit.Location / world_key / x / y / z / yaw / pitch）
// 兼容 3.x 格式：根级 <名称>.world/x/y/z/yaw/pitch 手工分键
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

    // 3.x 手工分键格式组装（仅传送时需要；世界缺失时返回 null 由调用方报错）
    private fun loadLegacySection(section: ConfigurationSection): Location? {
        val worldName = section.getString("world") ?: return null
        val world = Bukkit.getWorld(worldName) ?: return null
        return Location(
            world,
            section.getDouble("x"),
            section.getDouble("y"),
            section.getDouble("z"),
            section.getDouble("yaw").toFloat(),
            section.getDouble("pitch").toFloat()
        )
    }

    // 传送用：解析出可用 Location（4.0 序列化格式优先，兼容 3.x 手工分键）
    fun getWarpLocation(name: String): Location? {
        val raw = warpConfig.get("warps.$name") ?: warpConfig.get(name) ?: return null
        return when (raw) {
            is Location -> raw
            is ConfigurationSection -> loadLegacySection(raw)
            else -> null
        }
    }

    // 存在性：只看键是否在配置树中，不解析 Location、不判断世界（删除/列表不依赖世界可用性）
    fun containsWarpLocation(name: String): Boolean {
        return warpConfig.get("warps.$name") != null || warpConfig.get(name) != null
    }

    fun getWarpNames(): List<String> {
        val names = linkedSetOf<String>()
        warpConfig.getConfigurationSection("warps")?.getKeys(false)?.let { names.addAll(it) }
        // 3.x 根级格式：根键即传送点名（排除 4.0 的 warps 容器键）
        for (key in warpConfig.getKeys(false)) {
            val raw = warpConfig.get(key)
            if (key != "warps" && (raw is Location || raw is ConfigurationSection)) names.add(key)
        }
        return names.toList()
    }

    fun setWarpLocation(name: String, location: Location) {
        warpConfig.set("warps.$name", location)
        save()
    }

    fun delWarpLocation(name: String): Boolean {
        if (!containsWarpLocation(name)) return false
        warpConfig.set("warps.$name", null)
        warpConfig.set(name, null) // 兼容清理 3.x 根级残留
        save()
        return true
    }

    fun save() {
        warpConfig.save(warpConfigFile)
    }
}
