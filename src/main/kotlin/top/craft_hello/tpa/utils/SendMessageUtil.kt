package top.craft_hello.tpa.utils

import cn.handyplus.lib.adapter.PlayerSchedulerUtil
import com.mojang.brigadier.Command
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.objects.PlayerDataManager
import java.util.UUID

// 消息发送工具。语言文件采用 MiniMessage；按钮的点击/悬浮交互内嵌于语言文本。
class SendMessageUtil {
    companion object {
        // 发送消息
        fun sendMessage(sender: CommandSender?, message: Component) {
            if (sender != null) sender.sendMessage(message)
        }

        // 根据 path 读取配置文件中的消息并发送
        fun sendMessageForPath(sender: CommandSender, path: String, vararg vars: String) {
            val language = LanguageManager.getLanguage(sender)
            sendMessage(sender, language.getFormatPrefixMessage(sender, path, *vars))
        }

        // =============== 错误消息 ===============

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

        // 在此世界中无法使用该命令错误
        fun worldDisabledError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.world_disabled")
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

        // 命令语法错误（通用）
        fun syntaxGenericError(sender: CommandSender, command: String) : Int {
            sendMessageForPath(sender, "error.syntax_generic", command)
            return Command.SINGLE_SUCCESS
        }

        // tpa/tphere 命令语法错误
        fun syntaxTpaError(player: Player, command: String) : Int {
            sendMessageForPath(player, "error.syntax_tpa", command)
            return Command.SINGLE_SUCCESS
        }

        // tpall 命令语法错误
        fun syntaxTpAllError(player: Player, command: String) : Int {
            sendMessageForPath(player, "error.syntax_tpall", command)
            return Command.SINGLE_SUCCESS
        }

        // warp 命令语法错误
        fun syntaxWarpError(sender: CommandSender, command: String) : Int {
            sendMessageForPath(sender, "error.syntax_warp", command)
            return Command.SINGLE_SUCCESS
        }

        // home 命令语法错误
        fun syntaxHomeError(sender: CommandSender, command: String) : Int {
            sendMessageForPath(sender, "error.syntax_home", command)
            return Command.SINGLE_SUCCESS
        }

        // 目标玩家不在线错误
        fun targetOfflineError(sender: CommandSender, targetName: String) : Int {
            sendMessageForPath(sender, "error.target_offline", targetName)
            return Command.SINGLE_SUCCESS
        }

        // 不能对自己执行错误
        fun selfOperationError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.self_operation")
            return Command.SINGLE_SUCCESS
        }

