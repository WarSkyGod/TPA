package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /tpa <玩家>：请求传送到对方位置（管理子命令 version/setlang/reload 见 /tpac）
object TpaCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpa")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TPA) }
            .executes { context ->
                SafeGuard.command(context) { executeTpa(context.source.sender, emptyList()) }
            }
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
                    .executes { context ->
                        SafeGuard.command(context) {
                            executeTpa(context.source.sender, listOfNotNull(context.getArgumentOrNull<String>("player")))
                        }
                    }
            )
            .build()
    }

    // /tpa <玩家>：请求传送到对方位置（Brigadier 与 legacy 路由共用）
    fun executeTpa(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TPA)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.TPA)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)

        val playerName = args.getOrNull(0)
            ?: return SendMessageUtil.syntaxTpaError(sender, "tpa")
        val target = Bukkit.getPlayerExact(playerName)
            ?: return SendMessageUtil.targetOfflineError(sender, playerName)
        TeleportRequest.tpRequest(sender, target)
        return Command.SINGLE_SUCCESS
    }
}
