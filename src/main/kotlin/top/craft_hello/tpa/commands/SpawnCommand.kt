package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.Target
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil
import java.util.Objects.isNull

object SpawnCommand {
    fun spawnCommand(registrar: Commands){
        registrar.register(
            Commands.literal("spawn")
                .executes(::executeSpawn)
                .build()
        )
    }

    fun executeSpawn(context: CommandContext<CommandSourceStack>): Int {
        var sender = context.source.sender
        var config = ConfigManager.config
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!config.isEnableCommand(CommandType.SPAWN)) return SendMessageUtil.commandDisabledError(sender)
        if (!config.hasPermission(sender, PermissionType.SPAWN) ) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.commandDelayQueue.containsKey(sender)) return SendMessageUtil.commandCooldownError(sender, TeleportRequest.commandDelayQueue[sender] ?: "null")
        if (TeleportRequest.requestQueue.containsKey(sender)) return SendMessageUtil.requestPendingError(sender)
        var location = ConfigManager.spawnConfig.getLocation()
        if (location == null) return SendMessageUtil.spawnNotSetError(sender)
        TeleportRequest(sender, RequestType.SPAWN, Target(location), "spawn_name")
        return Command.SINGLE_SUCCESS
    }
}