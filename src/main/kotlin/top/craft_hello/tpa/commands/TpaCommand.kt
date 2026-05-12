package top.craft_hello.tpa.commands

import cn.handyplus.lib.adapter.EntitySchedulerUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.craft_hello.tpa.utils.SendMessageUtil

object TpaCommand {
    fun tpaCommand(registrar: Commands){
        registrar.register(
            Commands.literal("tpa")
                .then(
                    Commands.argument("player", StringArgumentType.greedyString())
                        .suggests { context, builder ->
                            val sender = context.source.sender
                            // 获取当前输入的内容（不包含命令部分）
                            val input = builder.remaining.lowercase()
                            if (sender is Player) {
                                for (player in Bukkit.getOnlinePlayers()) {
                                    val playerName = player.name
                                    if (playerName.lowercase().contains(input) and (playerName != sender.name))
                                        builder.suggest(playerName)
                                }
                            }
                            builder.buildFuture()
                        }
                        .executes(::executeTpa)
                )
                .build()
        )
    }

    fun executeTpa(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        val text = context.getArgument("player", String::class.java).lowercase()
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        sender.sendMessage("> TPA传送命令！${text}")
        Bukkit.getPlayerExact(text)?.let { SendMessageUtil.requestTeleportToTarget(sender, it, "30") }
        val location = Bukkit.getPlayerExact(text)?.location
        if (location != null) EntitySchedulerUtil.syncTeleport(sender, location)
        return Command.SINGLE_SUCCESS
    }
}