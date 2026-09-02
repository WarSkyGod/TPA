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
import top.craft_hello.tpa.enums.RequestType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /warp [名称]：传送到传送点；无参数显示传送点列表
object WarpCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("warp")
            .requires { ConfigManager.config.isEnableCommand(CommandType.WARP) }
            .executes { context -> SafeGuard.command(context) { executeWarp(context.source.sender, emptyList()) } }
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
                            executeWarp(context.source.sender, listOfNotNull(context.getArgumentOrNull<String>("warp")))
                        }
                    }
            )
            .build()
    }

    // /warp [名称]：传送到传送点；无参数显示传送点列表（Brigadier 与 legacy 路由共用）
    fun executeWarp(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.WARP)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.WARP)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }

        val warpName = args.getOrNull(0)
        if (warpName == null) {
            SendMessageUtil.warpListMessage(sender, ConfigManager.warpConfig.getWarpNames())
            return Command.SINGLE_SUCCESS
        }
        val location = ConfigManager.warpConfig.getWarpLocation(warpName)
            ?: return SendMessageUtil.warpNotFoundError(sender, warpName)
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)
        TeleportRequest.locationRequest(sender, RequestType.WARP, location, warpName)
        return Command.SINGLE_SUCCESS
    }
}
