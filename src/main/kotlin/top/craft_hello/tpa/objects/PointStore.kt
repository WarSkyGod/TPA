package top.craft_hello.tpa.objects

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.utils.YamlIO
import java.io.File
import java.sql.Connection

// 传送点（spawn/warp）存储抽象：yml（默认，兼容 3.x 文件）与数据库（use_database=true）
interface PointStore {
    // spawn（单例，null=未设置）
    fun loadSpawn(): Location?
    fun saveSpawn(location: Location)
    fun deleteSpawn()

    // warp（名称 → 位置；世界缺失的条目不进 Location 映射，但仍可通过 names/contains 管理）
    fun loadWarps(): Map<String, Location>
    fun containsWarp(name: String): Boolean
    fun saveWarp(name: String, location: Location)
    fun deleteWarp(name: String)
    fun warpNames(): List<String>
}

// Location 与数据库列值的编解码（world_name 独立列；世界缺失时解析为 null 但保留行数据）
object PointLocationCodec {
    fun loadLocation(worldName: String?, x: Double, y: Double, z: Double, yaw: Double, pitch: Double): Location? {
        if (worldName.isNullOrBlank()) return null
        val world = Bukkit.getWorld(worldName) ?: return null
        return Location(world, x, y, z, yaw.toFloat(), pitch.toFloat())
    }
}

// yml 存储：spawn.yml（单节点）+ warp.yml（4.0 warps.<名> 序列化 / 3.x 根级手工分键，双格式兼容）
class YamlPointStore(private val plugin: TPA) : PointStore {
    val spawnFile = File(plugin.dataFolder, "spawn.yml")
    val warpFile = File(plugin.dataFolder, "warp.yml")

    fun spawnFileExists(): Boolean = spawnFile.exists()
    fun warpFileExists(): Boolean = warpFile.exists()

    fun hasSpawnData(): Boolean {
        if (!spawnFile.exists()) return false
        val config = YamlIO.load(spawnFile)
        return config.getConfigurationSection("spawn") != null && config.getString("spawn.world") != null && config.getString("spawn.world") != "null"
    }

    // warp.yml 全部条目名（4.0 warps 节 + 3.x 根级），不含解析
    fun hasWarpData(): Boolean = rawWarpNames().isNotEmpty()

    // warp.yml 全部条目名（4.0 warps 节 + 3.x 根级），不含解析
    private fun rawWarpNames(): Set<String> {
        if (!warpFile.exists()) return emptySet()
        val config = YamlIO.load(warpFile)
        val names = linkedSetOf<String>()
        config.getConfigurationSection("warps")?.getKeys(false)?.let { names.addAll(it) }
        // 3.x 根级格式：根键即传送点名（排除 4.0 的 warps 容器键）
        for (key in config.getKeys(false)) {
            if (key == "warps") continue
            val section = config.getConfigurationSection(key) ?: continue
            if (section.contains("world") || section.contains("==")) names.add(key)
        }
        return names
    }

    override fun loadSpawn(): Location? {
        if (!spawnFile.exists()) return null
        val config = YamlIO.load(spawnFile)
        val worldName = config.getString("spawn.world") ?: return null
        if (worldName == "null") return null
        return PointLocationCodec.loadLocation(
            worldName,
            config.getDouble("spawn.x"),
            config.getDouble("spawn.y"),
            config.getDouble("spawn.z"),
            config.getDouble("spawn.yaw"),
            config.getDouble("spawn.pitch")
        )
    }

    override fun saveSpawn(location: Location) {
        val config = if (spawnFile.exists()) YamlIO.load(spawnFile) else YamlConfiguration()
        config.set("spawn.world", location.world.name)
        config.set("spawn.x", location.x)
        config.set("spawn.y", location.y)
        config.set("spawn.z", location.z)
        config.set("spawn.yaw", location.yaw.toDouble())
        config.set("spawn.pitch", location.pitch.toDouble())
        YamlIO.save(config, spawnFile)
    }

    override fun deleteSpawn() {
        if (!spawnFile.exists()) return
        val config = YamlIO.load(spawnFile)
        config.set("spawn", null)
        YamlIO.save(config, spawnFile)
    }

    override fun loadWarps(): Map<String, Location> {
        val locations = linkedMapOf<String, Location>()
        if (!warpFile.exists()) return locations
        val config = YamlIO.load(warpFile)
        // 4.0 格式：warps.<名> = Location（YamlConfiguration 依 == 标记自动反序列化为 Location 对象）
        for (name in config.getConfigurationSection("warps")?.getKeys(false) ?: emptySet()) {
            when (val raw = config.get("warps.$name")) {
                is Location -> locations[name] = raw
                is org.bukkit.configuration.ConfigurationSection -> loadLegacySection(raw)?.let { locations[name] = it }
                else -> {}
            }
        }
        // 3.x 根级格式：根键.world/x/y/z/yaw/pitch 手工分键
        for (key in config.getKeys(false)) {
            if (key == "warps" || locations.containsKey(key)) continue
            val section = config.getConfigurationSection(key) ?: continue
            if (section.contains("world")) loadLegacySection(section)?.let { locations[key] = it }
        }
        return locations
    }

