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
import top.craft_hello.tpa.datas.SpawnConfig
import top.craft_hello.tpa.datas.WarpConfig
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SendMessageUtil

// /tpall [player/warp/spawn] [名称]：将所有在线玩家传送到指定位置（管理员）
object TpAllCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpall")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TP_ALL) }
            .executes { context -> executeTpAll(context) }
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
                            .executes { context -> executeTpAll(context) }
                    )
            )
            .then(
                Commands.literal("warp")
                    .then(
                        Commands.argument("warp", StringArgumentType.word())
                            .suggests { _, builder ->
                                val input = builder.remaining.lowercase()
                                for (warpName in WarpConfigHolder.warpNames()) {
                                    if (warpName.lowercase().contains(input)) builder.suggest(warpName)
                                }
                                builder.buildFuture()
                            }
                            .executes { context -> executeTpAll(context) }
                    )
            )
            .then(
                Commands.literal("spawn")
                    .executes { context -> executeTpAll(context) }
            )
            .build()
    }

    private object WarpConfigHolder {
        fun warpNames(): List<String> = ConfigManager.warpConfig.getWarpNames()
    }

    private fun executeTpAll(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TP_ALL)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.TP_ALL)) return SendMessageUtil.permissionDeniedError(sender)

        val nodes = context.nodes.joinToString(" ") { it.node.name }
        val onlinePlayers = Bukkit.getOnlinePlayers().toList()
        if (onlinePlayers.isEmpty()) return SendMessageUtil.noOnlinePlayersError(sender)

        return when {
            nodes.endsWith("player") -> {
                val playerName = context.getArgumentOrNull<String>("player")
                    ?: return SendMessageUtil.syntaxTpAllError(sender, "tpall")
                val target = Bukkit.getPlayerExact(playerName)
                    ?: return SendMessageUtil.targetOfflineError(sender, playerName)
                teleportAll(sender, onlinePlayers, target.location, target.name)
            }
            nodes.endsWith("warp") -> {
                val warpName = context.getArgumentOrNull<String>("warp")
                    ?: return SendMessageUtil.syntaxTpAllError(sender, "tpall")
                val location = ConfigManager.warpConfig.getWarpLocation(warpName)
                    ?: return SendMessageUtil.warpNotFoundError(sender, warpName)
                teleportAll(sender, onlinePlayers, location, warpName)
            }
            nodes.endsWith("spawn") -> {
                val location = ConfigManager.spawnConfig.getLocation()
                    ?: return SendMessageUtil.spawnNotSetError(sender)
                teleportAll(sender, onlinePlayers, location, "spawn_name")
            }
            else -> {
                // 无参：所有玩家传送到管理员位置
                teleportAll(sender, onlinePlayers, sender.location, sender.name, toSelf = true)
            }
        }
    }

    private fun teleportAll(
        sender: Player,
        players: List<Player>,
        location: Location,
        targetName: String,
        toSelf: Boolean = false
    ): Int {
        for (player in players) {
            if (toSelf && player == sender) continue
            EntitySchedulerUtil.syncTeleport(player, location)
            SendMessageUtil.adminTpYouToMessage(player, sender.name)
        }
        return if (toSelf) {
            SendMessageUtil.adminTpAllPlayerToYouMessage(sender)
            Command.SINGLE_SUCCESS
        } else {
            SendMessageUtil.tpAllCommandSuccess(sender, targetName)
        }
    }
}
