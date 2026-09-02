package top.craft_hello.tpa.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.objects.LanguageManager
import top.craft_hello.tpa.objects.PlayerDataManager
import top.craft_hello.tpa.utils.SafeGuard
import top.craft_hello.tpa.utils.SendMessageUtil

// 1.8.8+ 传统命令路由：plugin.yml 声明的命令统一分发到各命令对象的
// executeXxx(sender, args)（与 Brigadier 树共用同一份业务实现），并按规则补全。
// 本类不 import 任何 Brigadier/Paper 高版本 API，低版本服务器可安全加载。
object LegacyCommandRouter : CommandExecutor, TabCompleter {

    fun register(plugin: TPA) {
        for (name in plugin.description.commands.keys) {
            val command = plugin.getCommand(name) ?: continue
            command.executor = this
            command.tabCompleter = this
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val argList = args.toList()
        SafeGuard.commandLegacy(sender) {
            when (command.name.lowercase()) {
                "tpa" -> TpaCommand.executeTpa(sender, argList)
                "tpac" -> TpacCommand.route(sender, argList)
                "tphere" -> TphereCommand.executeTphere(sender, argList)
                "tpaccept" -> TpacceptCommand.executeTpaccept(sender, argList)
                "tpdeny" -> TpdenyCommand.executeTpdeny(sender, argList)
                "denys" -> DenysCommand.executeDenys(sender, argList)
                "tpall" -> TpAllCommand.executeTpAll(sender, argList)
                "tplogout" -> TpLogoutCommand.executeTpLogout(sender, argList)
                "rtp" -> RtpCommand.executeRtp(sender, argList)
                "back" -> BackCommand.executeBack(sender, argList)
                "warp" -> WarpCommand.executeWarp(sender, argList)
                "setwarp" -> SetWarpCommand.executeSetWarp(sender, argList)
                "delwarp" -> DelWarpCommand.executeDelWarp(sender, argList)
                "home" -> HomeCommand.executeHome(sender, argList)
                "homes" -> HomesCommand.executeHomes(sender, argList)
                "sethome" -> SetHomeCommand.executeSetHome(sender, argList)
                "setdefaulthome" -> SetDefaultHomeCommand.executeSetDefaultHome(sender, argList)
                "delhome" -> DelHomeCommand.executeDelHome(sender, argList)
                "spawn" -> SpawnCommand.executeSpawn(sender, argList)
                "setspawn" -> SetSpawnCommand.executeSetSpawn(sender, argList)
                "delspawn" -> DelSpawnCommand.executeDelSpawn(sender, argList)
                else -> SendMessageUtil.syntaxGenericError(sender, command.name)
            }
        }
        return true
    }

    // Tab 补全（对齐 Brigadier 树内的 suggests 规则）
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String>? {
        val name = command.name.lowercase()
        val argList = args.toList()
        val input = (argList.lastOrNull() ?: "").lowercase()
        return when (name) {
            "tpac" -> suggestTpac(argList, input)
            "denys" -> suggestDenys(argList, input)
            "tpall" -> suggestTpAll(argList, input)
            "tpa", "tphere" -> if (argList.size == 1) onlineNameSuggestions(sender, input, excludeSelf = true) else emptyList()
            "tplogout" -> if (argList.size == 1) Bukkit.getOfflinePlayers().mapNotNull { it.name }.filter { it.lowercase().contains(input) } else emptyList()
            "warp", "delwarp" -> if (argList.size == 1) ConfigManager.warpConfig.getWarpNames().filter { it.lowercase().contains(input) } else emptyList()
            "home", "delhome", "setdefaulthome" -> {
                if (sender is Player && argList.size == 1) {
                    PlayerDataManager.get(sender).homeNames().filter { it.lowercase().contains(input) }
                } else emptyList()
            }
            else -> emptyList()
        }
    }

    // /tpac：子命令 + setlang 的语言参数
    private fun suggestTpac(argList: List<String>, input: String): List<String> {
        return when (argList.size) {
            1 -> listOf("version", "setlang", "reload").filter { it.startsWith(input) }
            2 -> if (argList[0].equals("setlang", ignoreCase = true)) {
                (listOf("clear") + LanguageManager.getLanguageNames()).filter { it.lowercase().contains(input) }
            } else emptyList()
            else -> emptyList()
        }
    }

    // /denys：add/remove 子命令 + 玩家名参数
    private fun suggestDenys(argList: List<String>, input: String): List<String> {
        return when (argList.size) {
            1 -> listOf("add", "remove").filter { it.startsWith(input) }
            2 -> onlineNameSuggestions(null, input, excludeSelf = false)
            else -> emptyList()
        }
    }

    // /tpall：player/warp/spawn 子命令 + 对应名称参数
    private fun suggestTpAll(argList: List<String>, input: String): List<String> {
        return when (argList.size) {
            1 -> listOf("player", "warp", "spawn").filter { it.startsWith(input) }
            2 -> when {
                argList[0].equals("player", ignoreCase = true) ->
                    Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().contains(input) }
                argList[0].equals("warp", ignoreCase = true) ->
                    ConfigManager.warpConfig.getWarpNames().filter { it.lowercase().contains(input) }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }

    // 在线玩家名补全；excludeSelf=true 时排除补全者自己（对齐 tpa/tphere 的不能对自己发起请求）
    private fun onlineNameSuggestions(sender: CommandSender?, input: String, excludeSelf: Boolean): List<String> {
        val self = if (excludeSelf) sender as? Player else null
        return Bukkit.getOnlinePlayers()
            .asSequence()
            .map { it.name }
            .filter { name ->
                (self == null || name != self.name) && name.lowercase().contains(input)
            }
            .toList()
    }
}
