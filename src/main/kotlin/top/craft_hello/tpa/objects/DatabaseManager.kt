package top.craft_hello.tpa.objects

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.SQLException

class DatabaseManager(private val plugin: JavaPlugin) {
    private var dataSource: HikariDataSource? = null

    // 初始化数据库连接池
    fun setupDatabase() {
        val databaseType = "sqlite"
        try {
            val config = HikariConfig().apply {
                when (databaseType) {
                    "sqlite" -> {
                        jdbcUrl = buildString{
                            append("jdbc:sqlite:${plugin.dataFolder.absolutePath}/")
                            append("database")
                            append(".db")
                        }
                        driverClassName = "org.sqlite.JDBC"
                    }

                    "mysql" -> {
                        jdbcUrl = buildString{
                            append("jdbc:mysql://")
                            append("localhost")
                            append(":")
                            append(3306)
                            append("/")
                            append("mydatabase")
                        }
                        username = "username"
                        password = "password"
                        driverClassName = "com.mysql.cj.jdbc.Driver"
                    }
                }

                // 连接池配置
                maximumPoolSize = 10
                minimumIdle = 2
                connectionTimeout = 30000
                idleTimeout = 600000
                maxLifetime = 1800000
            }

            dataSource = HikariDataSource(config)
            plugin.logger.info("数据库连接池已初始化")

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

    // 确保表存在，如果不存在则创建
    private fun ensureTableExists() {
        getConnection()?.use { connection ->
            // 检查表是否存在
            val tableExists = checkTableExists(connection, "player_data")

            if (!tableExists) {
                // 创建表
                createPlayerDataTable(connection)
                plugin.logger.info("已创建 player_data 表")
            } else {
                plugin.logger.info("player_data 表已存在")
            }
        }
    }

    // 检查表是否存在
    private fun checkTableExists(connection: Connection, tableName: String): Boolean {
        val metaData = connection.metaData
        var flag : Boolean
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
                    last_location TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """.trimIndent()

        connection.createStatement().use { statement ->
            statement.execute(createTableSQL)
        }
    }
}