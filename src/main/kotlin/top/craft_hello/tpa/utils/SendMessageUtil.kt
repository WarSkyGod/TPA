package top.craft_hello.tpa.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.LanguageManager
import java.util.Objects.isNull

class SendMessageUtil {
    // 发送消息
    fun sendMessage(sender: CommandSender, message : Component) {
        if (!isNull(sender)) sender.sendMessage(message)
    }

    // 根据 path 读取配置文件中的消息并发送
    fun sendMessageForPath(sender: CommandSender, path : String, vararg vars: String) {
        var language = LanguageManager.getLanguage(sender)
        sendMessage(sender, language.getFormatPrefixMessage(path, *vars))
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
        var language = LanguageManager.getLanguage(target)
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
        var executorName = executor.name
        var targetName = target.name
        sendMessageForPath(target, "request.to_here", executorName, delay)
        acceptOrDeny(target, executorName)
        sendMessageForPath(executor, "request.to_target", targetName, delay)
        successSentRequest(executor, targetName, delay)
    }


}