package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /delhome <名称>：删除家
object DelHomeCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("delhome")
            .requires { ConfigManager.config.isEnableCommand(CommandType.DEL_HOME) }
            .executes { context -> SafeGuard.command(context) { executeDelHome(context.source.sender, emptyList()) } }
            .then(
                Commands.argument("home", StringArgumentType.word())
                    .suggests { context, builder ->
                        val sender = context.source.sender
                        val input = builder.remaining.lowercase()
                        if (sender is Player) {
                            for (homeName in PlayerDataManager.get(sender).homeNames()) {
                                if (homeName.lowercase().contains(input)) builder.suggest(homeName)
                            }
                        }
                        builder.buildFuture()
                    }
                    .executes { context ->
                        SafeGuard.command(context) {
                            executeDelHome(context.source.sender, listOfNotNull(context.getArgumentOrNull<String>("home")))
                        }
                    }
            )
            .build()
    }

    // /delhome <名称>：删除家（Brigadier 与 legacy 路由共用）
    fun executeDelHome(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.DEL_HOME)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.DEL_HOME)) return SendMessageUtil.permissionDeniedError(sender)

        val homeName = args.getOrNull(0)
            ?: return SendMessageUtil.syntaxHomeError(sender, "delhome")
        val playerData = PlayerDataManager.get(sender)
        if (!playerData.delHome(homeName)) return SendMessageUtil.homeNotFoundError(sender, homeName)
        PlayerDataManager.save(sender)
        return SendMessageUtil.delHomeSuccess(sender, homeName)
    }
}
