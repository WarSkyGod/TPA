package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /denys [add/remove] <玩家>：黑名单管理；/denys：查看黑名单
object DenysCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("denys")
            .requires { ConfigManager.config.isEnableCommand(CommandType.DENYS) }
            .executes { context -> SafeGuard.command(context) { executeDenys(context) } }
            .then(
                Commands.literal("add")
                    .then(
                        Commands.argument("player", StringArgumentType.word())
                            .suggests { context, builder ->
                                val sender = context.source.sender
                                val input = builder.remaining.lowercase()
                                if (sender is Player) {
                                    // 不能拉黑自己：补全列表排除自己
                                    for (player in Bukkit.getOnlinePlayers()) {
                                        if (player != sender && player.name.lowercase().contains(input)) builder.suggest(player.name)
                                    }
                                }
                                builder.buildFuture()
                            }
                            .executes { context -> SafeGuard.command(context) { executeDenys(context) } }
                    )
            )
            .then(
                Commands.literal("remove")
                    .then(
                        Commands.argument("player", StringArgumentType.word())
                            .suggests { context, builder ->
                                val sender = context.source.sender
                                val input = builder.remaining.lowercase()
                                if (sender is Player) {
                                    for (denyUuid in PlayerDataManager.get(sender).denyList) {
                                        val name = SendMessageUtil.denyDisplayName(denyUuid)
                                        if (name.lowercase().contains(input)) builder.suggest(name)
                                    }
                                }
                                builder.buildFuture()
                            }
                            .executes { context -> SafeGuard.command(context) { executeDenys(context) } }
                    )
            )
            .build()
    }

    private fun executeDenys(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.DENYS)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.DENYS)) return SendMessageUtil.permissionDeniedError(sender)

        val playerData = PlayerDataManager.get(sender)
        val action = context.getArgumentOrNull<String>("player")
        // 判断当前命中的子命令：检查解析树节点名
        val nodeNames = context.nodes.map { it.node.name }
        return when {
            "add" in nodeNames -> {
                val playerName = action ?: return SendMessageUtil.syntaxGenericError(sender, "denys add <player>")
                val target = Bukkit.getPlayerExact(playerName)
                    ?: return SendMessageUtil.targetOfflineError(sender, playerName)
                if (target == sender) return SendMessageUtil.selfOperationError(sender)
                if (playerData.isDeny(target.uniqueId.toString())) return SendMessageUtil.alreadyBlacklistedError(sender)
                // 对齐 3.x：[拒绝并加入黑名单]——加黑名单前先拒绝自己收到的待处理请求
                if (TeleportRequest.isQueued(sender.uniqueId)) TeleportRequest.tpdeny(sender)
                playerData.addDeny(target.uniqueId.toString())
                PlayerDataManager.save(sender)
                SendMessageUtil.addDenysSuccess(sender, target.name)
            }
            "remove" in nodeNames -> {
                val playerName = action ?: return SendMessageUtil.syntaxGenericError(sender, "denys remove <player>")
                val denyUuid = playerData.denyList.firstOrNull {
                    SendMessageUtil.denyDisplayName(it).equals(playerName, ignoreCase = true)
                } ?: return SendMessageUtil.notBlacklistedError(sender)
                playerData.removeDeny(denyUuid)
                PlayerDataManager.save(sender)
                SendMessageUtil.removeDenySuccess(sender, SendMessageUtil.denyDisplayName(denyUuid))
            }
            else -> {
                SendMessageUtil.denysMessage(sender, playerData.denyList)
                Command.SINGLE_SUCCESS
            }
        }
    }
}
