package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
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
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /rtp：随机传送
object RtpCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("rtp")
            .requires { ConfigManager.config.isEnableCommand(CommandType.RTP) }
            .executes { context -> SafeGuard.command(context) { executeRtp(context.source.sender, emptyList()) } }
            .build()
    }

    // /rtp：随机传送（Brigadier 与 legacy 路由共用）
    fun executeRtp(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.RTP)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.RTP)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }
        TeleportRequest.rtpRequest(sender)
        return Command.SINGLE_SUCCESS
    }
}
