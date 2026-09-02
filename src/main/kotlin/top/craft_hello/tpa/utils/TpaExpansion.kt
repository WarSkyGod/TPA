package top.craft_hello.tpa.utils

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.datas.TeleportRequest
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.PlayerDataManager

// %tpa_*% 占位符（PlaceholderAPI 可选依赖）：
// %tpa_homes_amount% 已设置家数量
// %tpa_homes_max%    可设置家的上限（-1 为不限）
// %tpa_homes%        家名列表（逗号分隔）
// %tpa_default_home% 默认家名
// %tpa_pending%      是否有待处理的传送请求（true/false）
// %tpa_language%     当前显示语言
// %tpa_deny_amount%  黑名单数量
// %tpa_version%      插件版本
class TpaExpansion : PlaceholderExpansion() {

    override fun getIdentifier(): String = "tpa"

    override fun getAuthor(): String = "WarSkyGod"

    override fun getVersion(): String = TPA.plugin.description.version

    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer, params: String): String? {
        val data = if (player is Player) PlayerDataManager.get(player) else PlayerDataManager.getIfLoaded(player.uniqueId) ?: return null
        return when (params.lowercase()) {
            "homes_amount" -> data.homes.size.toString()
            "homes_max" -> ConfigManager.config.homeAmountMax(level(player)).toString()
            "homes" -> data.homes.keys.joinToString(",")
            "default_home" -> data.defaultHomeName ?: ""
            "pending" -> (TeleportRequest.requestQueue.containsKey(player.uniqueId)).toString()
            "language" -> data.language ?: ""
            "deny_amount" -> data.denyList.size.toString()
            "version" -> TPA.plugin.description.version
            else -> null
        }
    }

    // 离线玩家无法判定权限，按默认等级处理
    private fun level(player: OfflinePlayer): PermissionType {
        return if (player is Player) PermissionType.getLevel(player) else PermissionType.DEFAULT
    }
}
