package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /spawn：传送到主城
object SpawnCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("spawn")
            .requires { ConfigManager.config.isEnableCommand(CommandType.SPAWN) }
            .executes { context -> SafeGuard.command(context) { executeSpawn(context.source.sender, emptyList()) } }
            .build()
    }

    // /spawn：传送到主城（Brigadier 与 legacy 路由共用）
    fun executeSpawn(sender: CommandSender, args: List<String>): Int {
        val config = ConfigManager.config
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!config.isEnableCommand(CommandType.SPAWN)) return SendMessageUtil.commandDisabledError(sender)
        if (!config.hasPermission(sender, PermissionType.SPAWN)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)
        val location = ConfigManager.spawnConfig.getLocation() ?: return SendMessageUtil.spawnNotSetError(sender)
        TeleportRequest.locationRequest(sender, RequestType.SPAWN, location, "spawn_name")
        return Command.SINGLE_SUCCESS
    }
}