        // 没有在线玩家错误
        fun noOnlinePlayersError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.no_online_players")
            return Command.SINGLE_SUCCESS
        }

        // 请求待处理错误
        fun requestPendingError(player: Player) : Int {
            sendMessageForPath(player, "error.request_pending")
            return Command.SINGLE_SUCCESS
        }

        // 没有待处理请求错误
        fun noPendingRequestError(player: Player) : Int {
            sendMessageForPath(player, "error.no_pending_request")
            return Command.SINGLE_SUCCESS
        }

        // 对方已在黑名单错误
        fun alreadyBlacklistedError(player: Player) : Int {
            sendMessageForPath(player, "error.already_blacklisted")
            return Command.SINGLE_SUCCESS
        }

        // 已被对方拉黑错误
        fun blockedByTargetError(player: Player) : Int {
            sendMessageForPath(player, "error.blocked_by_target")
            return Command.SINGLE_SUCCESS
        }

        // 黑名单为空错误
        fun blacklistEmptyError(player: Player) : Int {
            sendMessageForPath(player, "error.blacklist_empty")
            return Command.SINGLE_SUCCESS
        }

        // 对方不在黑名单错误
        fun notBlacklistedError(player: Player) : Int {
            sendMessageForPath(player, "error.not_blacklisted")
            return Command.SINGLE_SUCCESS
        }

        // 家不存在错误
        fun homeNotFoundError(sender: CommandSender, homeName: String) : Int {
            sendMessageForPath(sender, "error.home_not_found", homeName)
            return Command.SINGLE_SUCCESS
        }

        // 没有设置家错误
        fun noHomesSetError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.no_homes_set")
            return Command.SINGLE_SUCCESS
        }

        // 没有默认家错误
        fun noDefaultHomeError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.no_default_home")
            return Command.SINGLE_SUCCESS
        }

        // 已是默认家错误
        fun defaultHomeAlreadySetError(sender: CommandSender, homeName: String) : Int {
            sendMessageForPath(sender, "error.default_home_already_set", homeName)
            return Command.SINGLE_SUCCESS
        }

        // 家数量达到上限错误
        fun homeMaxLimitError(player: Player, max: String) : Int {
            sendMessageForPath(player, "home.max_limit_error", max)
            return Command.SINGLE_SUCCESS
        }

        // 传送点不存在错误
        fun warpNotFoundError(sender: CommandSender, warpName: String) : Int {
            sendMessageForPath(sender, "error.warp_not_found", warpName)
            return Command.SINGLE_SUCCESS
        }

        // 没有可用传送点错误
        fun noWarpsSetError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.no_warps_set")
            return Command.SINGLE_SUCCESS
        }

        // 没有可用的上一次位置错误
        fun lastLocationMissingError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.last_location_missing")
            return Command.SINGLE_SUCCESS
        }

        // 没有最后下线位置记录错误
        fun logoutLocationMissingError(sender: CommandSender, targetName: String) : Int {
            sendMessageForPath(sender, "error.logout_location_missing", targetName)
            return Command.SINGLE_SUCCESS
        }

        // 未设置主城错误
        fun spawnNotSetError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "error.spawn_not_set")
            return Command.SINGLE_SUCCESS
        }

        // 随机传送失败错误
        fun rtpFailedError(player: Player) : Int {
            sendMessageForPath(player, "rtp.failed")
            return Command.SINGLE_SUCCESS
        }

        // 插件运行时错误
        fun runtimeError(sender: CommandSender, message: String) : Int {
            sendMessageForPath(sender, "error.runtime", message)
            return Command.SINGLE_SUCCESS
        }

        // 更新检查失败错误
        fun updateFailedError(sender: CommandSender) : Int {
            sendMessageForPath(sender, "update.failed")
            return Command.SINGLE_SUCCESS
        }

        // =============== 系统消息 ===============

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
        fun pluginUpdateMessage(sender : CommandSender, latestVersion : String) {
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
        fun configReloaded(sender : CommandSender) : Int {
            // 向服务器后台发送重载消息
            sendMessageForPath(Bukkit.getConsoleSender(), "system.config_reloaded")
            // 如果是玩家执行，则也向该玩家发送重载消息
            if (sender is Player) sendMessageForPath(sender, "system.config_reloaded")
            return Command.SINGLE_SUCCESS
        }

        // =============== 成功消息 ===============

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

        // 返回主城成功消息
        fun backSpawnSuccessMessage(player: Player) {
            val language = LanguageManager.getLanguage(player)
            sendMessageForPath(player, "spawn.teleport_success", language.getMessage("spawn_name"))
        }

        // 传送到上一次的位置成功消息
        fun backLastLocationSuccessMessage(player: Player) {
            sendMessageForPath(player, "back.teleport_success")
        }

        // 设置传送点成功消息
        fun setWarpSuccess(player: Player, warpName: String) : Int {
            sendMessageForPath(player, "warp.set_success", warpName)
            return Command.SINGLE_SUCCESS
        }

        // 删除传送点成功消息
        fun delWarpSuccess(sender: CommandSender, warpName: String) : Int {
            sendMessageForPath(sender, "warp.delete_success", warpName)
            return Command.SINGLE_SUCCESS
        }

        // 传送到传送点成功消息
        fun tpToWarpMessage(player: Player, warpName: String) {
            sendMessageForPath(player, "warp.teleport_success", warpName)
        }

        // 设置家成功消息
        fun setHomeSuccess(player: Player, homeName: String) : Int {
            sendMessageForPath(player, "home.set_success", homeName)
            return Command.SINGLE_SUCCESS
        }

        // 设置默认家成功消息
        fun setDefaultHomeSuccess(player: Player, homeName: String) : Int {
            sendMessageForPath(player, "home.default_set_success", homeName)
            return Command.SINGLE_SUCCESS
        }

        // 删除家成功消息
        fun delHomeSuccess(player: Player, homeName: String) : Int {
            sendMessageForPath(player, "home.delete_success", homeName)
            return Command.SINGLE_SUCCESS
        }

        // 传送回家成功消息
        fun tpToHomeMessage(player: Player, homeName: String) {
            sendMessageForPath(player, "home.teleport_success", homeName)
        }

        // 设置语言成功消息
        fun setLangCommandSuccess(player: Player, language: String) : Int {
            sendMessageForPath(player, "lang.set_success", language)
            return Command.SINGLE_SUCCESS
        }

        // 加入黑名单成功消息
        fun addDenysSuccess(player: Player, targetName: String) : Int {
            sendMessageForPath(player, "blacklist.add_success", targetName)
            return Command.SINGLE_SUCCESS
        }

        // 移出黑名单成功消息
        fun removeDenySuccess(player: Player, targetName: String) : Int {
            sendMessageForPath(player, "blacklist.remove_success", targetName)
            return Command.SINGLE_SUCCESS
        }

        // 随机传送成功消息
        fun rtpSuccessMessage(player: Player) {
            sendMessageForPath(player, "rtp.success")
        }

        // 正在生成随机传送点消息
        fun generateRandomLocationMessage(player: Player) {
            sendMessageForPath(player, "rtp.generating")
        }

        // 传送至对方位置成功消息
        fun youTeleportedToMessage(player: Player, targetName: String) {
            sendMessageForPath(player, "teleport.generic_success", targetName)
        }

        // /tpall 目标玩家侧消息
        fun adminTpYouToMessage(player: Player, adminName: String) {
            sendMessageForPath(player, "teleport.tpall.to_target", adminName)
        }

        // /tpall 管理员侧消息（传送到自己）
        fun adminTpAllPlayerToYouMessage(player: Player) {
            sendMessageForPath(player, "teleport.tpall.to_self")
        }

        // /tpall 成功消息（传送到指定目标）
        fun tpAllCommandSuccess(sender: CommandSender, targetName: String) : Int {
            sendMessageForPath(sender, "teleport.tpall.success", targetName)
            return Command.SINGLE_SUCCESS
        }

        // /tplogout 成功消息
        fun tpLogoutCommandSuccess(player: Player, targetName: String) {
            sendMessageForPath(player, "teleport.logout_location", targetName)
        }

        // =============== 请求消息 ===============

        // 请求方传送到目标的请求消息（目标收到 + 双方确认）
        fun requestTeleportToTarget(executor : Player, target : Player, delay : String) {
            val executorName = executor.name
            val targetName = target.name
            sendMessageForPath(target, "request.to_here", executorName, delay)
            acceptOrDeny(target, executorName)
            successSentRequest(executor, targetName, delay)
        }

        // 目标传送到请求方的请求消息（tphere）
        fun requestTargetTeleportToHere(executor : Player, target : Player, delay : String) {
            val executorName = executor.name
            val targetName = target.name
            sendMessageForPath(target, "request.to_target", executorName, delay)
            acceptOrDeny(target, executorName)
            successSentRequest(executor, targetName, delay)
        }

        // 请求按钮消息（接受 / 拒绝 / 拒绝并加入黑名单，交互内嵌于语言文本）
        fun acceptOrDeny(target : Player, executorName : String) {
            val language = LanguageManager.getLanguage(target)
            val accept = language.getFormatMessage(target, "accept_button", executorName)
            val deny = language.getFormatMessage(target, "deny_button", executorName)
            val blacklist = language.getFormatMessage(target, "blacklist.add_button", executorName)
            sendMessage(target, accept.append(deny).append(blacklist))
        }

        // 成功发送请求消息
        fun successSentRequest(executor : Player, target : String, delay : String) {
            sendMessageForPath(executor, "request.sent_success", target)
            sendMessageForPath(executor, "request.timeout_notice", delay)
        }

        // 接受请求消息（双方）
        fun acceptMessage(executor : Player, target : Player) {
            sendMessageForPath(executor, "teleport.accept.target", target.name)
            sendMessageForPath(target, "teleport.accept.self", executor.name)
        }

        // 拒绝请求消息（双方）
        fun denyMessage(executor : Player, target : Player) {
            sendMessageForPath(executor, "teleport.deny.target", target.name)
            sendMessageForPath(target, "teleport.deny.self", executor.name)
        }

        // 请求超时自动拒绝消息（双方）
        fun timeOverDeny(executor : Player, target : Player) {
            sendMessageForPath(executor, "request.expired_to", target.name)
            sendMessageForPath(target, "request.expired_from", executor.name)
        }

        // 移动取消传送消息（对方可为空，位置类传送无对方）
        fun move(executor : Player, target : Player?) {
            if (target != null && target.isOnline) sendMessageForPath(target, "teleport.canceled.target", executor.name)
            sendMessageForPath(executor, "teleport.canceled.self")
        }

        // =============== 传送过程提示 ===============

        // 传送倒计时消息（{target} 为位置显示名时使用语言值）
        fun teleportCountdown(executor: Player, target: String, delay: String) {
            var target = target
            val language = LanguageManager.getLanguage(executor)
            if ("last_location" == target || "rtp_name" == target || "spawn_name" == target) target = language.getMessage(target)
            sendMessageForPath(executor, "teleport.countdown", target, delay)
            sendMessageForPath(executor, "teleport.cancel_on_move")
        }

        // title 样式的传送倒计时消息
        fun titleCountdownMessage(executor: Player, target: String, delay: String) {
            var target = target
            val language = LanguageManager.getLanguage(executor)
            if ("last_location" == target || "rtp_name" == target || "spawn_name" == target) target = language.getMessage(target)
            val title = language.getFormatMessage(executor, "teleport.countdown", target, delay)
            val subTitle = language.getFormatMessage(executor, "teleport.cancel_on_move")
            executor.showTitle(Title.title(title, subTitle))
        }

        // 传送音效：仅受 enable_sound 控制（独立于 title 设置），音效/音量/音调从配置读取
        fun playTeleportSound(executor: Player) {
            val config = top.craft_hello.tpa.objects.ConfigManager.config
            if (config.enableSound) PlayerSchedulerUtil.playSound(executor, config.teleportSound, config.soundVolume, config.soundPitch)
        }

        // title 样式的传送完成消息
        fun titleCountdownOverMessage(executor: Player, target: String) {
            var target = target
            val language = LanguageManager.getLanguage(executor)
            if ("last_location" == target || "rtp_name" == target || "spawn_name" == target) target = language.getMessage(target)
            val title = language.getFormatMessage(executor, "teleport.generic_success", target)
            executor.showTitle(Title.title(title, Component.empty()))
        }

        // title 样式的正在生成随机传送点消息
        fun titleGenerateRandomLocationMessage(executor: Player) {
            val language = LanguageManager.getLanguage(executor)
            val title = language.getFormatMessage(executor, "rtp.generating")
            executor.showTitle(Title.title(title, Component.empty()))
        }

        // =============== 列表消息 ===============

        // 通用交互列表：名字行 + 按钮行（按钮交互内嵌于语言文本）
        fun listMessage(
            executor: CommandSender,
            targetNames: List<String>,
            command: String,
            teleportButton: Boolean = false,
            settingButton: Boolean = false,
            settingDefaultHomeButton: Boolean = false,
            deleteButton: Boolean = false,
            removeDenysButton: Boolean = false
        ) {
            val language = LanguageManager.getLanguage(executor)
            for (targetName in targetNames) {
                sendMessage(executor, language.getFormatMessage(executor, "prefix").append(Component.text(targetName)))
                // Adventure Component 不可变：append 返回新组件，必须重新赋值（否则按钮全部丢失）
                var row: Component = Component.empty()
                if (teleportButton) {
                    row = row.append(language.getFormatMessage(executor, "teleport_button", targetName, command))
                }
                if (settingButton) {
                    row = row.append(language.getFormatMessage(executor, "location_set_button", targetName, command))
                }
                if (settingDefaultHomeButton && command == "home" && !defaultHomeEquals(executor, targetName)) {
                    row = row.append(language.getFormatMessage(executor, "default_home_set_button", targetName, command))
                }
                if (deleteButton) {
                    row = row.append(language.getFormatMessage(executor, "delete_button", targetName, command))
                }
                if (removeDenysButton) {
                    row = row.append(language.getFormatMessage(executor, "blacklist.remove_button", targetName))
                }
                sendMessage(executor, row)
            }
        }

        private fun defaultHomeEquals(sender: CommandSender, homeName: String): Boolean {
            if (sender !is Player) return false
            return PlayerDataManager.get(sender).equalsDefaultHomeName(homeName)
        }

        // 家列表（含 传送 / 设置位置 / 设为默认家 / 删除 按钮）
        fun homeListMessage(executor: Player, homeNameList: List<String>) {
            if (homeNameList.isEmpty()) {
                // 对齐 3.x：空列表用 error.no_homes_set（not_homes 在 3.x 仅用于 tab 补全提示文字）
                sendMessageForPath(executor, "error.no_homes_set")
                return
            }
            sendMessageForPath(executor, "home.list_header")
            listMessage(executor, homeNameList, "home", teleportButton = true, settingButton = true, settingDefaultHomeButton = true, deleteButton = true)
        }

        // 传送点列表（含 传送 按钮；控制台只显示名字）
        fun warpListMessage(sender: CommandSender, warpList: List<String>) {
            if (warpList.isEmpty()) {
                // 对齐 3.x：空列表用 error.no_warps_set（not_warps 在 3.x 仅用于 tab 补全提示文字）
                noWarpsSetError(sender)
                return
            }
            sendMessageForPath(sender, "warp.list_header")
            if (sender is Player) {
                // 对齐 3.x：设置位置/删除按钮按管理权限显示
                val settingButton = ConfigManager.config.hasPermission(sender, PermissionType.SET_WARP)
                val deleteButton = ConfigManager.config.hasPermission(sender, PermissionType.DEL_WARP)
                listMessage(sender, warpList, "warp", teleportButton = true, settingButton = settingButton, deleteButton = deleteButton)
            } else {
                for (warpName in warpList) sendMessage(sender, Component.text(warpName))
            }
        }

        // 黑名单显示名（UUID -> 玩家名，无法解析时显示 UUID）
        fun denyDisplayName(denyUuid: String): String {
            return try {
                val offline: OfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(denyUuid))
                offline.name ?: denyUuid
            } catch (e: IllegalArgumentException) {
                denyUuid
            }
        }

        // 黑名单列表（含 移出黑名单 按钮）
        fun denysMessage(executor: Player, denyList: List<String>) {
            if (denyList.isEmpty()) {
                blacklistEmptyError(executor)
                return
            }
            sendMessageForPath(executor, "blacklist.list_header")
            listMessage(executor, denyList.map { denyDisplayName(it) }, "denys", removeDenysButton = true)
        }

        private object ConfigHolder {
            fun enableSound(): Boolean = top.craft_hello.tpa.objects.ConfigManager.config.enableSound
        }
    }
}
