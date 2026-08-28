package top.craft_hello.tpa.objects

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import top.craft_hello.tpa.TPA
import java.sql.Connection
import java.sql.SQLException

// 数据库连接池（单例）。use_database=false 时不初始化。
// 表在 4.0 基础上补充 deny_list、logout_location 列，旧库自动升级。
object DatabaseManager {
    private lateinit var plugin: TPA
    private var dataSource: HikariDataSource? = null

    // 当前数据库类型：sqlite / mysql（供存储层区分 upsert 方言）
    var databaseType: String = "sqlite"
        private set

    // 初始化数据库连接池（按配置；未启用数据库时跳过）
    fun setupDatabase(plugin: TPA) {
        this.plugin = plugin
        val config = ConfigManager.config
        if (!config.useDatabase) {
            plugin.logger.info("未启用数据库存储（use_database: false），玩家数据将保存到 playerdata 目录")
            return
        }
        databaseType = config.databaseType.lowercase()
        try {
            val hikari = HikariConfig().apply {
                when (databaseType) {
                    "sqlite" -> {
                        jdbcUrl = buildString {
                            append("jdbc:sqlite:${plugin.dataFolder.absolutePath}/")
                            append(config.databaseName)
                            append(".db")
                        }
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

                    else -> throw IllegalArgumentException("不支持的数据库类型: $databaseType")
                }

                // 连接池配置
                minimumIdle = 2
                connectionTimeout = 30000
                idleTimeout = 600000
                maxLifetime = 1800000
            }

            dataSource = HikariDataSource(hikari)
            plugin.logger.info("数据库连接池已初始化（$databaseType）")

            // 确保表存在
            ensureTableExists()

        } catch (e: Exception) {
            plugin.logger.severe("数据库初始化失败: ${e.message}")
        }
    }

    // 获取数据库连接
    fun getConnection(): Connection? {
        return try {
            dataSource?.connection
        } catch (e: SQLException) {
            plugin.logger.severe("获取数据库连接失败: ${e.message}")
            null
        }
    }

    // 关闭连接池
    fun closeDataSource() {
        dataSource?.close()
        plugin.logger.info("数据库连接池已关闭")
    }

    // 确保表存在，如果不存在则创建；旧表自动补充缺失列
    private fun ensureTableExists() {
        getConnection()?.use { connection ->
            val tableExists = checkTableExists(connection, "player_data")
            if (!tableExists) {
                createPlayerDataTable(connection)
                plugin.logger.info("已创建 player_data 表")
            } else {
                upgradeTable(connection)
                plugin.logger.info("player_data 表已存在")
            }
        }
    }

    // 检查表是否存在
    private fun checkTableExists(connection: Connection, tableName: String): Boolean {
        val metaData = connection.metaData
        val resultSet = metaData.getTables(null, null, tableName, arrayOf("TABLE"))
        return resultSet.next()
    }

    // 创建 player_data 表
    private fun createPlayerDataTable(connection: Connection) {
        val createTableSQL = """
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

        connection.createStatement().use { statement ->
            statement.execute(createTableSQL)
        }
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
                    plugin.logger.info("player_data 表已补充列 $name")
                } catch (e: Exception) {
                    plugin.logger.warning("player_data 表补充列 $name 失败: ${e.message}")
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
