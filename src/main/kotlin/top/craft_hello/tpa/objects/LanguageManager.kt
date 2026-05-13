package top.craft_hello.tpa.objects

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.Language
import top.craft_hello.tpa.enums.LanguageType
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
                if(!languages.equals(languageName)) languages[languageName] = Language(languageFile, false)
            }
        }
    }

    fun loadLanguage(path: String, isReplace: Boolean): Language {
        return Language(File(path), isReplace)
    }

    fun getLanguage(languageName: String): Language {
        return languages[languageName] ?: languages[ConfigManager.config.language]!!
    }

    fun getLanguage(languageType: LanguageType): Language {
        return getLanguage(languageType.languageName)
    }

    fun getLanguage(sender : CommandSender): Language {
        return if(sender is Player) getLanguage(buildString {
            append(sender.locale().language)
            append("_")
            append(sender.locale().country)
        }) else getLanguage(ConfigManager.config.language)
    }

    fun reloadLanguage() {
        languages.clear()
        loadAllLanguage()
    }
}