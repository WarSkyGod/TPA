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
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /tplogout <玩家>：传送到目标玩家最后下线的位置
object TpLogoutCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tplogout")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TP_LOGOUT) }
            .executes { context -> SafeGuard.command(context) { executeTpLogout(context) } }
            .then(
                Commands.argument("player", StringArgumentType.word())
                    .suggests { _, builder ->
                        val input = builder.remaining.lowercase()
                        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
                            val name = offlinePlayer.name ?: continue
                            if (name.lowercase().contains(input)) builder.suggest(name)
                        }
                        builder.buildFuture()
                    }
                    .executes { context -> SafeGuard.command(context) { executeTpLogout(context) } }
            )
            .build()
    }

    private fun executeTpLogout(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TP_LOGOUT)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.TP_LOGOUT)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)

        val playerName = context.getArgumentOrNull<String>("player")
            ?: return SendMessageUtil.syntaxGenericError(sender, "tplogout <player>")
        val targetData = PlayerDataManager.getByName(playerName)
            ?: return SendMessageUtil.targetOfflineError(sender, playerName)
        val logoutLocation = targetData.logoutLocation
            ?: return SendMessageUtil.logoutLocationMissingError(sender, playerName)
        TeleportRequest.locationRequest(sender, RequestType.TP_LOGOUT, logoutLocation, playerName)
        return Command.SINGLE_SUCCESS
    }
}
