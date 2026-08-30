package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
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
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /back：返回上一次的位置；没有记录时尝试返回主城
object BackCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("back")
            .requires { ConfigManager.config.isEnableCommand(CommandType.BACK) }
            .executes { context -> SafeGuard.command(context) { executeBack(context) } }
            .build()
    }

    private fun executeBack(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.BACK)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.BACK)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)

        val lastLocation = PlayerDataManager.get(sender).lastLocation
        if (lastLocation != null) {
            TeleportRequest.locationRequest(sender, RequestType.BACK, lastLocation, "last_location")
            return Command.SINGLE_SUCCESS
        }
        val spawnLocation = ConfigManager.spawnConfig.getLocation()
        if (spawnLocation != null) {
            TeleportRequest.locationRequest(sender, RequestType.BACK, spawnLocation, "spawn_name")
            return Command.SINGLE_SUCCESS
        }
        return SendMessageUtil.lastLocationMissingError(sender)
    }
}
