package top.craft_hello.tpa.objects

import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.utils.SendMessageUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

// 存储方式自动迁移器（启动与 /tpac reload 时执行）：
// 1. 数据库可用：
//    a. 降级恢复：data_fallback.db 有数据 → 迁回主库 → 复制到 backup/ 后删除降级库
//    b. yml → 数据库：spawn.yml/warp.yml/playerdata 有数据且库中缺失 → 迁入 → 原 yml 复制到 backup/
// 2. 数据库未启用：残留 sqlite 主库（<databaseName>.db）有传送点数据而 yml 缺失 → 迁回 yml → 复制到 backup/
// 迁移幂等：目标已有数据不覆盖；源文件保留（迁移后不再读取），每次触发仅在"目标缺、源有"时搬运。
object StorageMigrator {
    private lateinit var plugin: TPA

    fun init(plugin: TPA) {
        this.plugin = plugin
    }

    private fun backupDir(): File = File(plugin.dataFolder, "backup")

    // 复制源文件到 backup/（重名自动加序号），返回备份文件路径或 null
    private fun backup(source: File): File? {
        if (!source.exists()) return null
        val dir = backupDir()
        if (!dir.exists()) dir.mkdirs()
        var target = File(dir, source.name)
        var index = 1
        while (target.exists()) {
            val base = source.nameWithoutExtension
            target = File(dir, "$base.$index.${source.extension}")
            index++
        }
        return try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            target
        } catch (e: Exception) {
            plugin.logger.warning(SendMessageUtil.consoleLog("system.log.backup_failed", source.name, e.message))
            null
        }
    }

    // 统一迁移入口
    fun migrateAll() {
        val useDatabase = ConfigManager.config.useDatabase
        if (useDatabase && DatabaseManager.isAvailable()) {
            // 降级状态下目标库就是降级库本身，恢复迁移跳过（文件正被连接池占用），
            // 但 yml→降级库迁移照常执行，保证配置修复后数据能完整迁回数据库
            migrateFallbackBackToDatabase()
            migrateFilesToDatabase()
            migratePlayerDataToDatabase()
        } else if (!useDatabase) {
            migrateDatabasePointsToFiles()
        }
    }

    // ---------- 降级库 → 主库恢复 ----------

    private fun migrateFallbackBackToDatabase() {
        if (DatabaseManager.isFallback) return // 降级文件正被当前连接池使用，无恢复必要
        val fallback = DatabaseManager.fallbackFile()
        if (!fallback.exists() || fallback.length() == 0L) return
        try {
            // 直接 JDBC 打开降级 sqlite 只读搬数据
            val connection = java.sql.DriverManager.getConnection("jdbc:sqlite:${fallback.absolutePath}")
            connection.use { source ->
                val target = DatabaseManager.getConnection() ?: return
                target.use { targetConnection ->
                    var migratedPlayers = 0
                    // 玩家数据：主库缺该 uuid 才写入（表可能不存在，单独容错）
                    try {
                        source.prepareStatement("SELECT uuid, player_name, language, setlang, default_home, homes, deny_list, last_location, logout_location FROM player_data").use { statement ->
                            statement.executeQuery().use { rs ->
                                while (rs.next()) {
                                    val uuid = rs.getString("uuid")
                                    if (playerRowExists(targetConnection, uuid)) continue
                                    DatabasePointStore.writePlayerRow(
                                        targetConnection, DatabaseManager.databaseType, uuid,
                                        rs.getString("player_name"), rs.getString("language"), rs.getBoolean("setlang"),
                                        rs.getString("default_home"), rs.getString("homes"),
                                        rs.getString("deny_list"), rs.getString("last_location"), rs.getString("logout_location")
                                    )
                                    migratedPlayers++
                                }
                            }
                        }
                    } catch (e: Exception) {
                        plugin.logger.warning(SendMessageUtil.consoleLog("system.log.fallback_migrate_players_failed", e.message))
                    }
                    // 传送点：主库缺失才写入（单独容错）
                    var migratedPoints = 0
                    try {
                        val points = DatabasePointStore.readAllPoints(source)
                        for ((type, entries) in points) {
                            for ((name, location) in entries) {
                                val exists = if (type == "spawn") DatabasePointStore.spawnPointExists(targetConnection) else DatabasePointStore.warpPointExists(targetConnection, name)
                                if (exists) continue
                                DatabasePointStore.writePoint(targetConnection, DatabaseManager.databaseType, type, name, location)
                                migratedPoints++
                            }
                        }
                    } catch (e: Exception) {
                        plugin.logger.warning(SendMessageUtil.consoleLog("system.log.fallback_migrate_points_failed", e.message))
                    }
                    plugin.logger.info(SendMessageUtil.consoleLog("system.log.fallback_migrated_back", migratedPlayers, migratedPoints))
                }
            }
            // 迁移完成：备份降级文件后删除
            backup(fallback)?.let {
                if (fallback.delete()) plugin.logger.info(SendMessageUtil.consoleLog("system.log.fallback_backup_removed", it.name))
            }
        } catch (e: Exception) {
            plugin.logger.warning(SendMessageUtil.consoleLog("system.log.fallback_migrate_back_failed", e.message))
        }
    }

    private fun playerRowExists(connection: java.sql.Connection, uuid: String): Boolean {
        return connection.prepareStatement("SELECT 1 FROM player_data WHERE uuid = ?").use { statement ->
            statement.setString(1, uuid)
            statement.executeQuery().next()
        }
    }

    // ---------- yml → 数据库 ----------

    private fun migrateFilesToDatabase() {
        val yamlStore = YamlPointStore(plugin)
        val databaseStore = DatabasePointStore(DatabaseManager)
        val target = DatabaseManager.getConnection() ?: return
        target.use { connection ->
            // spawn：yml 有数据且库中没有 → 迁移（备份仅在迁移实际发生时执行）
            if (yamlStore.hasSpawnData()) {
                val yamlSpawn = yamlStore.loadSpawn()
                if (yamlSpawn != null && !DatabasePointStore.spawnPointExists(connection)) {
                    DatabasePointStore.writePoint(connection, DatabaseManager.databaseType, "spawn", "spawn", yamlSpawn)
                    plugin.logger.info(SendMessageUtil.consoleLog("system.log.spawn_migrated_to_db"))
                    backup(yamlStore.spawnFile)?.let { plugin.logger.info(SendMessageUtil.consoleLog("system.log.spawn_yml_backed_up", it.name)) }
                }
            }
            // warp：逐个迁移库中缺失的条目
            val warpEntries = yamlStore.loadWarps()
            if (warpEntries.isNotEmpty()) {
                var migrated = 0
                for ((name, location) in warpEntries) {
                    if (DatabasePointStore.warpPointExists(connection, name)) continue
                    DatabasePointStore.writePoint(connection, DatabaseManager.databaseType, "warp", name, location)
                    migrated++
                }
                if (migrated > 0) {
                    plugin.logger.info(SendMessageUtil.consoleLog("system.log.warp_migrated_to_db", migrated))
                    backup(yamlStore.warpFile)?.let { plugin.logger.info(SendMessageUtil.consoleLog("system.log.warp_yml_backed_up", it.name)) }
                }
            }
        }
    }

    // 玩家数据 yml → 数据库（库中缺该 uuid 才迁移）
    private fun migratePlayerDataToDatabase() {
        val yamlStore = YamlPlayerDataStore(plugin)
        val folder = File(plugin.dataFolder, "playerdata")
        if (!folder.exists()) return
        val files = folder.listFiles { file -> file.extension.equals("yml", ignoreCase = true) } ?: return
        if (files.isEmpty()) return
        val target = DatabaseManager.getConnection() ?: return
        target.use { connection ->
            var migrated = 0
            for (file in files) {
                val uuid = runCatching { UUID.fromString(file.nameWithoutExtension) }.getOrNull() ?: continue
                if (playerRowExists(connection, uuid.toString())) continue
                val data = yamlStore.load(uuid) ?: continue
                if (data.homes.isEmpty() && data.lastLocation == null && data.logoutLocation == null && data.denyList.isEmpty()) continue
                DatabasePointStore.writePlayerRow(
                    connection, DatabaseManager.databaseType, uuid.toString(),
                    data.playerName ?: file.nameWithoutExtension, data.language ?: "zh_CN", data.setlang,
                    data.defaultHomeName, encodeHomes(data),
                    if (data.denyList.isEmpty()) null else data.denyList.joinToString(","),
                    encodeLocation(data.lastLocation), encodeLocation(data.logoutLocation)
                )
                migrated++
            }
            if (migrated > 0) plugin.logger.info(SendMessageUtil.consoleLog("system.log.players_migrated_to_db", migrated))
        }
    }

    // ---------- 数据库（残留 sqlite 主库） → yml（use_database 关闭时） ----------

    private fun migrateDatabasePointsToFiles() {
        val sqliteFile = File(plugin.dataFolder, "${ConfigManager.config.databaseName}.db")
        if (!sqliteFile.exists()) return
        val yamlStore = YamlPointStore(plugin)
        try {
            val connection = java.sql.DriverManager.getConnection("jdbc:sqlite:${sqliteFile.absolutePath}")
            connection.use { source ->
                val points = DatabasePointStore.readAllPoints(source)
                val spawn = points["spawn"]?.get("spawn")
                val warps = points["warp"] ?: emptyMap()
                var migrated = 0
                // spawn：yml 无数据且库里有 → 迁回
                if (spawn != null && !yamlStore.hasSpawnData()) {
                    yamlStore.saveSpawn(spawn)
                    migrated++
                }
                // warp：yml 缺失的条目迁回
                for ((name, location) in warps) {
                    if (yamlStore.containsWarp(name)) continue
                    yamlStore.saveWarp(name, location)
                    migrated++
                }
                if (migrated > 0) {
                    backup(sqliteFile)?.let { plugin.logger.info(SendMessageUtil.consoleLog("system.log.db_file_backed_up", it.name)) }
                    plugin.logger.info(SendMessageUtil.consoleLog("system.log.points_migrated_to_yml", migrated))
                }
            }
        } catch (e: Exception) {
            // 表不存在等情况静默（无数据可迁）
            plugin.logger.fine("检查数据库传送点数据未成功: ${e.message}")
        }
    }

    // ---------- 编码辅助（与 DatabasePlayerDataStore 保持一致） ----------

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

    private fun encodeHomes(data: top.craft_hello.tpa.datas.PlayerData): String {
        val gson = com.google.gson.Gson()
        return gson.toJson(data.homes.mapValues { encodeLocation(it.value) ?: "" })
    }
}
