package top.craft_hello.tpa.objects

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.PlayerData
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

// 玩家数据管理器：缓存 + 双存储（yml 默认 / 数据库可选）
object PlayerDataManager {
    private lateinit var plugin: TPA
    private lateinit var store: PlayerDataStore
    private val cache = ConcurrentHashMap<UUID, PlayerData>()

    fun init(plugin: TPA) {
        this.plugin = plugin
        store = if (ConfigManager.config.useDatabase) {
            DatabasePlayerDataStore(DatabaseManager)
        } else {
            YamlPlayerDataStore(plugin)
        }
    }

    fun get(uuid: UUID): PlayerData {
        return cache.computeIfAbsent(uuid) { uuid2 ->
            store.load(uuid2) ?: PlayerData(uuid2)
        }
    }

    fun get(player: Player): PlayerData {
        val data = get(player.uniqueId)
        if (data.playerName != player.name) {
            data.playerName = player.name
        }
        return data
    }

    fun getIfLoaded(uuid: UUID): PlayerData? = cache[uuid]

    // 按名称查找（在线优先，其次缓存），用于 /tplogout 等离线目标场景
    fun getByName(name: String): PlayerData? {
        val online = Bukkit.getPlayerExact(name) ?: return cache.values.firstOrNull {
            it.playerName.equals(name, ignoreCase = true)
        }
        return get(online)
    }

    fun save(uuid: UUID) {
        val data = cache[uuid] ?: return
        try {
            store.save(data)
        } catch (ignored: Exception) {
            if (ConfigManager.config.debug) ignored.printStackTrace()
        }
    }

    fun save(player: Player) {
        save(player.uniqueId)
    }

    fun save(data: PlayerData) {
        try {
            store.save(data)
        } catch (ignored: Exception) {
            if (ConfigManager.config.debug) ignored.printStackTrace()
        }
    }

    // 玩家退出：保存并移除缓存
    fun unload(uuid: UUID) {
        save(uuid)
        cache.remove(uuid)
    }

    fun unloadAll() {
        for (uuid in cache.keys) save(uuid)
        cache.clear()
    }
}
