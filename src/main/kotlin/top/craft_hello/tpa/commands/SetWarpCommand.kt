package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /setwarp <名称>：在当前位置设置传送点
object SetWarpCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("setwarp")
            .requires { ConfigManager.config.isEnableCommand(CommandType.SET_WARP) }
            .executes { context -> SafeGuard.command(context) { executeSetWarp(context) } }
            .then(
                Commands.argument("warp", StringArgumentType.word())
                    .executes { context -> SafeGuard.command(context) { executeSetWarp(context) } }
            )
            .build()
    }

    private fun executeSetWarp(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.SET_WARP)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.SET_WARP)) return SendMessageUtil.permissionDeniedError(sender)

        val warpName = context.getArgumentOrNull<String>("warp")
            ?: return SendMessageUtil.syntaxWarpError(sender, "setwarp")
        ConfigManager.warpConfig.setWarpLocation(warpName, sender.location)
        return SendMessageUtil.setWarpSuccess(sender, warpName)
    }
}
