package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SendMessageUtil

// /home [名称]：传送到家；无参数传送默认家
object HomeCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("home")
            .requires { ConfigManager.config.isEnableCommand(CommandType.HOME) }
            .executes { context -> executeHome(context) }
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
                    .executes { context -> executeHome(context) }
            )
            .build()
    }

    private fun executeHome(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.HOME)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.HOME)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }

        val playerData = PlayerDataManager.get(sender)
        val homeName = context.getArgumentOrNull<String>("home") ?: playerData.defaultHomeName
        if (homeName == null) {
            return SendMessageUtil.noDefaultHomeError(sender)
        }
        val location = playerData.getHome(homeName)
            ?: return SendMessageUtil.homeNotFoundError(sender, homeName)
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)
        TeleportRequest.locationRequest(sender, RequestType.HOME, location, homeName)
        return Command.SINGLE_SUCCESS
    }
}
