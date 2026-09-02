package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /delwarp <名称>：删除传送点
object DelWarpCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("delwarp")
            .requires { ConfigManager.config.isEnableCommand(CommandType.DEL_WARP) }
            .executes { context -> SafeGuard.command(context) { executeDelWarp(context.source.sender, emptyList()) } }
            .then(
                Commands.argument("warp", StringArgumentType.word())
                    .suggests { _, builder ->
                        val input = builder.remaining.lowercase()
                        for (warpName in ConfigManager.warpConfig.getWarpNames()) {
                            if (warpName.lowercase().contains(input)) builder.suggest(warpName)
                        }
                        builder.buildFuture()
                    }
                    .executes { context ->
                        SafeGuard.command(context) {
                            executeDelWarp(context.source.sender, listOfNotNull(context.getArgumentOrNull<String>("warp")))
                        }
                    }
            )
            .build()
    }

    // /delwarp <名称>：删除传送点（Brigadier 与 legacy 路由共用）
    fun executeDelWarp(sender: CommandSender, args: List<String>): Int {
        if (!ConfigManager.config.isEnableCommand(CommandType.DEL_WARP)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.DEL_WARP)) return SendMessageUtil.permissionDeniedError(sender)

        val warpName = args.getOrNull(0)
            ?: return SendMessageUtil.syntaxWarpError(sender, "delwarp")
        if (!ConfigManager.warpConfig.delWarpLocation(warpName)) return SendMessageUtil.warpNotFoundError(sender, warpName)
        return SendMessageUtil.delWarpSuccess(sender, warpName)
    }
}
