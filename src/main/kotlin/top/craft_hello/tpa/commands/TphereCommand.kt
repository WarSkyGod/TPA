package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SendMessageUtil

// /tphere <玩家>：请求对方传送到自己位置
object TphereCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tphere")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TP_HERE) }
            .executes { context -> executeTphere(context) }
            .then(
                Commands.argument("player", StringArgumentType.word())
                    .suggests { context, builder ->
                        val sender = context.source.sender
                        val input = builder.remaining.lowercase()
                        if (sender is Player) {
                            // 不能对自己发起传送请求：补全列表排除自己
                            for (player in Bukkit.getOnlinePlayers()) {
                                val playerName = player.name
                                if (player != sender && playerName.lowercase().contains(input)) builder.suggest(playerName)
                            }
                        }
                        builder.buildFuture()
                    }
                    .executes { context -> executeTphere(context) }
            )
            .build()
    }

    private fun executeTphere(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TP_HERE)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.TP_HERE)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)

        val playerName = context.getArgumentOrNull<String>("player")
            ?: return SendMessageUtil.syntaxTpaError(sender, "tphere")
        val target = Bukkit.getPlayerExact(playerName)
            ?: return SendMessageUtil.targetOfflineError(sender, playerName)
        TeleportRequest.tpHereRequest(sender, target)
        return Command.SINGLE_SUCCESS
    }
}
