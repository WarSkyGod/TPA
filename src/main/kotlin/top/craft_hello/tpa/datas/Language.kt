package top.craft_hello.tpa.datas

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files

data class Language(var languageFile: File, var isReplace: Boolean) {
    val plugin = TPA.plugin
    var language: FileConfiguration
    var miniMessage = MiniMessage.miniMessage()
    init {
        language = loadLanguage(languageFile, isReplace)
    }

    constructor(languageFile: File): this(languageFile, false)

    private fun loadLanguage(languageFile: File, isReplace: Boolean): FileConfiguration {

        if (isReplace || !languageFile.exists()) {
            plugin.saveResource(buildString {
                append("language/")
                append(languageFile.name)
            }, isReplace)
            if (!languageFile.exists()){
                plugin.saveResource(buildString {
                    append("language/")
                    append(ConfigManager.config.language)
                    append(".yml")
                }, isReplace)
            }
        }
        return YamlConfiguration.loadConfiguration(
            InputStreamReader(
                Files.newInputStream(languageFile.toPath()), StandardCharsets.UTF_8
            )
        )
    }

    private fun formatText(text: String): Component {
        /*return text.replace(Regex("&([0-9a-fk-or])")) { match ->
            "§${match.groupValues[1]}"
        }*/
        return miniMessage.deserialize(text)
    }

    private fun formatText(text: String, vararg vars: String): Component {
        when (vars.size){
            1 -> {
                return formatText(text
                    .replace("{target}", vars[0])
                    .replace("{command}", vars[0])
                    .replace("{message}", vars[0])
                    .replace("{max_home_amount}", vars[0])
                    .replace("{seconds}", vars[0]))

            }

            2 -> {
                return formatText(text.replace("{target}", vars[0])
                    .replace("{seconds}", vars[1]))
            }

            else -> {
                return formatText(text)
            }
        }
    }

    fun getPrefix(): String {
        return getPrefix(Bukkit.getConsoleSender())
    }

    fun getPrefix(sender: CommandSender): String {
        return if(sender is Player) getMessage("prefix") else getMessage("console_prefix")
    }

    fun getMessage(path: String): String {
        return language.getString(path) ?: "null"
    }

    fun getFormatMessage(path: String, vararg vars: String): Component {
        return formatText(getMessage(path), *vars)
    }

    fun getPrefixMessage(path: String): String {
        return buildString {
            append(getPrefix())
            append(getMessage(path))
        }
    }

    fun getPrefixMessage(sender: CommandSender, path: String): String {
        return buildString {
            append(getPrefix(sender))
            append(getMessage(path))
        }
    }

    fun getFormatPrefixMessage(path: String): Component {
        return formatText(getPrefixMessage(path))
    }

    fun getFormatPrefixMessage(path: String, vararg vars: String): Component {
        return formatText(getPrefixMessage(path), *vars)
    }

    fun getFormatPrefixMessage(sender: CommandSender, path: String, vararg vars: String): Component {
        return formatText(getPrefixMessage(sender, path), *vars)
    }
}
