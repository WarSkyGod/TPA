package top.craft_hello.tpa.objects

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.PlayerData
import java.io.File
import java.util.UUID

// 玩家数据存储抽象：yml（默认，兼容 3.x playerdata 文件）与数据库（use_database=true）
interface PlayerDataStore {
    fun load(uuid: UUID): PlayerData?
    fun save(data: PlayerData)
}

// 默认存储：plugins/TPA/playerdata/<uuid>.yml，与 3.x 格式互通
class YamlPlayerDataStore(private val plugin: TPA) : PlayerDataStore {
    private val folder = File(plugin.dataFolder, "playerdata")

    private fun fileOf(uuid: UUID): File = File(folder, "$uuid.yml")

    override fun load(uuid: UUID): PlayerData? {
        val file = fileOf(uuid)
        if (!file.exists()) return null
        val config = YamlConfiguration.loadConfiguration(file)
        val data = PlayerData(uuid)
        data.playerName = config.getString("player_name")
        data.language = config.getString("language")
        data.setlang = config.getBoolean("setlang", false)
        data.defaultHomeName = config.getString("default_home")
        for (name in config.getConfigurationSection("homes")?.getKeys(false) ?: emptySet()) {
            val location = config.getLocation("homes.$name") ?: continue
            data.homes[name] = location
        }
        data.denyList.addAll(config.getStringList("deny_list"))
        data.lastLocation = config.getLocation("last_location")
        data.logoutLocation = config.getLocation("logout_location")
        return data
    }

    override fun save(data: PlayerData) {
        if (!folder.exists()) folder.mkdirs()
        val config = YamlConfiguration()
        config.set("player_name", data.playerName)
        config.set("language", data.language)
        config.set("setlang", data.setlang)
        config.set("default_home", data.defaultHomeName)
        for ((name, location) in data.homes) config.set("homes.$name", location)
        config.set("deny_list", data.denyList)
        config.set("last_location", data.lastLocation)
        config.set("logout_location", data.logoutLocation)
        config.save(fileOf(data.uuid))
    }
}

// 数据库存储：复用 DatabaseManager 连接池，homes/位置以字符串编码
class DatabasePlayerDataStore(private val database: DatabaseManager) : PlayerDataStore {
    private val gson = Gson()
    private val homesType = object : TypeToken<Map<String, String>>() {}.type

    private fun encodeLocation(location: Location?): String? {
        if (location == null || location.world == null) return null
        return buildString {
            append(location.world.name).append(";")
            append(location.x).append(";")
            append(location.y).append(";")
            append(location.z).append(";")
            append(location.yaw).append(";")
            append(location.pitch)
        }
    }

    private fun decodeLocation(raw: String?): Location? {
        if (raw.isNullOrBlank()) return null
        val parts = raw.split(";")
        if (parts.size < 6) return null
        val world = Bukkit.getWorld(parts[0]) ?: return null
        return Location(
            world,
            parts[1].toDoubleOrNull() ?: return null,
            parts[2].toDoubleOrNull() ?: return null,
            parts[3].toDoubleOrNull() ?: return null,
            parts[4].toFloatOrNull() ?: 0f,
            parts[5].toFloatOrNull() ?: 0f
        )
    }

    override fun load(uuid: UUID): PlayerData? {
        return database.getConnection()?.use { connection ->
            connection.prepareStatement(
                "SELECT player_name, language, setlang, default_home, homes, last_location, logout_location FROM player_data WHERE uuid = ?"
            ).use { statement ->
                statement.setString(1, uuid.toString())
                statement.executeQuery().use { rs ->
                    if (!rs.next()) return@use null
                    val data = PlayerData(uuid)
                    data.playerName = rs.getString("player_name")
                    data.language = rs.getString("language")
                    data.setlang = rs.getBoolean("setlang")
                    data.defaultHomeName = rs.getString("default_home")
                    val homesRaw: Map<String, String>? = gson.fromJson(rs.getString("homes"), homesType)
                    for ((name, raw) in homesRaw ?: emptyMap()) {
                        decodeLocation(raw)?.let { data.homes[name] = it }
                    }
                    data.lastLocation = decodeLocation(rs.getString("last_location"))
                    data.logoutLocation = decodeLocation(rs.getString("logout_location"))
                    data.denyList.addAll(loadDenyList(connection, uuid))
                    data
                }
            }
        }
    }

    override fun save(data: PlayerData) {
        val homesRaw = data.homes.mapValues { encodeLocation(it.value) ?: "" }
        database.getConnection()?.use { connection ->
            connection.prepareStatement(
                """
                INSERT INTO player_data (uuid, player_name, language, setlang, default_home, homes, last_location, logout_location, deny_list, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT(uuid) DO UPDATE SET
                    player_name = excluded.player_name,
                    language = excluded.language,
                    setlang = excluded.setlang,
                    default_home = excluded.default_home,
                    homes = excluded.homes,
                    last_location = excluded.last_location,
                    logout_location = excluded.logout_location,
                    deny_list = excluded.deny_list,
                    updated_at = CURRENT_TIMESTAMP
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, data.uuid.toString())
                statement.setString(2, data.playerName)
                statement.setString(3, data.language)
                statement.setInt(4, if (data.setlang) 1 else 0)
                statement.setString(5, data.defaultHomeName)
                statement.setString(6, gson.toJson(homesRaw))
                statement.setString(7, encodeLocation(data.lastLocation))
                statement.setString(8, encodeLocation(data.logoutLocation))
                statement.setString(9, data.denyList.joinToString(","))
                statement.executeUpdate()
            }
        }
    }

    // 黑名单与家/语言分列：deny_list 以逗号分隔存储于独立列
    private fun loadDenyList(connection: java.sql.Connection, uuid: UUID): List<String> {
        connection.prepareStatement("SELECT deny_list FROM player_data WHERE uuid = ?").use { statement ->
            statement.setString(1, uuid.toString())
            statement.executeQuery().use { rs ->
                if (!rs.next()) return emptyList()
                val raw = rs.getString("deny_list") ?: return emptyList()
                return raw.split(",").filter { it.isNotBlank() }
            }
        }
    }
}
