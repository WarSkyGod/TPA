package top.craft_hello.tpa.commands

import org.bukkit.plugin.java.JavaPlugin

// 从 Bukkit CommandMap 移除 plugin.yml 声明的同名命令（Brigadier 路径防重复注册）。
// 仅在 supportsBrigadier 的服务器上调用；低版本服务器的 plugin.yml 命令由 LegacyCommandRouter 接管。
object CommandMapUtil {

    fun removeDeclaredCommands(plugin: JavaPlugin) {
        runCatching {
            val declared = plugin.description.commands.keys
            if (declared.isEmpty()) return
            val server = plugin.server
            val commandMapField = server.javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            val commandMap = commandMapField.get(server) ?: return
            // SimpleCommandMap 的命令表：1.8-1.12 为 commands，1.13+ 为 knownCommands
            val knownField = runCatching { commandMap.javaClass.getDeclaredField("knownCommands") }
                .getOrElse { commandMap.javaClass.getDeclaredField("commands") }
            knownField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val knownCommands = knownField.get(commandMap) as? MutableMap<String, Any?> ?: return
            val pluginNamespace = plugin.name.lowercase()
            for (name in declared) {
                knownCommands.remove(name)
                knownCommands.remove("$pluginNamespace:$name")
            }
        }
    }
}
