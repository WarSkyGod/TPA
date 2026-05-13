package top.craft_hello.tpa.commands

import cn.handyplus.lib.adapter.EntitySchedulerUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil

object TpaCommand {
    fun tpaCommand(registrar: Commands){
        registrar.register(
            Commands.literal("tpa")
                .requires {
                    ConfigManager.config.isEnableCommand(CommandType.TPA)
                }
                .executes(::executeTpa)
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

    inline fun <reified T> CommandContext<*>.getArgumentOrNull(name: String): T? {
        return try {
            getArgument(name, T::class.java)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun executeTpa(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (!ConfigManager.config.hasPermission(sender, PermissionType.TPA)) return SendMessageUtil.permissionDeniedError(sender)
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()

        val playerName = context.getArgumentOrNull<String>("player")
            ?.lowercase()
            ?.substringBefore(' ')
            ?: return SendMessageUtil.syntaxTpaError(
                sender,
                context.nodes.firstOrNull()?.node?.name ?: "tpa"
        )

        Bukkit.getPlayerExact(playerName)?.let { SendMessageUtil.requestTeleportToTarget(sender, it, "30") }
        val location = Bukkit.getPlayerExact(playerName)?.location
        if (location != null) EntitySchedulerUtil.syncTeleport(sender, location)
        return Command.SINGLE_SUCCESS
    }
}