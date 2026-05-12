package top.craft_hello.tpa.commands

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil

object DelSpawnCommand {
    fun delSpawnCommand(registrar: Commands){
        registrar.register(
            Commands.literal("delspawn")
                .executes(::executeDelSpawn)
                .build()
        )
    }

    fun executeDelSpawn(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        val config = ConfigManager.config
        if (!config.isEnableCommand(CommandType.SPAWN)) return SendMessageUtil.commandDisabledError(sender)
        if (!config.hasPermission(sender, PermissionType.DEL_SPAWN) ) return SendMessageUtil.permissionDeniedError(sender)
        if (!ConfigManager.spawnConfig.delLocation()) return SendMessageUtil.spawnNotSetError(sender)
        return SendMessageUtil.delSpawnSuccess(sender)
    }
}