    // 3.x 手工分键格式解析（世界缺失时返回 null 由调用方报错）
    private fun loadLegacySection(section: org.bukkit.configuration.ConfigurationSection): Location? {
        val worldName = section.getString("world") ?: return null
        val world = Bukkit.getWorld(worldName) ?: return null
        return Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), section.getDouble("yaw").toFloat(), section.getDouble("pitch").toFloat())
    }

    override fun containsWarp(name: String): Boolean = rawWarpNames().contains(name)

    override fun saveWarp(name: String, location: Location) {
        val config = if (warpFile.exists()) YamlIO.load(warpFile) else YamlConfiguration()
        config.set("warps.$name", location)
        YamlIO.save(config, warpFile)
    }

    override fun deleteWarp(name: String) {
        if (!warpFile.exists()) return
        val config = YamlIO.load(warpFile)
        config.set("warps.$name", null)
        config.set(name, null) // 兼容清理 3.x 根级残留
        YamlIO.save(config, warpFile)
    }

    override fun warpNames(): List<String> = rawWarpNames().toList()
}

// 数据库存储：tpa_points 表（point_type: spawn/warp；spawn 固定 point_name='spawn'）
class DatabasePointStore(private val database: DatabaseManager) : PointStore {

    override fun loadSpawn(): Location? {
        return queryPoint("spawn", "spawn")
    }

    override fun saveSpawn(location: Location) {
        upsertPoint("spawn", "spawn", location)
    }

    override fun deleteSpawn() {
        database.getConnection()?.use { connection ->
            connection.prepareStatement("DELETE FROM tpa_points WHERE point_type = 'spawn'").use { statement ->
                statement.executeUpdate()
            }
        }
    }

