package top.craft_hello.tpa.utils

import com.mojang.brigadier.context.CommandContext

// Brigadier 公共辅助
object BrigadierUtil {
    // 安全读取命令参数（缺失/类型不符时返回 null）
    inline fun <reified T> CommandContext<*>.getArgumentOrNull(name: String): T? {
        return try {
            getArgument(name, T::class.java)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
