package top.craft_hello.tpa.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import top.craft_hello.tpa.TPA
import top.craft_hello.tpa.enums.CommandType
import top.craft_hello.tpa.enums.PermissionType
import top.craft_hello.tpa.objects.ConfigManager
import top.craft_hello.tpa.utils.SendMessageUtil

object TpacCommand {
    fun tpacCommand(registrar: Commands){
        registrar.register(
            Commands.literal("tpac")
                .executes(::executeHelp)
                .then(
                    Commands.literal("setlang")
                        .then(
                            Commands.argument("language", StringArgumentType.string())
                                .suggests { context, builder ->
                                    val sender = context.source.sender
                                    val input = builder.remaining.lowercase()
                                    val languages = listOf("zh_CN", "en_US")
                                    for (language in languages) {
                                        if (language.lowercase().contains(input)) builder.suggest(language)
                                    }
                                    builder.buildFuture()
                                }
                                .executes(::executeSetLang)
                        )
                )
                .then(
                    Commands.literal("version")
                        .requires { source -> source.sender.hasPermission("tpa.version") or source.sender.hasPermission("tpa.admin") }
                        .executes(::executeVersion)
                )
                .then(
                    Commands.literal("reload")
                        .requires { ConfigManager.config.hasPermission(it.sender, PermissionType.RELOAD) }
                        .executes(::executeReload)
                )
                .build()
        )

    }

    fun executeHelp(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.sender
        player.sendMessage(buildString{
            append("> TPA 帮助：")
            append("> /tpa <玩家名> - 请求传送到该玩家")
            append("> /tphere <玩家名> - 请求让该玩家传送到你" )
            append("> /tpaccept - 接受邀请")
            append("> /tpdeny - 拒绝邀请")
        })
        return Command.SINGLE_SUCCESS
    }

    fun executeReload(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        ConfigManager.reloadAllConfig()
        return SendMessageUtil.configReloaded(sender)
    }

    fun executeVersion(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        sender.sendMessage(Component.text("> 当前版本：4.0.0！"))
        return Command.SINGLE_SUCCESS
    }

    fun executeSetLang(context: CommandContext<CommandSourceStack>): Int {
        val sender = context.source.sender
        sender.sendMessage(Component.text("> 设置玩家语言为：zh_CN！"))
        return Command.SINGLE_SUCCESS
    }
}