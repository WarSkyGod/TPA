package top.craft_hello.tpa.commands

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil

object SetSpawnCommand {
    fun setSpawnCommand(registrar: Commands){
        registrar.register(
            Commands.literal("setspawn")
                .executes(::executeSetSpawn)
                .build()
        )
    }

    fun executeSetSpawn(context: CommandContext<CommandSourceStack>): Int {
        var sender = context.source.sender
        var config = ConfigManager.config
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!config.isEnableCommand(CommandType.SPAWN)) return SendMessageUtil.commandDisabledError(sender)
        if (!config.hasPermission(sender, PermissionType.SET_SPAWN) ) return SendMessageUtil.permissionDeniedError(sender)
        ConfigManager.spawnConfig.setLocation(sender.location)
        return SendMessageUtil.setSpawnSuccess(sender)
    }
}