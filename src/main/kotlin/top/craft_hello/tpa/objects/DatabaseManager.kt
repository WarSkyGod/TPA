package top.craft_hello.tpa.objects

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.utils.SendMessageUtil
import java.io.File
import java.sql.Connection
import java.sql.SQLException

// 数据库连接池（单例）。
// - use_database=false 时不初始化
// - 配置 mysql 连接失败时自动降级为本地 SQLite（data_fallback.db），并在配置修复后由
//   StorageMigrator 将降级数据自动迁回数据库（迁前复制到 backup/）
// - 表：player_data（玩家数据）+ tpa_points（spawn/warp 传送点）
object DatabaseManager {
    private lateinit var plugin: TPA
    private var dataSource: HikariDataSource? = null

    // 当前实际生效的数据库类型：sqlite / mysql（供存储层区分 upsert 方言）
    var databaseType: String = "sqlite"
        private set

    // 是否处于降级状态（配置 mysql 但连接失败，实际使用本地 sqlite 兜底库）
    var isFallback: Boolean = false
        private set

    // 降级兜底库文件名
    val fallbackFileName: String = "data_fallback.db"

    // 主库（配置指定类型）是否当前可用
    fun isAvailable(): Boolean = dataSource != null

    // 降级兜底库文件
    fun fallbackFile(): File = File(plugin.dataFolder, fallbackFileName)

    // 初始化数据库连接池（按配置；未启用数据库时跳过）。返回是否获得可用连接。
    fun setupDatabase(plugin: TPA): Boolean {
        this.plugin = plugin
        closeDataSource()
        val config = ConfigManager.config
        if (!config.useDatabase) {
            plugin.logger.info(SendMessageUtil.consoleLog("system.log.database_disabled"))
            return false
        }
        val requestedType = config.databaseType.lowercase()

        // 1. 按配置连接（initializationFailTimeout 快速失败，避免 reload 卡 30 秒）
        try {
            dataSource = createPool(requestedType)
            databaseType = requestedType
            isFallback = false
            ensureTables()
            plugin.logger.info(SendMessageUtil.consoleLog("system.log.database_pool_initialized", databaseType))
            return true
        } catch (e: Exception) {
            plugin.logger.warning(SendMessageUtil.consoleLog("system.log.database_connect_failed", requestedType, e.message))
        }

        // 2. 配置 mysql 连接失败 → 降级本地 SQLite
        if (requestedType == "mysql") {
            try {
                dataSource = createPool("sqlite", fallbackFile())
                databaseType = "sqlite"
                isFallback = true
                ensureTables()
                plugin.logger.warning(SendMessageUtil.consoleLog("system.log.database_fallback_sqlite", fallbackFileName))
                return true
            } catch (e: Exception) {
                plugin.logger.severe(SendMessageUtil.consoleLog("system.log.database_fallback_create_failed", e.message))
            }
        }

        plugin.logger.severe(SendMessageUtil.consoleLog("system.log.database_memory_only"))
        dataSource = null
        isFallback = false
        return false
    }

    private fun createPool(type: String, sqliteFile: File? = null): HikariDataSource {
        val config = ConfigManager.config
        val hikari = HikariConfig().apply {
            when (type) {
                "sqlite" -> {
                    jdbcUrl = "jdbc:sqlite:${sqliteFile?.absolutePath ?: "${plugin.dataFolder.absolutePath}/${config.databaseName}.db"}"
                    driverClassName = "org.sqlite.JDBC"
                    maximumPoolSize = 1
                }

                "mysql" -> {
                    jdbcUrl = buildString {
                        append("jdbc:mysql://")
                        append(config.databaseAddress)
                        append(":")
                        append(config.databasePort)
                        append("/")
                        append(config.databaseName)
                    }
                    username = config.databaseUsername
                    password = config.databasePassword
                    driverClassName = "com.mysql.cj.jdbc.Driver"
                }

                else -> throw IllegalArgumentException(SendMessageUtil.consoleLog("system.log.unsupported_database_type", type))
            }

            // 连接池配置（initializationFailTimeout 快速失败：5 秒内连不上直接抛异常走降级）
            minimumIdle = 2
            maximumPoolSize = 4
            connectionTimeout = 30000
            idleTimeout = 600000
            maxLifetime = 1800000
            initializationFailTimeout = 5000
        }
        return HikariDataSource(hikari)
    }

