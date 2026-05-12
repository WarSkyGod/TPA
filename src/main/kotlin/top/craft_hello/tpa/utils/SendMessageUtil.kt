package top.craft_hello.tpa.utils

import com.mojang.brigadier.Command
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.objects.LanguageManager
import java.util.Objects.isNull

class SendMessageUtil {
    companion object {
        // 发送消息
        fun sendMessage(sender: CommandSender, message : Component) {
            if (!isNull(sender)) sender.sendMessage(message)
        }

        // 根据 path 读取配置文件中的消息并发送
        fun sendMessageForPath(sender: CommandSender, path : String, vararg vars: String) {
            val language = LanguageManager.getLanguage(sender)
            sendMessage(sender, language.getFormatPrefixMessage(path, *vars))
        }

        // 控制台不能使用此命令错误
        fun consoleRestrictedError() : Int {
            sendMessageForPath(Bukkit.getConsoleSender(), "error.console_restricted")
            return Command.SINGLE_SUCCESS
        }

        // 服务器未启用此命令错误
        fun commandDisabledError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.command_disabled")
            return Command.SINGLE_SUCCESS
        }

        // 没有权限错误
        fun permissionDeniedError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.permission_denied")
            return Command.SINGLE_SUCCESS
        }

        // 命令冷却中错误
        fun commandCooldownError(player: Player, delay: String) : Int {
            sendMessageForPath(player, "error.command_cooldown", delay)
            return Command.SINGLE_SUCCESS
        }

        // 请求待处理错误
        fun requestPendingError(player: Player) : Int {
            sendMessageForPath(player, "error.request_pending")
            return Command.SINGLE_SUCCESS
        }

        // 未设置主城错误
        fun spawnNotSetError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.spawn_not_set")
            return Command.SINGLE_SUCCESS
        }

        // 返回主城成功消息
        fun backSpawnSuccessMessage(player: Player) {
            val language = LanguageManager.getLanguage(player)
            sendMessageForPath(player, "spawn.teleport_success", language.getMessage("spawn_name"))
        }

        // 成功设置主城消息
        fun setSpawnSuccess(player: Player) : Int {
            sendMessageForPath(player, "spawn.set_success")
            return Command.SINGLE_SUCCESS
        }

        // 成功删除主城消息
        fun delSpawnSuccess(sender: CommandSender) : Int {
            sendMessageForPath(sender, "spawn.delete_success")
            return Command.SINGLE_SUCCESS
        }

        // 配置文件迁移消息
        fun configVersionUpdate(sender : CommandSender) {
            sendMessageForPath(sender, "system.config_migrated")
        }

        // 配置文件迁移成功消息
        fun configVersionUpdateSuccess(sender : CommandSender) {
            sendMessageForPath(sender, "system.config_migrated_success")
        }

        // 正在检查更新消息
        fun checkUpdate(sender : CommandSender) {
            sendMessageForPath(sender, "update.checking")
        }

        // 发现更新消息
        fun pluginUpdateMessage(sender : CommandSender,latestVersion : String) {
            sendMessageForPath(sender, "update.available", latestVersion)
        }

        // 当前已是最新版本消息
        fun pluginLatestVersion(sender : CommandSender) {
            sendMessageForPath(sender, "update.latest")
        }

        // 插件加载消息
        fun pluginLoaded(sender : CommandSender, pluginVersion : String) {
            sendMessageForPath(sender, "system.plugin_loaded", pluginVersion)
        }

        // 插件卸载消息
        fun pluginUnLoaded(sender : CommandSender) {
            sendMessageForPath(sender, "system.plugin_unloaded")
        }

        // 配置文件重载消息
        fun configReloaded(sender : CommandSender) {
            // 向服务器后台发送重载消息
            sendMessageForPath(Bukkit.getConsoleSender(), "system.config_reloaded")
            // 如果是玩家执行，则也向该玩家发送重载消息
            if (sender is Player) sendMessageForPath(sender, "system.config_reloaded")
        }

        fun acceptOrDeny(target : Player, executorName : String) {
            val language = LanguageManager.getLanguage(target)
            sendMessage(target, language.getFormatPrefixMessage("accept_button")
                .append(language.getFormatMessage("deny_button"))
                .append(language.getFormatMessage("blacklist.add_button", executorName))
            )
        }

        // 成功发送请求消息
        fun successSentRequest(executor : Player, target : String, delay : String) {
            sendMessageForPath(executor, "request.sent_success", target)
            sendMessageForPath(executor, "request.timeout_notice", delay)
        }

        // 请求传送到对方的位置消息
        fun requestTeleportToTarget(executor : Player, target : Player, delay : String) {
            val executorName = executor.name
            val targetName = target.name
            sendMessageForPath(target, "request.to_here", executorName, delay)
            acceptOrDeny(target, executorName)
            successSentRequest(executor, targetName, delay)
        }

        // title 样式的传送倒计时消息
        fun titleCountdownMessage(executor: Player, vararg vars: String) {
            val target: String = vars[vars.size - 2]
            val language = LanguageManager.getLanguage(executor)
            val vars2 = vars.copyOfRange(0, vars.size - 2)
            if ("last_location" == target || "rtp_name" == target || "spawn_name" == target)
                 language.getMessage(target)
            // val title: String = language.getFormatMessage("teleport.countdown", vars)
            // val subTitle: String = language.getFormatMessage("teleport.cancel_on_move")
            // executor.sendTitle(title, subTitle)
        }


        // 传送倒计时消息
        fun teleportCountdown(executor: Player, target: String, delay: String) {
            var target = target
            val language = LanguageManager.getLanguage(executor)
            if ("last_location" == target || "rtp_name" == target || "spawn_name" == target) target = language.getMessage(target)
            sendMessageForPath(executor, "teleport.countdown", target, delay)
            sendMessageForPath(executor, "teleport.cancel_on_move")
        }
    }
}