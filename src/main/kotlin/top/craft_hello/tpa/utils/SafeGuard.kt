package top.craft_hello.tpa.utils

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager

// 全局异常拦截兜底：debug=false（生产）时在命令/事件入口捕获异常，
// 玩家收到友好错误提示、后台记录简要日志；debug=true（排错）时不拦截，
// 异常裸抛由 Paper 打印完整堆栈，便于开发者定位。
object SafeGuard {

    // 命令入口兜底（异常拦截时返回 0 表示未成功执行）
    fun command(context: CommandContext<CommandSourceStack>, block: () -> Int): Int {
        if (ConfigManager.config.debug) return block()
        return try {
            block()
        } catch (e: Throwable) {
            TPA.plugin.logger.warning("命令处理异常（已兜底拦截）: ${e.javaClass.name}: ${e.message}")
            val sender = context.source.sender
            if (sender is Player) SendMessageUtil.runtimeError(sender, e.javaClass.simpleName)
            0
        }
    }

    // 事件入口兜底
    fun event(name: String, block: () -> Unit) {
        if (ConfigManager.config.debug) {
            block()
            return
        }
        try {
            block()
        } catch (e: Throwable) {
            TPA.plugin.logger.warning("$name 事件处理异常（已兜底拦截）: ${e.javaClass.name}: ${e.message}")
        }
    }
}
