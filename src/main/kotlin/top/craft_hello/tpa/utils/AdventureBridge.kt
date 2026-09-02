package top.craft_hello.tpa.utils

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

// Adventure 桥接：全版本（1.8.8+）统一经 BukkitAudiences 发送 Component/Title。
// 低版本服务器由平台层负责序列化（Spigot 1.8.8 内置 BungeeCord chat → tellraw，
// 点击/悬浮交互完整保留；hex/渐变自动降采样为传统 16 色）。
// 本依赖随包 shade 并 relocate，与 Paper 1.18.2+ 原生 Adventure 相互隔离、行为一致。
object AdventureBridge {

    private var audiences: BukkitAudiences? = null

    fun init(plugin: Plugin) {
        if (audiences == null) audiences = BukkitAudiences.create(plugin)
    }

    // onDisable 时释放（须在最后一条消息发送之后调用）
    fun close() {
        audiences?.close()
        audiences = null
    }

    fun sender(sender: CommandSender) = audiences?.sender(sender)

    fun player(player: Player) = audiences?.player(player)
}
