package top.craft_hello.tpa.objects

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
// 3. 旧 config.yml 原样备份到 backup/<旧版本号>/config.yml（对齐 3.x renameTo 路径）
//    旧 language/ 目录整体备份到 backup/<旧版本号>/language/，
//    随后由 Language 惰性提取机制从 jar 重新生成新版语言文件（根治旧文件缺新键）
// 4. 以 jar 内新版 config.yml 为基础，旧配置中仍存在于新版的键用旧值覆盖
//    （用户自定义保留；新版已移除/改名的旧键丢弃；新版新增键保持默认值）
// 5. 写回 version 为当前插件版本（对齐 3.x configuration.set("version", VERSION)），
//    发送 system.config_migrated_success（对齐 3.x offUpdateConfiguration）
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

        // 新版配置为基础 + 旧值合并
        val newConfig = loadBundledConfig(plugin)
        mergeSection(oldConfig, newConfig)
        // 对齐 3.x：写回当前版本号，下次启动不再触发
        newConfig.set("version", currentVersion)
        newConfig.save(configFile)

        // 对齐 3.x offUpdateConfiguration：迁移完成消息
        SendMessageUtil.configMigratedSuccessNotice()
        plugin.logger.info("已迁移旧版配置（$configVersion → $currentVersion）：用户自定义值已保留，旧语言文件已备份至 plugins/TPA/backup/$configVersion/ 并重新生成新版语言文件")

        // 刷新配置包装（ConfigManager 单例在 onEnable 后续流程使用的是迁移后的新值）
        ConfigManager.reloadConfig()
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
