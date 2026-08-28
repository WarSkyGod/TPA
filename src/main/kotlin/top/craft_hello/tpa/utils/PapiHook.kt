package top.craft_hello.tpa.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player

// PlaceholderAPI 可选依赖钩子。
// PAPI 不存在时所有方法安全降级；expansion 注册在独立类 TpaExpansion 中，
// 只有 PAPI 存在时才会加载该类，避免 NoClassDefFoundError。
object PapiHook {
    private val papiEnabled: Boolean by lazy {
        Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
    }

    fun isAvailable(): Boolean = papiEnabled

    // 注册 %tpa_*% 占位符（仅当 PAPI 存在）
    fun registerExpansion() {
        if (!papiEnabled) return
        try {
            TpaExpansion().register()
        } catch (ignored: Throwable) {
            // PAPI 注册失败不影响插件主功能
        }
    }

    // 透传 PAPI 占位符（%player_name% 等）；PAPI 不存在时原样返回
    fun setPlaceholders(player: Player, text: String): String {
        if (!papiEnabled) return text
        return try {
            me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text)
        } catch (ignored: Throwable) {
            text
        }
    }
}
