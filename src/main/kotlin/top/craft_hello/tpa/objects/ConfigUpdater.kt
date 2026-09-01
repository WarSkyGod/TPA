package top.craft_hello.tpa.objects

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.VersionUtil
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

// 老版本配置自动迁移（对齐 3.x configVersionCheck/updateConfiguration 逻辑，幂等）：
// 1. 版本判断：config.yml 的 version 键（缺失视为 "1.0"），比当前插件版本旧则触发
// 2. 发送 system.config_migrated 控制台消息（对齐 3.x configVersionUpdate）
// 3. 备份到 backup/<旧版本号>/（对齐 3.x renameTo 路径）：
//    config.yml 原样 + language/ 整目录（随后由 Language 惰性提取机制从 jar
//    重新生成新版语言文件，根治旧文件缺新键）+ playerdata/ 原样
// 4. config.yml 合并：以 jar 内新版为基础，旧配置中仍存在于新版的键用旧值覆盖
//    （用户自定义保留；新版已移除/改名的旧键丢弃；新版新增键保持默认值）
// 5. playerdata 迁移：3.x 的 Location 为手工分键（<path>.world 存世界名字符串，
//    .x/.y/.z/.pitch/.yaw 跟随），4.0 使用 Bukkit Location 配置序列化
//    （getLocation 读取，world 为 UUID 的 == 标记格式），手工分键直接读会全为
//    null 导致家/回溯位置丢失——此处把 homes/<名>、last_location、logout_location
//    的手工分键转换为 Location 序列化写回；并对齐 3.x 旧迁移逻辑处理
//    lang→language、denys→deny_list 等键名变更
// 6. spawn.yml 与 3.x 同构（spawn.world/...）无需迁移；warp.yml 的 3.x 根级
//    手工分键已由 YamlPointStore 双格式兼容读取，亦无需迁移
// 7. 写回 version 为当前插件版本（对齐 3.x），发送 system.config_migrated_success
object ConfigUpdater {

    fun migrateIfNeeded(plugin: TPA) {
        val dataFolder = plugin.dataFolder
        val configFile = File(dataFolder, "config.yml")
        // 首次安装：无旧配置，saveDefaultConfig 会生成当前版本配置，无需迁移
        if (!configFile.exists()) return

        val oldConfig = YamlConfiguration.loadConfiguration(configFile)
        // 对齐 3.x configVersionCheck：version 缺失视为 "1.0"（最老），旧于当前版本则迁移
        val configVersion = oldConfig.getString("version") ?: "1.0"
        val currentVersion = plugin.pluginMeta.version
        if (!VersionUtil.versionComparison(currentVersion, configVersion)) return

        // 对齐 3.x：迁移开始消息
        SendMessageUtil.configMigratedNotice()

        val backupDir = File(dataFolder, "backup/$configVersion")
        backupDir.mkdirs()

        // 旧 config.yml 原样备份（回滚保险；3.x 为 renameTo，此处 copy 保留原文件供合并）
        configFile.copyTo(File(backupDir, configFile.name), overwrite = true)

        // 旧语言文件整目录备份（复制而非剪切：避免运行中文件占用导致移走失败）
        val languageDir = File(dataFolder, "language")
        if (languageDir.isDirectory) {
            copyDirectory(languageDir, File(backupDir, languageDir.name))
            languageDir.deleteRecursively()
        }

        // 旧 playerdata 原样备份（逐文件在转换前备份，未转换的世界缺失数据不丢）
        migratePlayerdata(plugin, backupDir)

        // 新版配置为基础 + 旧值合并
        val newConfig = loadBundledConfig(plugin)
        mergeSection(oldConfig, newConfig)
        // 对齐 3.x：写回当前版本号，下次启动不再触发
        newConfig.set("version", currentVersion)
        newConfig.save(configFile)

        // 对齐 3.x offUpdateConfiguration：迁移完成消息
        SendMessageUtil.configMigratedSuccessNotice()
        plugin.logger.info(SendMessageUtil.consoleLog("system.log.config_migrated_detail", configVersion, currentVersion))

        // 刷新配置包装（ConfigManager 单例在 onEnable 后续流程使用的是迁移后的新值）
        ConfigManager.reloadConfig()
    }