    // 获取数据库连接
    fun getConnection(): Connection? {
        return try {
            dataSource?.connection
        } catch (e: SQLException) {
            plugin.logger.severe(SendMessageUtil.consoleLog("system.log.database_get_connection_failed", e.message))
            null
        }
    }

    // 关闭连接池
    fun closeDataSource() {
        if (dataSource != null) {
            dataSource?.close()
            dataSource = null
            if (this::plugin.isInitialized) plugin.logger.info(SendMessageUtil.consoleLog("system.log.database_pool_closed"))
        }
    }

    // 确保表存在；旧表自动补充缺失列
    private fun ensureTables() {
        getConnection()?.use { connection ->
            ensurePlayerDataTable(connection)
            ensurePointsTable(connection)
        }
    }

    private fun ensurePlayerDataTable(connection: Connection) {
        val tableExists = checkTableExists(connection, "player_data")
        if (!tableExists) {
            connection.createStatement().use { statement ->
                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS player_data (
                        uuid VARCHAR(36) PRIMARY KEY,
                        player_name VARCHAR(255) NOT NULL,
                        language VARCHAR(10) DEFAULT 'zh_CN',
                        setlang BOOLEAN DEFAULT FALSE,
                        default_home VARCHAR(255),
                        homes TEXT,
                        deny_list TEXT,
                        last_location TEXT,
                        logout_location TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """.trimIndent()
                )
            }
            plugin.logger.info(SendMessageUtil.consoleLog("system.log.database_table_created", "player_data"))
        } else {
            upgradeTable(connection)
            plugin.logger.info(SendMessageUtil.consoleLog("system.log.database_table_exists", "player_data"))
        }
    }

    // 确保传送点表存在（spawn/warp 统一存储）
    private fun ensurePointsTable(connection: Connection) {
        if (checkTableExists(connection, "tpa_points")) return
        connection.createStatement().use { statement ->
            statement.execute(
                """
                CREATE TABLE IF NOT EXISTS tpa_points (
                    point_type VARCHAR(16) NOT NULL,
                    point_name VARCHAR(255) NOT NULL,
                    world_name VARCHAR(255) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw DOUBLE NOT NULL,
                    pitch DOUBLE NOT NULL,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (point_type, point_name)
                )
                """.trimIndent()
            )
        }
        plugin.logger.info(SendMessageUtil.consoleLog("system.log.database_table_points_created"))
    }

    // 检查表是否存在
    private fun checkTableExists(connection: Connection, tableName: String): Boolean {
        val metaData = connection.metaData
        val resultSet = metaData.getTables(null, null, tableName, arrayOf("TABLE"))
        return resultSet.next()
    }

    // 旧表升级：补充 4.0 新增列（幂等）
    private fun upgradeTable(connection: Connection) {
        for (column in listOf("deny_list TEXT", "logout_location TEXT")) {
            val name = column.substringBefore(" ")
            if (!columnExists(connection, "player_data", name)) {
                try {
                    connection.createStatement().use { statement ->
                        statement.execute("ALTER TABLE player_data ADD COLUMN $column")
                    }
                    plugin.logger.info(SendMessageUtil.consoleLog("system.log.database_column_added", name))
                } catch (e: Exception) {
                    plugin.logger.warning(SendMessageUtil.consoleLog("system.log.database_column_add_failed", name, e.message))
                }
            }
        }
    }

    private fun columnExists(connection: Connection, table: String, column: String): Boolean {
        val metaData = connection.metaData
        val resultSet = metaData.getColumns(null, null, table, column)
        return resultSet.next()
    }
}
