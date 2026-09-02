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

// /setdefaulthome <名称>：设置默认家
object SetDefaultHomeCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("setdefaulthome")
            .requires { ConfigManager.config.isEnableCommand(CommandType.SET_DEFAULT_HOME) }
            .executes { context -> SafeGuard.command(context) { executeSetDefaultHome(context.source.sender, emptyList()) } }
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
                            executeSetDefaultHome(context.source.sender, listOfNotNull(context.getArgumentOrNull<String>("home")))
                        }
                    }
            )
            .build()
    }

    // /setdefaulthome <名称>：设置默认家（Brigadier 与 legacy 路由共用）
    fun executeSetDefaultHome(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.SET_DEFAULT_HOME)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.SET_DEFAULT_HOME)) return SendMessageUtil.permissionDeniedError(sender)

        val homeName = args.getOrNull(0)
            ?: return SendMessageUtil.syntaxHomeError(sender, "setdefaulthome")
        val playerData = PlayerDataManager.get(sender)
        if (!playerData.homes.containsKey(homeName)) return SendMessageUtil.homeNotFoundError(sender, homeName)
        if (playerData.equalsDefaultHomeName(homeName)) return SendMessageUtil.defaultHomeAlreadySetError(sender, homeName)
        playerData.defaultHomeName = homeName
        PlayerDataManager.save(sender)
        return SendMessageUtil.setDefaultHomeSuccess(sender, homeName)
    }
}
