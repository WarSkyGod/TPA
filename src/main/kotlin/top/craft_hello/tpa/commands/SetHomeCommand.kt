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
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SendMessageUtil

// /sethome <名称>：在当前位置设置家（受玩家等级家数量上限约束）
object SetHomeCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("sethome")
            .requires { ConfigManager.config.isEnableCommand(CommandType.SET_HOME) }
            .executes { context -> executeSetHome(context) }
            .then(
                Commands.argument("home", StringArgumentType.word())
                    .executes { context -> executeSetHome(context) }
            )
            .build()
    }

    private fun executeSetHome(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.SET_HOME)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.SET_HOME)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }

        val playerData = PlayerDataManager.get(sender)
        val homeName = context.getArgumentOrNull<String>("home")

        // 无参：对齐 3.x setHomeLocation(location)——有默认家则替换其位置，否则设置名为 default 的默认家
        if (homeName == null) {
            val defaultHomeName = playerData.defaultHomeName ?: "default"
            if (!playerData.homes.containsKey(defaultHomeName)) {
                if (ConfigManager.config.isHomeAmountExceeded(sender, playerData.homes.size)) {
                    return SendMessageUtil.homeMaxLimitError(sender, ConfigManager.config.homeAmountMax(sender).toString())
                }
                playerData.defaultHomeName = defaultHomeName
            }
            playerData.setHome(defaultHomeName, sender.location)
            PlayerDataManager.save(sender)
            return SendMessageUtil.setHomeSuccess(sender, defaultHomeName)
        }

        // 带名：对齐 3.x setHomeLocation(name, location)——新家受数量上限约束；无默认家时该名字成为默认家
        if (!playerData.homes.containsKey(homeName) && ConfigManager.config.isHomeAmountExceeded(sender, playerData.homes.size)) {
            return SendMessageUtil.homeMaxLimitError(sender, ConfigManager.config.homeAmountMax(sender).toString())
        }
        if (playerData.defaultHomeName == null) playerData.defaultHomeName = homeName
        playerData.setHome(homeName, sender.location)
        PlayerDataManager.save(sender)
        return SendMessageUtil.setHomeSuccess(sender, homeName)
    }
}
