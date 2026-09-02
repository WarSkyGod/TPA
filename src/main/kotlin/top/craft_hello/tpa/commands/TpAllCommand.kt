package top.craft_hello.tpa.commands

import cn.handyplus.lib.adapter.EntitySchedulerUtil
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// /tpall [player/warp/spawn] [名称]：将在线玩家传送到指定位置（管理员）
// 消息归属对齐 3.x：被传送者收"管理员已将您传送至 <目的地>"；player 目标额外收"管理员将所有玩家传送到您的位置"
object TpAllCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpall")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TP_ALL) }
            .executes { context -> SafeGuard.command(context) { executeTpAll(context.source.sender, emptyList()) } }
            .then(
                Commands.literal("player")
                    .then(
                        Commands.argument("player", StringArgumentType.word())
                            .suggests { _, builder ->
                                val input = builder.remaining.lowercase()
                                for (player in Bukkit.getOnlinePlayers()) {
                                    if (player.name.lowercase().contains(input)) builder.suggest(player.name)
                                }
                                builder.buildFuture()
                            }
                            .executes { context ->
                                SafeGuard.command(context) {
                                    executeTpAll(context.source.sender, listOfNotNull("player", context.getArgumentOrNull<String>("player")))
                                }
                            }
                    )
            )
            .then(
                Commands.literal("warp")
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
                                    executeTpAll(context.source.sender, listOfNotNull("warp", context.getArgumentOrNull<String>("warp")))
                                }
                            }
                    )
            )
            .then(
                Commands.literal("spawn")
                    .executes { context -> SafeGuard.command(context) { executeTpAll(context.source.sender, listOf("spawn")) } }
            )
            .build()
    }

    // /tpall [player/warp/spawn] [名称]：将在线玩家传送到指定位置（Brigadier 与 legacy 路由共用）
    // args 形如 [player, 名称] / [warp, 名称] / [spawn] / []
    fun executeTpAll(sender: CommandSender, args: List<String>): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TP_ALL)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.TP_ALL)) return SendMessageUtil.permissionDeniedError(sender)

        val onlinePlayers = Bukkit.getOnlinePlayers().toList()
        if (onlinePlayers.isEmpty()) return SendMessageUtil.noOnlinePlayersError(sender)

        return when {
            "player".equals(args.getOrNull(0), ignoreCase = true) -> {
                val playerName = args.getOrNull(1)
                    ?: return SendMessageUtil.syntaxTpAllError(sender, "tpall")
                val target = Bukkit.getPlayerExact(playerName)
                    ?: return SendMessageUtil.targetOfflineError(sender, playerName)
                tpAllToPlayer(sender, onlinePlayers, target)
            }
            "warp".equals(args.getOrNull(0), ignoreCase = true) -> {
                val warpName = args.getOrNull(1)
                    ?: return SendMessageUtil.syntaxTpAllError(sender, "tpall")
                if (ConfigManager.warpConfig.getWarpNames().isEmpty()) return SendMessageUtil.noWarpsSetError(sender)
                val location = ConfigManager.warpConfig.getWarpLocation(warpName)
                    ?: return SendMessageUtil.warpNotFoundError(sender, warpName)
                tpAllToLocation(sender, onlinePlayers, location, warpName)
            }
            "spawn".equals(args.getOrNull(0), ignoreCase = true) -> {
                val location = ConfigManager.spawnConfig.getLocation()
                    ?: return SendMessageUtil.spawnNotSetError(sender)
                tpAllToLocation(sender, onlinePlayers, location, "spawn_name")
            }
            else -> tpAllToSelf(sender, onlinePlayers)
        }
    }

    // /tpall player <目标>：除目标外的所有人（含执行者）传送到目标位置；
    // 被传送者收"传送至 <目标名>"，目标收"管理员将所有玩家传送到您的位置"
    private fun tpAllToPlayer(sender: Player, onlinePlayers: List<Player>, target: Player): Int {
        val movable = onlinePlayers.filter { it != target }
        if (movable.isEmpty()) return SendMessageUtil.noOnlinePlayersError(sender)
        val targetName = target.name
        for (player in movable) {
            EntitySchedulerUtil.syncTeleport(player, target.location)
            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownOverMessage(player, targetName)
            SendMessageUtil.playTeleportSuccessSound(player)
            SendMessageUtil.adminTpYouToMessage(player, targetName)
        }
        SendMessageUtil.tpAllCommandSuccess(sender, targetName)
        SendMessageUtil.adminTpAllPlayerToYouMessage(target)
        return Command.SINGLE_SUCCESS
    }

    // /tpall warp|spawn：所有人（含执行者）传送到指定位置，被传送者收"传送至 <目的地>"
    private fun tpAllToLocation(sender: Player, onlinePlayers: List<Player>, location: Location, targetName: String): Int {
        val targetNameShown = if (targetName == "spawn_name") LanguageManager.getLanguage(sender).getMessage("spawn_name") else targetName
        for (player in onlinePlayers) {
            EntitySchedulerUtil.syncTeleport(player, location)
            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownOverMessage(player, targetName)
            SendMessageUtil.playTeleportSuccessSound(player)
            SendMessageUtil.adminTpYouToMessage(player, targetNameShown)
        }
        return SendMessageUtil.tpAllCommandSuccess(sender, targetNameShown)
    }

    // /tpall（无参）：除执行者外的所有人传送到执行者位置
    private fun tpAllToSelf(sender: Player, onlinePlayers: List<Player>): Int {
        val movable = onlinePlayers.filter { it != sender }
        if (movable.isEmpty()) return SendMessageUtil.noOnlinePlayersError(sender)
        for (player in movable) {
            EntitySchedulerUtil.syncTeleport(player, sender.location)
            if (ConfigManager.config.enableTitleMessage) SendMessageUtil.titleCountdownOverMessage(player, sender.name)
            SendMessageUtil.playTeleportSuccessSound(player)
            SendMessageUtil.adminTpYouToMessage(player, sender.name)
        }
        return SendMessageUtil.tpAllCommandSuccess(sender, sender.name)
    }
}
