package top.craft_hello.tpa.utils

import org.bukkit.Bukkit

// 服务器版本检测：bukkitVersion 形如 1.8.8-R0.1-SNAPSHOT、1.21.10-R0.1-SNAPSHOT、26.2-R0.1-SNAPSHOT
object TpaVersion {

    private val parts: List<Int> = Bukkit.getBukkitVersion()
        .substringBefore('-')
        .split('.')
        .map { it.toIntOrNull() ?: 0 }

    val major: Int = parts.getOrElse(0) { 1 }
    val minor: Int = parts.getOrElse(1) { 0 }
    private val patch: Int = parts.getOrElse(2) { 0 }

    // Paper 1.20.6+ 提供 Brigadier lifecycle 命令 API（1.21 起稳定；新版本号方案 2.x+ 均支持）
    // 其余版本走 plugin.yml + LegacyCommandRouter 传统命令路由
    val supportsBrigadier: Boolean = major > 1 || minor > 20 || (minor == 20 && patch >= 6)

    // 1.8.x 的 Sound 枚举为旧命名（无 ENTITY_/BLOCK_ 前缀），1.9 起改为新命名
    val legacySoundNames: Boolean = major == 1 && minor <= 8

    // Bukkit PlayerLocaleChangeEvent 与 Player.getLocale() 自 1.12 起提供；
    // 更低版本无法感知客户端语言切换（跟随逻辑在加入时回退配置默认语言）
    val supportsLocaleEvent: Boolean = major > 1 || minor >= 12

    // Paper 的 World.getChunkAtAsync 异步取 chunk 自 1.13 起提供；
    // 1.8-1.12 回退主线程同步扫描（Bukkit 同步取 chunk 自动加载，安全但阻塞主线程）
    val supportsAsyncChunk: Boolean = major > 1 || minor >= 13

    // World.getMinHeight 自 1.17 起提供（此前主/下/末世界下限恒为 0）
    val supportsWorldMinHeight: Boolean = major > 1 || minor >= 17

    // 版本描述（调试用）
    fun describe(): String = "MC ${major}.${minor}${if (patch > 0) ".${patch}" else ""}" +
        " (brigadier=${supportsBrigadier}, legacySound=${legacySoundNames})"
}
