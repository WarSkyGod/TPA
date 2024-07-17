package top.craft_hello.tpa;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.util.enums.ClickEvent;
import top.craft_hello.tpa.util.enums.HoverEvent;
import top.craft_hello.tpa.util.MessageUtil;
import top.craft_hello.tpa.util.JsonMessage;

import java.util.Objects;

public class Messages {
    private static final FileConfiguration langConfig = TPA.getPlugin(TPA.class).getLangConfig();

    public static void acceptOrDeny(@NotNull Player target){
        MessageUtil.syncPerformOpCommand(target, new JsonMessage(target, Objects.requireNonNull(langConfig.getString("prefix")))
                .addText(Objects.requireNonNull(langConfig.getString("tpaccept")))
                .addInsertion("/tpaccept")
                .addClickEvent(ClickEvent.RUN_COMMAND, "/tpaccept")
                .addHoverEvent(HoverEvent.SHOW_TEXT, Objects.requireNonNull(langConfig.getString("accept")))
                .addText()
                .addText()
                .addText(Objects.requireNonNull(langConfig.getString("tpdeny")))
                .addInsertion("/tpdeny")
                .addClickEvent(ClickEvent.RUN_COMMAND, "/tpdeny")
                .addHoverEvent(HoverEvent.SHOW_TEXT, Objects.requireNonNull(langConfig.getString("deny")))
                .toString()
        );
    }
    public static void move(@NotNull Player executor, @NotNull Player target){
        MessageUtil.sendMessage(executor, langConfig.getString("move"));
        if (executor != target){
            MessageUtil.sendMessage(target, langConfig.getString("target_canceled_teleport"));
        }
    }
    public static void successSentRequest(@NotNull Player executor, @NotNull String target, long delay){
        MessageUtil.sendMessage(executor, langConfig.getString("success_sent_request"), target);
        MessageUtil.sendMessage(executor, langConfig.getString("target_seconds"), String.valueOf(delay));
    }

    public static void offlineOrNull(@NotNull Player executor, @NotNull String target){
        MessageUtil.sendMessage(executor, langConfig.getString("offline_or_null"), target);
    }

    public static void warpNull(@NotNull Player executor, @NotNull String warpName){
        MessageUtil.sendMessage(executor, langConfig.getString("warp_null"), warpName);
    }

    public static void requestYou(@NotNull Player executor){
        MessageUtil.sendMessage(executor, langConfig.getString("request_you"));
    }

    public static void targetToYou(@NotNull Player executor, @NotNull Player target, long delay){
        MessageUtil.sendMessage(target, langConfig.getString("target_to_you"), executor.getName(), String.valueOf(delay));
        acceptOrDeny(target);
        successSentRequest(executor, target.getName(), delay);
    }

    public static void youToTarget(@NotNull Player executor, @NotNull Player target, long delay){
        MessageUtil.sendMessage(target, langConfig.getString("you_to_target"), executor.getName(), String.valueOf(delay));
        acceptOrDeny(target);
        successSentRequest(executor, target.getName(), delay);
    }

    public static void acceptMessage(@NotNull Player executor, @NotNull Player target, long delay, boolean isTpHere){
        MessageUtil.sendMessage(executor, langConfig.getString("you_accept"), target.getName());
        MessageUtil.sendMessage(target, langConfig.getString("target_accept"), executor.getName());
        if (isTpHere){
            tpTimeMessage(executor, target.getName(), delay);
            return;
        }
        tpTimeMessage(target, executor.getName(), delay);
    }

    public static void deny(@NotNull Player executor, @NotNull Player target){
        MessageUtil.sendMessage(executor, langConfig.getString("you_deny"), target.getName());
        MessageUtil.sendMessage(target, langConfig.getString("target_deny"), executor.getName());
    }

    public static void tpTimeMessage(@NotNull Player executor, @NotNull String target, long delay){
        MessageUtil.sendMessage(executor, langConfig.getString("tp_time"),  target, String.valueOf(delay));
    }
    public static void warpMessage(@NotNull Player executor, @NotNull String warpName){
        MessageUtil.sendMessage(executor, langConfig.getString("tp_to_warp"), warpName);
    }

    public static void setWarp(@NotNull Player executor, @NotNull String warpName){
        MessageUtil.sendMessage(executor, langConfig.getString("set_warp"), warpName);
    }

    public static void timeOverDeny(@NotNull Player executor, @NotNull Player target){
        MessageUtil.sendMessage(target, langConfig.getString("from_target_expired"), executor.getName());
        MessageUtil.sendMessage(executor, langConfig.getString("to_target_expired"), target.getName());
    }

    public static void noRequestAccept(@NotNull Player executor){
        MessageUtil.sendMessage(executor, langConfig.getString("no_request_accept"));
    }
    public static void noRequestDeny(@NotNull Player executor){
        MessageUtil.sendMessage(executor, langConfig.getString("no_request_deny"));
    }

    public static void requestLock(@NotNull Player executor){
        MessageUtil.sendMessage(executor, langConfig.getString("request_lock"));
    }

    public static void commandError(@NotNull Player executor, @NotNull String label){
        MessageUtil.sendMessage(executor, langConfig.getString("command_error"), label);
    }

    public static void warpCommandError(@NotNull Player executor, @NotNull String label){
        MessageUtil.sendMessage(executor, langConfig.getString("warp_command_error"), label);
    }

    public static void consoleUseError(@NotNull CommandSender sender){
        MessageUtil.sendMessage(sender, langConfig.getString("console_use_error"));
    }

    public static void pluginLoaded(@NotNull CommandSender sender){
        MessageUtil.sendMessage(sender, langConfig.getString("plugin_loaded"), sender.getServer().getName());
    }

    public static void pluginUnLoaded(@NotNull CommandSender sender){
        MessageUtil.sendMessage(sender, langConfig.getString("plugin_unloaded"));
    }

    public static void configNotFound(@NotNull CommandSender sender){
        MessageUtil.sendMessage(sender, langConfig.getString("config_not_found"));
    }

    public static void configReloaded(@NotNull CommandSender sender){
        MessageUtil.sendMessage(sender, langConfig.getString("config_reloaded"));
    }

    public static void notPermission(@NotNull CommandSender sender){
        MessageUtil.sendMessage(sender, langConfig.getString("not_permission"));
    }
}
