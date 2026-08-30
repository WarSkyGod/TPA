package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /delspawn：删除主城（可由控制台执行）
object DelSpawnCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("delspawn")
            .executes { context -> SafeGuard.command(context) { executeDelSpawn(context) } }
            .build()
    }

    fun executeDelSpawn(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        val config = ConfigManager.config
        if (!config.isEnableCommand(CommandType.DEL_SPAWN)) return SendMessageUtil.commandDisabledError(sender)
        if (!config.hasPermission(sender, PermissionType.DEL_SPAWN)) return SendMessageUtil.permissionDeniedError(sender)
        if (!ConfigManager.spawnConfig.delLocation()) return SendMessageUtil.spawnNotSetError(sender)
        return SendMessageUtil.delSpawnSuccess(sender)
    }
}
