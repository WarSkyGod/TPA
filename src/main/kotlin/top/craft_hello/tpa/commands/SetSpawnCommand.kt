package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /setspawn：在当前位置设置主城
object SetSpawnCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("setspawn")
            .executes { context -> SafeGuard.command(context) { executeSetSpawn(context.source.sender, emptyList()) } }
            .build()
    }

    // /setspawn：在当前位置设置主城（Brigadier 与 legacy 路由共用）
    fun executeSetSpawn(sender: CommandSender, args: List<String>): Int {
        val config = ConfigManager.config
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!config.isEnableCommand(CommandType.SET_SPAWN)) return SendMessageUtil.commandDisabledError(sender)
        if (!config.hasPermission(sender, PermissionType.SET_SPAWN)) return SendMessageUtil.permissionDeniedError(sender)
        ConfigManager.spawnConfig.setLocation(sender.location)
        return SendMessageUtil.setSpawnSuccess(sender)
    }
}
