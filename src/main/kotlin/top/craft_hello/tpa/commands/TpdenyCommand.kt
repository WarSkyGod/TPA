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
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /tpdeny：拒绝传送请求
object TpdenyCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpdeny")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TP_DENY) }
            .executes { context -> SafeGuard.command(context) { executeTpdeny(context.source.sender, emptyList()) } }
            .build()
    }

    // /tpdeny：拒绝传送请求（Brigadier 与 legacy 路由共用）
    fun executeTpdeny(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TP_DENY)) return SendMessageUtil.commandDisabledError(sender)
        TeleportRequest.tpdeny(sender)
        return Command.SINGLE_SUCCESS
    }
}
