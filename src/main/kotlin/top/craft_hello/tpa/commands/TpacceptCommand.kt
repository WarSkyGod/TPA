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

// /tpaccept：接受传送请求
object TpacceptCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpaccept")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TP_ACCEPT) }
            .executes { context -> SafeGuard.command(context) { executeTpaccept(context.source.sender, emptyList()) } }
            .build()
    }

    // /tpaccept：接受传送请求（Brigadier 与 legacy 路由共用）
    fun executeTpaccept(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TP_ACCEPT)) return SendMessageUtil.commandDisabledError(sender)
        TeleportRequest.tpaccept(sender)
        return Command.SINGLE_SUCCESS
    }
}