    override fun loadWarps(): Map<String, Location> {
        val locations = linkedMapOf<String, Location>()
        database.getConnection()?.use { connection ->
            connection.prepareStatement("SELECT point_name, world_name, x, y, z, yaw, pitch FROM tpa_points WHERE point_type = 'warp'").use { statement ->
                statement.executeQuery().use { rs ->
                    while (rs.next()) {
                        PointLocationCodec.loadLocation(rs.getString("world_name"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getDouble("yaw"), rs.getDouble("pitch"))
                            ?.let { locations[rs.getString("point_name")] = it }
                    }
                }
            }
        }
        return locations
    }

    override fun containsWarp(name: String): Boolean {
        return database.getConnection()?.use { connection ->
            connection.prepareStatement("SELECT 1 FROM tpa_points WHERE point_type = 'warp' AND point_name = ?").use { statement ->
                statement.setString(1, name)
                statement.executeQuery().next()
            }
        } ?: false
    }

    override fun saveWarp(name: String, location: Location) {
        upsertPoint("warp", name, location)
    }

    override fun deleteWarp(name: String) {
        database.getConnection()?.use { connection ->
            connection.prepareStatement("DELETE FROM tpa_points WHERE point_type = 'warp' AND point_name = ?").use { statement ->
                statement.setString(1, name)
                statement.executeUpdate()
            }
        }
    }

    override fun warpNames(): List<String> {
        val names = mutableListOf<String>()
        database.getConnection()?.use { connection ->
            connection.prepareStatement("SELECT point_name FROM tpa_points WHERE point_type = 'warp'").use { statement ->
                statement.executeQuery().use { rs ->
                    while (rs.next()) names.add(rs.getString("point_name"))
                }
            }
        }
        return names
    }

    private fun queryPoint(type: String, name: String): Location? {
        return database.getConnection()?.use { connection ->
            connection.prepareStatement("SELECT world_name, x, y, z, yaw, pitch FROM tpa_points WHERE point_type = ? AND point_name = ?").use { statement ->
                statement.setString(1, type)
                statement.setString(2, name)
                statement.executeQuery().use { rs ->
                    if (!rs.next()) return@use null
                    PointLocationCodec.loadLocation(rs.getString("world_name"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getDouble("yaw"), rs.getDouble("pitch"))
                }
            }
        }
    }

    private fun upsertPoint(type: String, name: String, location: Location) {
        database.getConnection()?.use { connection ->
            // SQLite 支持 ON CONFLICT；MySQL 使用 ON DUPLICATE KEY UPDATE
            val upsert = if (database.databaseType == "mysql") {
                """
                INSERT INTO tpa_points (point_type, point_name, world_name, x, y, z, yaw, pitch, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON DUPLICATE KEY UPDATE
                    world_name = VALUES(world_name), x = VALUES(x), y = VALUES(y), z = VALUES(z),
                    yaw = VALUES(yaw), pitch = VALUES(pitch), updated_at = CURRENT_TIMESTAMP
                """.trimIndent()
            } else {
                """
                INSERT INTO tpa_points (point_type, point_name, world_name, x, y, z, yaw, pitch, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT(point_type, point_name) DO UPDATE SET
                    world_name = excluded.world_name, x = excluded.x, y = excluded.y, z = excluded.z,
                    yaw = excluded.yaw, pitch = excluded.pitch, updated_at = CURRENT_TIMESTAMP
                """.trimIndent()
            }
            connection.prepareStatement(upsert).use { statement ->
                statement.setString(1, type)
                statement.setString(2, name)
                statement.setString(3, location.world.name)
                statement.setDouble(4, location.x)
                statement.setDouble(5, location.y)
                statement.setDouble(6, location.z)
                statement.setDouble(7, location.yaw.toDouble())
                statement.setDouble(8, location.pitch.toDouble())
                statement.executeUpdate()
            }
        }
    }

    companion object {
        // 迁移器用：目标库缺失时写入玩家数据行（dialect: sqlite/mysql）
        fun writePlayerRow(
            connection: Connection,
            dialect: String,
            uuid: String,
            playerName: String?,
            language: String?,
            setlang: Boolean,
            defaultHome: String?,
            homes: String?,
            denyList: String?,
            lastLocation: String?,
            logoutLocation: String?
        ) {
            val sql = if (dialect == "mysql") {
                """
                INSERT IGNORE INTO player_data
                    (uuid, player_name, language, setlang, default_home, homes, deny_list, last_location, logout_location, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """.trimIndent()
            } else {
                """
                INSERT INTO player_data
                    (uuid, player_name, language, setlang, default_home, homes, deny_list, last_location, logout_location, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT(uuid) DO NOTHING
                """.trimIndent()
            }
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, uuid)
                statement.setString(2, playerName)
                statement.setString(3, language)
                statement.setInt(4, if (setlang) 1 else 0)
                statement.setString(5, defaultHome)
                statement.setString(6, homes)
                statement.setString(7, denyList)
                statement.setString(8, lastLocation)
                statement.setString(9, logoutLocation)
                statement.executeUpdate()
            }
        }

        // 直接基于给定连接读取全部传送点（迁移器从降级库/旧库搬数据用，仅 sqlite 连接）
        fun readAllPoints(connection: Connection): Map<String, Map<String, Location>> {
            val points = linkedMapOf<String, MutableMap<String, Location>>()
            connection.prepareStatement("SELECT point_type, point_name, world_name, x, y, z, yaw, pitch FROM tpa_points").use { statement ->
                statement.executeQuery().use { rs ->
                    while (rs.next()) {
                        val type = rs.getString("point_type")
                        val name = rs.getString("point_name")
                        PointLocationCodec.loadLocation(rs.getString("world_name"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getDouble("yaw"), rs.getDouble("pitch"))
                            ?.let { points.getOrPut(type) { linkedMapOf() }[name] = it }
                    }
                }
            }
            return points
        }

        // 目标库写入（仅目标缺失时插入；dialect 决定冲突跳过方言：sqlite=ON CONFLICT / mysql=INSERT IGNORE）
        fun writePoint(connection: Connection, dialect: String, type: String, name: String, location: Location) {
            val sql = if (dialect == "mysql") {
                """
                INSERT IGNORE INTO tpa_points (point_type, point_name, world_name, x, y, z, yaw, pitch, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """.trimIndent()
            } else {
                """
                INSERT INTO tpa_points (point_type, point_name, world_name, x, y, z, yaw, pitch, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT(point_type, point_name) DO NOTHING
                """.trimIndent()
            }
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, type)
                statement.setString(2, name)
                statement.setString(3, location.world.name)
                statement.setDouble(4, location.x)
                statement.setDouble(5, location.y)
                statement.setDouble(6, location.z)
                statement.setDouble(7, location.yaw.toDouble())
                statement.setDouble(8, location.pitch.toDouble())
                statement.executeUpdate()
            }
        }

        fun spawnPointExists(connection: Connection): Boolean {
            return connection.prepareStatement("SELECT 1 FROM tpa_points WHERE point_type = 'spawn'").use { statement ->
                statement.executeQuery().next()
            }
        }

        fun warpPointExists(connection: Connection, name: String): Boolean {
            return connection.prepareStatement("SELECT 1 FROM tpa_points WHERE point_type = 'warp' AND point_name = ?").use { statement ->
                statement.setString(1, name)
                statement.executeQuery().next()
            }
        }
    }
}
