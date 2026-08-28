package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil

// /setspawn：在当前位置设置主城
object SetSpawnCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("setspawn")
            .executes { context -> executeSetSpawn(context) }
            .build()
    }

    fun executeSetSpawn(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        val config = ConfigManager.config
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!config.isEnableCommand(CommandType.SET_SPAWN)) return SendMessageUtil.commandDisabledError(sender)
        if (!config.hasPermission(sender, PermissionType.SET_SPAWN)) return SendMessageUtil.permissionDeniedError(sender)
        ConfigManager.spawnConfig.setLocation(sender.location)
        return SendMessageUtil.setSpawnSuccess(sender)
    }
}
