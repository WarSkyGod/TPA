package top.craft_hello.tpa.objects

import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.Config
import top.craft_hello.tpa.datas.SpawnConfig
import top.craft_hello.tpa.datas.WarpConfig

object ConfigManager {
    val plugin = TPA.plugin
    var config: Config
    var spawnConfig: SpawnConfig
    var warpConfig: WarpConfig

    init {
        plugin.saveDefaultConfig()
        config = Config(plugin.config)
        spawnConfig = SpawnConfig(plugin)
        warpConfig = WarpConfig(plugin)
    }

    // 按当前配置创建传送点存储：数据库启用且连接可用 → tpa_points 表；否则 yml 文件
    fun createPointStore(): PointStore {
        return if (config.useDatabase && DatabaseManager.isAvailable()) {
            DatabasePointStore(DatabaseManager)
        } else {
            YamlPointStore(plugin)
        }
    }

    // 数据库初始化与迁移完成后重建传送点存储薄壳（启动与 reload 共用）
    fun reinitDataStores() {
        spawnConfig = SpawnConfig(plugin)
        warpConfig = WarpConfig(plugin)
    }

    fun reloadConfig() {
        plugin.reloadConfig()
        config = Config(plugin.config)
    }

    fun reloadSpawnConfig() { spawnConfig = SpawnConfig(plugin) }

    fun reloadWarpConfig() { warpConfig = WarpConfig(plugin) }

    fun reloadLanguages() { LanguageManager.reloadLanguage() }

    // 热重载全部配置与存储层（/tpac reload）：
    // 1. 玩家缓存全量落盘旧存储并清空
    // 2. 重读配置 → 重连数据库（失败自动降级 sqlite）
    // 3. 自动迁移（降级库恢复 / yml↔数据库双向，迁前备份到 backup/）
    // 4. 玩家数据存储按新配置重建（yml→db 全量迁移）
    // 5. spawn/warp 存储薄壳重建 + 语言重载
    fun reloadAllConfig() {
        PlayerDataManager.unloadAll()
        reloadConfig()
        DatabaseManager.setupDatabase(plugin)
        StorageMigrator.migrateAll()
        PlayerDataManager.init(plugin)
        reinitDataStores()
        reloadLanguages()
    }
}
