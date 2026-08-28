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
import top.craft_hello.tpa.utils.PapiHook
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files

// 语言文件封装（MiniMessage 格式）。
// 占位符采用 4.0 重构设计：%target%、%seconds%、%command%、%message%、%max_home_amount%。
// 若服务器安装了 PlaceholderAPI，消息会先经过 PAPI 占位符处理（可选依赖）。
data class Language(var languageFile: File, var isReplace: Boolean) {
    val plugin = TPA.plugin
    var languageConfig: FileConfiguration
    private val miniMessage = MiniMessage.miniMessage()

    init {
        languageConfig = loadLanguage(languageFile, isReplace)
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
        return miniMessage.deserialize(text)
    }

    // 替换占位符并组件化。
    // 单变量时 %target%/%command%/%message%/%max_home_amount%/%seconds% 共用该值；
    // 双变量时 %target% 取第一个，%command%/%message%/%max_home_amount%/%seconds% 取第二个。
    // 变量值来自玩家名等外部输入，先做 MiniMessage 转义，避免注入标签。
    private fun formatText(text: String, vararg vars: String): Component {
        val escaped = vars.map { MiniMessage.miniMessage().escapeTags(it) }
        var t = text
        when (vars.size){
            1 -> {
                t = t.replace("%target%", escaped[0])
                    .replace("%command%", escaped[0])
                    .replace("%message%", escaped[0])
                    .replace("%max_home_amount%", escaped[0])
                    .replace("%seconds%", escaped[0])
            }
            2 -> {
                t = t.replace("%target%", escaped[0])
                    .replace("%command%", escaped[1])
                    .replace("%message%", escaped[1])
                    .replace("%max_home_amount%", escaped[1])
                    .replace("%seconds%", escaped[1])
            }
        }
        return formatText(t)
    }

    fun getPrefix(): String {
        return getPrefix(Bukkit.getConsoleSender())
    }

    fun getPrefix(sender: CommandSender): String {
        return if(sender is Player) getMessage("prefix") else getMessage("console_prefix")
    }

    fun getMessage(path: String): String {
        return languageConfig.getString(path) ?: "null"
    }

    // 读取原文并透传 PlaceholderAPI 占位符（玩家可用时）
    fun getRawMessage(sender: CommandSender?, path: String): String {
        val raw = getMessage(path)
        return if (sender is Player) PapiHook.setPlaceholders(sender, raw) else raw
    }

    fun getFormatMessage(path: String, vararg vars: String): Component {
        return formatText(getMessage(path), *vars)
    }

    fun getFormatMessage(sender: CommandSender, path: String, vararg vars: String): Component {
        return formatText(getRawMessage(sender, path), *vars)
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
            append(getRawMessage(sender, path))
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
