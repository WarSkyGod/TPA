package top.craft_hello.tpa.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull

// /tpac [version/setlang/reload]：管理命令
object TpacCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpac")
            .executes { context -> executeHelp(context.source.sender) }
            .then(
                Commands.literal("version")
                    .executes { context -> TpaCommand.executeVersion(context.source.sender) }
            )
            .then(
                Commands.literal("setlang")
                    .then(
                        Commands.argument("language", StringArgumentType.word())
                            .suggests { _, builder ->
                                for (languageName in top.craft_hello.tpa.objects.LanguageManager.getLanguageNames()) {
                                    builder.suggest(languageName)
                                }
                                builder.buildFuture()
                            }
                            .executes { context ->
                                val language = context.getArgumentOrNull<String>("language")
                                    ?: return@executes SendMessageUtil.syntaxGenericError(context.source.sender, "tpac setlang <language>")
                                TpaCommand.executeSetLang(context.source.sender, language)
                            }
                    )
            )
            .then(
                Commands.literal("reload")
                    .executes { context -> TpaCommand.executeReload(context.source.sender) }
            )
            .build()
    }

    fun executeHelp(sender: org.bukkit.command.CommandSender): Int {
        return SendMessageUtil.syntaxGenericError(sender, "tpac [version/setlang/reload]")
    }
}