    // playerdata/<uuid>.yml：3.x 手工分键 Location → 4.0 Bukkit Location 序列化
    // （homes.<名>、last_location、logout_location；世界缺失的条目保留原样不丢数据）
    private fun migratePlayerdata(plugin: TPA, backupDir: File) {
        val folder = File(plugin.dataFolder, "playerdata")
        val files = folder.listFiles { file -> file.extension.equals("yml", ignoreCase = true) } ?: return
        val playerdataBackup = File(backupDir, folder.name)
        for (file in files) {
            val config = YamlConfiguration.loadConfiguration(file)
            var migrated = false
            // 对齐 3.x 旧迁移逻辑的键名变更（更老版本直接升级的场景）
            for ((oldKey, newKey) in listOf("lang" to "language", "denys" to "deny_list")) {
                if (config.contains(oldKey) && !config.contains(newKey)) {
                    config.set(newKey, config.get(oldKey))
                    config.set(oldKey, null)
                    migrated = true
                }
            }
            // homes.<名> 手工分键 → Location 序列化
            for (name in config.getConfigurationSection("homes")?.getKeys(false) ?: emptySet()) {
                if (convertManualLocation(config, "homes.$name")) migrated = true
            }
            // last_location / logout_location 手工分键 → Location 序列化
            for (path in listOf("last_location", "logout_location")) {
                if (convertManualLocation(config, path)) migrated = true
            }
            if (migrated) {
                playerdataBackup.mkdirs()
                file.copyTo(File(playerdataBackup, file.name), overwrite = true)
                config.save(file)
            }
        }
    }

    // 手工分键（<path>.world=世界名 + x/y/z/yaw/pitch）→ Bukkit Location 序列化。
    // 已是 Location 序列化（4.0 格式）或无 world 子键（更老的未知格式）则返回 false 保留原样
    private fun convertManualLocation(config: YamlConfiguration, path: String): Boolean {
        if (config.get(path) is Location) return false // 已是 4.0 格式
        val section: ConfigurationSection = config.getConfigurationSection(path) ?: return false
        if (!section.contains("world")) return false
        val world = Bukkit.getWorld(section.getString("world") ?: return false) ?: return false // 世界未加载：保留原样
        config.set(
            path,
            Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                section.getDouble("yaw").toFloat(),
                section.getDouble("pitch").toFloat()
            )
        )
        return true
    }

    // 旧配置的值覆盖到新配置（递归 section）：只覆盖新配置中存在的键，
    // 避免旧版残留键污染新版结构；新配置没有的旧键（已移除/改名）自然丢弃
    private fun mergeSection(old: ConfigurationSection, new: ConfigurationSection) {
        for (key in old.getKeys(false)) {
            val oldValue = old.get(key) ?: continue
            if (oldValue is ConfigurationSection) {
                val newSection = new.getConfigurationSection(key)
                if (newSection != null) mergeSection(oldValue, newSection)
            } else if (new.contains(key)) {
                new.set(key, oldValue)
            }
        }
    }

    // 从 jar 读取打包的新版 config.yml（UTF-8，version 为 gradle 注入的当前版本）
    private fun loadBundledConfig(plugin: TPA): YamlConfiguration {
        val stream = plugin.getResource("config.yml") ?: return YamlConfiguration()
        return YamlConfiguration.loadConfiguration(InputStreamReader(stream, StandardCharsets.UTF_8))
    }

    private fun copyDirectory(source: File, target: File) {
        if (source.isDirectory) {
            target.mkdirs()
            source.listFiles()?.forEach { copyDirectory(it, File(target, it.name)) }
        } else {
            source.copyTo(target, overwrite = true)
        }
    }
}
