package top.craft_hello.tpa.commands

import cn.handyplus.lib.adapter.HandySchedulerUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.VersionUtil

// /tpac [version/setlang/reload]：管理命令（4.0 特意从 /tpa 拆出的职责）
object TpacCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpac")
            .executes { context -> SafeGuard.command(context) { executeHelp(context.source.sender) } }
            .then(
                Commands.literal("version")
                    .executes { context -> SafeGuard.command(context) { executeVersion(context.source.sender) } }
            )
            .then(
                Commands.literal("setlang")
                    .then(
                        Commands.argument("language", StringArgumentType.word())
                            .suggests { context, builder ->
                                val input = builder.remaining.lowercase()
                                if ("clear".contains(input)) builder.suggest("clear")
                                for (languageName in LanguageManager.getLanguageNames()) {
                                    if (languageName.lowercase().contains(input)) builder.suggest(languageName)
                                }
                                builder.buildFuture()
                            }
                            .executes { context ->
                                SafeGuard.command(context) {
                                    val language = context.getArgumentOrNull<String>("language")
                                        ?: return@command SendMessageUtil.syntaxGenericError(context.source.sender, "tpac setlang <language/clear>")
                                    executeSetLang(context.source.sender, language)
                                }
                            }
                    )
            )
            .then(
                Commands.literal("reload")
                    .executes { context -> SafeGuard.command(context) { executeReload(context.source.sender) } }
            )
            .build()
    }

    // 无参数：提示正确用法
    fun executeHelp(sender: CommandSender): Int {
        return SendMessageUtil.syntaxGenericError(sender, "tpac [version/setlang/reload]")
    }

    // 检查插件更新（GitHub Releases Latest）
    fun executeVersion(sender: CommandSender): Int {
        if (!PermissionType.hasPermission(sender, PermissionType.VERSION)) {
            return SendMessageUtil.permissionDeniedError(sender)
        }
        HandySchedulerUtil.runTaskAsynchronously {
            VersionUtil.updateCheck(sender)
        }
        return Command.SINGLE_SUCCESS
    }

    // 设置玩家语言；clear 恢复为根据客户端自动匹配（3.x 设计）
    fun executeSetLang(sender: CommandSender, language: String): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        val playerData = PlayerDataManager.get(sender)
        if (language.equals("clear", ignoreCase = true)) {
            playerData.setlang = false
            // 同步 language 为当前客户端语言（老服务器无法获取客户端语言时由跟随逻辑回退配置默认）
            val clientLanguage = LanguageManager.formatLangStr(
                buildString {
                    append(sender.locale().language)
                    append("_")
                    append(sender.locale().country)
                }
            )
            if (LanguageManager.hasLanguage(clientLanguage)) playerData.language = clientLanguage
            PlayerDataManager.save(sender)
            SendMessageUtil.setLangCommandSuccess(sender, LanguageManager.getLanguage(sender).languageFile.nameWithoutExtension)
            return Command.SINGLE_SUCCESS
        }
        if (!LanguageManager.hasLanguage(language)) return SendMessageUtil.syntaxGenericError(sender, "tpac setlang <language/clear>")
        val languageName = LanguageManager.formatLangStr(language)
        playerData.language = languageName
        playerData.setlang = true
        PlayerDataManager.save(sender)
        SendMessageUtil.setLangCommandSuccess(sender, languageName)
        return Command.SINGLE_SUCCESS
    }

    // 重载全部配置
    fun executeReload(sender: CommandSender): Int {
        if (!PermissionType.hasPermission(sender, PermissionType.RELOAD)) {
            return SendMessageUtil.permissionDeniedError(sender)
        }
        ConfigManager.reloadAllConfig()
        return SendMessageUtil.configReloaded(sender)
    }
}
