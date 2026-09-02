package top.craft_hello.tpa.utils

import org.bukkit.entity.Player

// 玩家客户端语言获取：Bukkit Player.getLocale()（1.12-1.20.x，返回 "zh_CN" 字符串）
// 与 Paper locale()（新版本，返回 java.util.Locale）版本间签名不同且 1.8-1.11 均不存在；
// 全部走反射双路径探测，本类在 1.8-1.26 全版本服务器都会被类加载，
// 编译期直接引用任何一条路径都会在旧服务器 NoSuchMethodError。
// 无法获取时返回 null，调用方按"未设置语言"回退配置默认语言。
object LocaleUtil {

    fun playerLocale(player: Player): String? {
        // 路径一：Bukkit getLocale()（1.12+ 提供字符串形式；26.x 可能移除）
        runCatching {
            (player.javaClass.getMethod("getLocale").invoke(player) as? String)?.let { return it }
        }
        // 路径二：Paper locale()（返回 java.util.Locale）
        runCatching {
            val locale = player.javaClass.getMethod("locale").invoke(player) as? java.util.Locale
            locale?.let { return "${it.language}_${it.country}" }
        }
        return null
    }
}
