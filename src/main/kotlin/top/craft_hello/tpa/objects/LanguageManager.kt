package top.craft_hello.tpa.objects

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.Language
import top.craft_hello.tpa.enums.LanguageType
import top.craft_hello.tpa.utils.LocaleUtil
import java.io.File

object LanguageManager {
    val plugin = TPA.plugin
    val languages = mutableMapOf<String, Language>()

    init {
        loadAllLanguage()
    }

    fun loadAllLanguage() {
        // 加载插件自带的语言文件
        for (languageType in LanguageType.entries) {
            languages[languageType.languageName] = loadLanguage(buildString {
                append(plugin.dataFolder)
                append("/language/")
                append(languageType.languageName)
                append(".yml")
            }, false)
        }
        // 加载自定义语言文件
        val langFolder = File(plugin.dataFolder, "/language").listFiles()
        if(langFolder != null){
            for (languageFile in langFolder) {
                val languageName = languageFile.name.replace(".yml", "")
                if(!languages.containsKey(languageName)) languages[languageName] = Language(languageFile, false)
            }
        }
    }

    fun loadLanguage(path: String, isReplace: Boolean): Language {
        return Language(File(path), isReplace)
    }

    fun getLanguage(languageName: String): Language {
        return languages[languageName]
            ?: languages[ConfigManager.config.language]
            ?: languages.values.first()
    }

    fun getLanguage(languageType: LanguageType): Language {
        return getLanguage(languageType.languageName)
    }

    // 语言选择优先级：玩家手动设置的语言（/tpa setlang）> 客户端语言 > 配置默认语言
    fun getLanguage(sender : CommandSender): Language {
        if (sender is Player) {
            val playerData = PlayerDataManager.get(sender)
            if (playerData.setlang && !playerData.language.isNullOrBlank()) {
                return getLanguage(playerData.language!!)
            }
            val clientLanguage = LocaleUtil.playerLocale(sender) ?: ""
            if (languages.containsKey(clientLanguage)) return languages[clientLanguage]!!
        }
        return getLanguage(ConfigManager.config.language)
    }

    // 语言名是否存在（大小写不敏感）
    fun hasLanguage(languageName: String): Boolean {
        return languages.keys.any { it.equals(languageName, ignoreCase = true) }
    }

    // 全部语言名
    fun getLanguageNames(): List<String> = languages.keys.toList()

    // 规范化语言名：zh_cn -> zh_CN
    fun formatLangStr(languageName: String): String {
        val parts = languageName.lowercase().split("_")
        if (parts.size < 2) return languageName.lowercase()
        return buildString {
            append(parts[0])
            append("_")
            append(parts[1].uppercase())
        }
    }

    fun reloadLanguage() {
        languages.clear()
        loadAllLanguage()
    }
}