package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.BrigadierUtil.getArgumentOrNull
import top.craft_hello.tpa.utils.SendMessageUtil
import top.craft_hello.tpa.utils.VersionUtil

// /tpa <玩家>：请求传送到对方位置
// 兼容 3.x 子命令：/tpa version、/tpa setlang <语言>、/tpa reload
object TpaCommand {

    fun registerCommands(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("tpa")
            .requires { ConfigManager.config.isEnableCommand(CommandType.TPA) }
            .executes(::executeTpa)
            .then(
                Commands.argument("player", StringArgumentType.word())
                    .suggests { _, builder ->
                        val input = builder.remaining.lowercase()
                        for (player in Bukkit.getOnlinePlayers()) {
                            val playerName = player.name
                            if (playerName.lowercase().contains(input)) builder.suggest(playerName)
                        }
                        builder.buildFuture()
                    }
                    .executes(::executeTpa)
            )
            .then(
                Commands.literal("version")
                    .executes { context -> executeVersion(context.source.sender) }
            )
            .then(
                Commands.literal("setlang")
                    .then(
                        Commands.argument("language", StringArgumentType.word())
                            .suggests { _, builder ->
                                for (languageName in top.craft_hello.tpa.objects.LanguageManager.getLanguageNames()) {
                                    builder.suggest(languageName)
                                }
                                builder.buildFuture()
                            }
                            .executes { context ->
                                val language = context.getArgumentOrNull<String>("language")
                                    ?: return@executes Command.SINGLE_SUCCESS
                                executeSetLang(context.source.sender, language)
                            }
                    )
            )
            .then(
                Commands.literal("reload")
                    .executes { context -> executeReload(context.source.sender) }
            )
            .build()
    }

    fun executeTpa(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        if (!ConfigManager.config.isEnableCommand(CommandType.TPA)) return SendMessageUtil.commandDisabledError(sender)
        if (!ConfigManager.config.hasPermission(sender, PermissionType.TPA)) return SendMessageUtil.permissionDeniedError(sender)
        if (TeleportRequest.isInCommandDelay(sender.uniqueId)) {
            return SendMessageUtil.commandCooldownError(sender, TeleportRequest.getCommandDelayRemaining(sender.uniqueId).toString())
        }
        if (TeleportRequest.isQueued(sender.uniqueId)) return SendMessageUtil.requestPendingError(sender)

        val playerName = context.getArgumentOrNull<String>("player")
            ?: return SendMessageUtil.syntaxTpaError(sender, "tpa")
        val target = Bukkit.getPlayerExact(playerName)
            ?: return SendMessageUtil.targetOfflineError(sender, playerName)
        TeleportRequest.tpRequest(sender, target)
        return Command.SINGLE_SUCCESS
    }

    // 检查更新
    fun executeVersion(sender: CommandSender): Int {
        if (!PermissionType.hasPermission(sender, PermissionType.VERSION)) return SendMessageUtil.permissionDeniedError(sender)
        cn.handyplus.lib.adapter.HandySchedulerUtil.runTaskAsynchronously {
            VersionUtil.updateCheck(sender)
        }
        return Command.SINGLE_SUCCESS
    }

    // 设置玩家语言（false 恢复跟随客户端）
    fun executeSetLang(sender: CommandSender, language: String): Int {
        if (sender !is Player) return SendMessageUtil.consoleRestrictedError()
        val languageManager = top.craft_hello.tpa.objects.LanguageManager
        val playerData = top.craft_hello.tpa.objects.PlayerDataManager.get(sender)
        if (language.equals("false", ignoreCase = true)) {
            playerData.setlang = false
            top.craft_hello.tpa.objects.PlayerDataManager.save(sender)
            SendMessageUtil.setLangCommandSuccess(sender, languageManager.getLanguage(sender).languageFile.nameWithoutExtension)
            return Command.SINGLE_SUCCESS
        }
        if (!languageManager.hasLanguage(language)) return SendMessageUtil.syntaxGenericError(sender, "tpac setlang <language>")
        val languageName = languageManager.formatLangStr(language)
        playerData.language = languageName
        playerData.setlang = true
        top.craft_hello.tpa.objects.PlayerDataManager.save(sender)
        SendMessageUtil.setLangCommandSuccess(sender, languageName)
        return Command.SINGLE_SUCCESS
    }

    // 重载配置
    fun executeReload(sender: CommandSender): Int {
        if (!PermissionType.hasPermission(sender, PermissionType.RELOAD)) return SendMessageUtil.permissionDeniedError(sender)
        ConfigManager.reloadAllConfig()
        return SendMessageUtil.configReloaded(sender)
    }
}
