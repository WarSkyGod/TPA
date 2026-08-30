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
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /homes：显示家列表（含交互按钮）
object HomesCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("homes")
            .requires { ConfigManager.config.isEnableCommand(CommandType.HOMES) }
            .executes { context -> SafeGuard.command(context) { executeHomes(context) } }
            .build()
    }

    private fun executeHomes(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.HOMES)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.HOMES)) return SendMessageUtil.permissionDeniedError(sender)
        SendMessageUtil.homeListMessage(sender, PlayerDataManager.get(sender).homeNames())
        return Command.SINGLE_SUCCESS
    }
}
