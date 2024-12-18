package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.util.JsonMessage;
import top.craft_hello.tpa.util.MessageUtil;
import top.craft_hello.tpa.util.enums.ClickEvent;
import top.craft_hello.tpa.util.enums.HoverEvent;

import java.util.Objects;

public class Messages {
    private static FileConfiguration langConfig = TPA.getPlugin(TPA.class).getLangConfig();

    public static void setLangConfig(FileConfiguration langConfig){
        if(langConfig == null) return;
        Messages.langConfig = langConfig;
    }

    public static void acceptOrDeny(@NotNull Player target){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(target.getLocale()));
        PlayerSchedulerUtil.syncPlayerPerformOpCommand(target, new JsonMessage(target, Objects.requireNonNull(langConfig.getString("prefix")))
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
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("move"));
        if (executor != target){
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(target.getLocale()));
            MessageUtil.sendMessage(target, langConfig.getString("target_canceled_teleport"));
        }
    }
    public static void successSentRequest(@NotNull Player executor, @NotNull String target, long delay){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("success_sent_request"), target);
        MessageUtil.sendMessage(executor, langConfig.getString("target_seconds"), String.valueOf(delay));
    }

    public static void offlineOrNull(@NotNull Player executor, @NotNull String target){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("offline_or_null"), target);
    }

    public static void warpNull(@NotNull Player executor, @NotNull String warpName){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("warp_null"), warpName);
    }

    public static void requestYou(@NotNull Player executor){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("request_you"));
    }

    public static void targetToYou(@NotNull Player executor, @NotNull Player target, long delay){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(target.getLocale()));
        MessageUtil.sendMessage(target, langConfig.getString("target_to_you"), executor.getName(), String.valueOf(delay));
        acceptOrDeny(target);
        successSentRequest(executor, target.getName(), delay);
    }

    public static void youToTarget(@NotNull Player executor, @NotNull Player target, long delay){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(target.getLocale()));
        MessageUtil.sendMessage(target, langConfig.getString("you_to_target"), executor.getName(), String.valueOf(delay));
        acceptOrDeny(target);
        successSentRequest(executor, target.getName(), delay);
    }

    public static void acceptMessage(@NotNull Player executor, @NotNull Player target, long delay, boolean isTpHere){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("you_accept"), target.getName());
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(target.getLocale()));
        MessageUtil.sendMessage(target, langConfig.getString("target_accept"), executor.getName());
        if (isTpHere){
            tpTimeMessage(executor, target.getName(), delay);
            return;
        }
        tpTimeMessage(target, executor.getName(), delay);
    }

    public static void deny(@NotNull Player executor, @NotNull Player target){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("you_deny"), target.getName());
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(target.getLocale()));
        MessageUtil.sendMessage(target, langConfig.getString("target_deny"), executor.getName());
    }

    public static void tpTimeMessage(@NotNull Player executor, @NotNull String target, long delay){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        if (target.equals("last_location")){
            MessageUtil.sendMessage(executor, langConfig.getString("tp_time"),  langConfig.getString(target), String.valueOf(delay));
            return;
        }
        MessageUtil.sendMessage(executor, langConfig.getString("tp_time"),  target, String.valueOf(delay));
    }
    public static void warpMessage(@NotNull Player executor, @NotNull String warpName){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("tp_to_warp"), warpName);
    }

    public static void setWarp(@NotNull Player executor, @NotNull String warpName){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("set_warp"), warpName);
    }

    public static void timeOverDeny(@NotNull Player executor, @NotNull Player target){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(target.getLocale()));
        MessageUtil.sendMessage(target, langConfig.getString("from_target_expired"), executor.getName());
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("to_target_expired"), target.getName());
    }

    public static void noRequestAccept(@NotNull Player executor){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("no_request_accept"));
    }
    public static void noRequestDeny(@NotNull Player executor){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("no_request_deny"));
    }

    public static void requestLock(@NotNull Player executor){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("request_lock"));
    }

    public static void commandError(@NotNull Player executor, @NotNull String label){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("command_error"), label);
    }

    public static void warpCommandError(@NotNull Player executor, @NotNull String label){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("warp_command_error"), label);
    }

    public static void consoleUseError(@NotNull CommandSender sender){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(TPA.getPlugin(TPA.class).getLang()));
        MessageUtil.sendMessage(sender, langConfig.getString("console_use_error"));
    }

    public static void pluginLoaded(@NotNull CommandSender sender){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(TPA.getPlugin(TPA.class).getLang()));
        MessageUtil.sendMessage(sender, langConfig.getString("plugin_loaded"), sender.getServer().getName());
    }

    public static void pluginUnLoaded(@NotNull CommandSender sender){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(TPA.getPlugin(TPA.class).getLang()));
        MessageUtil.sendMessage(sender, langConfig.getString("plugin_unloaded"));
    }

    public static void configNotFound(@NotNull CommandSender sender){
        if(!(sender instanceof Player)){
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(TPA.getPlugin(TPA.class).getLang()));
        }else {
            Player executor = (Player) sender;
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        }
        MessageUtil.sendMessage(sender, langConfig.getString("config_not_found"));
    }

    public static void configReloaded(@NotNull CommandSender sender){
        if(!(sender instanceof Player)){
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(TPA.getPlugin(TPA.class).getLang()));
        }else {
            Player executor = (Player) sender;
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        }
        MessageUtil.sendMessage(sender, langConfig.getString("config_reloaded"));
    }

    public static void notPermission(@NotNull CommandSender sender){
        if(!(sender instanceof Player)){
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(TPA.getPlugin(TPA.class).getLang()));
        }else {
            Player executor = (Player) sender;
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        }
        MessageUtil.sendMessage(sender, langConfig.getString("not_permission"));
    }

    public static void lastLocationNull(@NotNull CommandSender sender){
        if(!(sender instanceof Player)){
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(TPA.getPlugin(TPA.class).getLang()));
        }else {
            Player executor = (Player) sender;
            Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        }
        MessageUtil.sendMessage(sender, langConfig.getString("last_location_null"));
    }


    public static void backMessage(@NotNull Player executor, @NotNull String target){
        Messages.setLangConfig(TPA.getPlugin(TPA.class).loadLangConfig(executor.getLocale()));
        MessageUtil.sendMessage(executor, langConfig.getString("tp_to_last_location"), langConfig.getString(target));
    }

    public static void pluginError(@NotNull CommandSender sender, @NotNull String message){
        MessageUtil.sendMessage(sender, langConfig.getString("error") + message);
    }
}
