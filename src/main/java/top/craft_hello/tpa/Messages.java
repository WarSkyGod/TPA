package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.enums.ClickEventType;
import top.craft_hello.tpa.enums.HoverEventType;
import top.craft_hello.tpa.utils.JsonMessageUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.MessageUtil;

import java.util.Objects;


public class Messages {

    // 获取服务端配置的语言
    private static FileConfiguration defaultLang = LoadingConfigFileUtil.getLang();

    // 这个随玩家语言动态变化
    private static FileConfiguration lang = LoadingConfigFileUtil.getLang();

    // 设置为指定语言
    private static CommandSender setLang(@NotNull FileConfiguration lang){
        Messages.lang = lang;
        return Bukkit.getConsoleSender();
    }

    // 自动判断语言
    private static CommandSender setLang(@NotNull CommandSender executor){

        setLang((executor instanceof Player) ? LoadingConfigFileUtil.getLang(((Player) executor).locale().getLanguage() + "_" + ((Player) executor).locale().getCountry()) : defaultLang);
        return executor;
    }

    // 重新获取服务端配置的语言
    public static void reloadDefaultLang(){
        defaultLang = LoadingConfigFileUtil.getLang();
    }

    // 配置文件迁移消息
    public static void configVersionUpdate(@NotNull CommandSender sender){
        MessageUtil.sendMessage(setLang(defaultLang), lang.getString("config_version_update"), sender.getServer().getName());
    }

    // 配置文件迁移成功消息
    public static void configVersionUpdateSuccess(@NotNull CommandSender sender){
        MessageUtil.sendMessage(setLang(defaultLang), lang.getString("config_version_update_success"), sender.getServer().getName());
    }

    // 发现更新消息
    public static void pluginUpdateMessage(@NotNull CommandSender executor, String latestVersion){
        MessageUtil.sendMessage(setLang(executor), lang.getString("plugin_update_message"), latestVersion);
    }

    // 插件加载消息
    public static void pluginLoaded(@NotNull String version){
        MessageUtil.sendMessage(setLang(defaultLang), lang.getString("plugin_loaded"), version);
    }

    // 插件卸载消息
    public static void pluginUnLoaded(){
        MessageUtil.sendMessage(setLang(defaultLang), lang.getString("plugin_unloaded"));
    }

    // 配置文件重载消息
    public static void configReloaded(@NotNull CommandSender executor){
        // 向服务器后台发送重载消息
        MessageUtil.sendMessage(setLang(defaultLang), lang.getString("config_reloaded"));
        // 如果是玩家执行，则也向该玩家发送重载消息
        if(executor instanceof Player) {
            MessageUtil.sendMessage(setLang(executor), lang.getString("config_reloaded"));
        }
    }

    // 插件错误消息
    public static void pluginError(@NotNull CommandSender sender, @NotNull String message){
        // 向服务器后台发送错误消息
        MessageUtil.sendMessage(setLang(defaultLang), lang.getString("plugin_error") + message);
        // 如果是玩家导致的，则也向该玩家发送错误消息
        if((sender instanceof Player)){
            MessageUtil.sendMessage(setLang(sender), lang.getString("plugin_error") + message);
        }

    }

    // 服务器未启用此命令消息
    public static void disableCommandError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("disable_command_error"));
    }

    // 没有权限消息
    public static void notPermissionError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_permission_error"));
    }

    // 命令只能由玩家执行消息
    public static void consoleUseError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("console_use_error"));
    }

    // 命令错误消息
    public static void commandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("command_error"), label);
    }

    // tpa命令错误消息
    public static void tpaCommandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("tpa_command_error"), label);
    }

    // warp命令错误消息
    public static void warpCommandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("warp_command_error"), label);
    }

    // home命令错误消息
    public static void homeCommandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("home_command_error"), label);
    }

    // 目标玩家离线或不存在消息
    public static void offlineOrNullError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("offline_or_null_error"));
    }

    // 不能自己请求自己消息
    public static void selfRequestedError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("self_requested_error"));
    }

    // 自己或对方有尚未结束的请求消息
    public static void requestLockError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("request_lock_error"));
    }

    // 请求传送到对方的位置消息
    public static void requestTeleportToTarget(@NotNull CommandSender executor, @NotNull CommandSender target, long delay){
        MessageUtil.sendMessage(setLang(target), lang.getString("request_teleport_to_target"), executor.getName(), String.valueOf(delay));
        acceptOrDeny((Player) target);
        successSentRequest(setLang(executor), target.getName(), delay);
    }

    // 请求对方传送到你的位置消息
    public static void requestTargetTeleportToHere(@NotNull CommandSender executor, @NotNull CommandSender target, long delay){
        MessageUtil.sendMessage(setLang(target), lang.getString("request_target_teleport_to_here"), executor.getName(), String.valueOf(delay));
        acceptOrDeny((Player) target);
        successSentRequest(setLang(executor), target.getName(), delay);
    }

    // 接受或拒绝消息
    public static void acceptOrDeny(@NotNull Player target){
        setLang(target);
        PlayerSchedulerUtil.syncDispatchCommand(new JsonMessageUtil(target, Objects.requireNonNull(lang.getString("prefix")))
                .addText(Objects.requireNonNull(lang.getString("tpaccept")))
                .addInsertion("/tpaccept")
                .addClickEvent(ClickEventType.RUN_COMMAND, "/tpaccept")
                .addHoverEvent(HoverEventType.SHOW_TEXT, Objects.requireNonNull(lang.getString("accept")))
                .addText()
                .addText()
                .addText(Objects.requireNonNull(lang.getString("tpdeny")))
                .addInsertion("/tpdeny")
                .addClickEvent(ClickEventType.RUN_COMMAND, "/tpdeny")
                .addHoverEvent(HoverEventType.SHOW_TEXT, Objects.requireNonNull(lang.getString("deny")))
                .toString()
        );
    }

    // 成功发送请求消息
    public static void successSentRequest(@NotNull CommandSender executor, @NotNull String target, long delay){
        MessageUtil.sendMessage(setLang(executor), lang.getString("success_sent_request"), target);
        MessageUtil.sendMessage(executor, lang.getString("target_seconds"), String.valueOf(delay));
    }

    // 接受传送消息
    public static void acceptMessage(@NotNull CommandSender executor, @NotNull CommandSender target, long delay, boolean isTphere){
        if (isTphere){
            MessageUtil.sendMessage(setLang(executor), lang.getString("you_accept"), target.getName());
            MessageUtil.sendMessage(setLang(target), lang.getString("target_accept"), executor.getName());
            teleportCountdown(executor, executor.getName(), delay);
            return;
        }
        MessageUtil.sendMessage(setLang(target), lang.getString("you_accept"), executor.getName());
        MessageUtil.sendMessage(setLang(executor), lang.getString("target_accept"), target.getName());
        teleportCountdown(executor, executor.getName(), delay);
    }

    // 拒绝传送消息
    public static void denyMessage(@NotNull CommandSender executor, @NotNull CommandSender target, boolean isTphere){
        if (isTphere){
            MessageUtil.sendMessage(setLang(executor), lang.getString("you_deny"), target.getName());
            MessageUtil.sendMessage(setLang(target), lang.getString("target_deny"), executor.getName());
            return;
        }
        MessageUtil.sendMessage(setLang(target), lang.getString("you_deny"), executor.getName());
        MessageUtil.sendMessage(setLang(executor), lang.getString("target_deny"), target.getName());
    }

    // 传送倒计时消息
    public static void teleportCountdown(@NotNull CommandSender executor, @NotNull String target, long delay){
        if (target.equals("last_location") || target.equals("spawn_point")){
            MessageUtil.sendMessage(setLang(executor), lang.getString("teleport_countdown"),  lang.getString(target), String.valueOf(delay));
            return;
        }
        MessageUtil.sendMessage(setLang(executor), lang.getString("teleport_countdown"),  target, String.valueOf(delay));
    }

    // 因移动而取消传送消息
    public static void move(@NotNull CommandSender executor, @NotNull CommandSender target){
        MessageUtil.sendMessage(setLang(executor), lang.getString("move"));
        if (executor != target){
            MessageUtil.sendMessage(setLang(target), lang.getString("target_canceled_teleport"));
        }
    }

    // 因倒计时结束而取消传送消息
    public static void timeOverDeny(@NotNull Player executor, @NotNull Player target){
        MessageUtil.sendMessage(setLang(target), lang.getString("from_target_expired"), executor.getName());
        MessageUtil.sendMessage(setLang(executor), lang.getString("to_target_expired"), target.getName());
    }

    // 没有待接受的请求消息
    public static void notRequestAccept(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_request_accept"));
    }

    // 没有待拒绝的请求消息
    public static void notRequestDeny(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_request_deny"));
    }

    // 管理员将您传送至消息
    public static void adminTpYouToMessage(@NotNull CommandSender executor, @NotNull String targetName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("admin_tp_you_to_message"), targetName);
    }

    // tpall成功执行消息
    public static void tpAllCommandSuccess(@NotNull CommandSender executor, @NotNull String targetName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("tpall_command_success"), targetName);
    }

    // 没有在线玩家消息
    public static void notOnlinePlayerError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_online_player_error"));
    }

    // 找不到传送点消息
    public static void notWarpError(@NotNull CommandSender executor, @NotNull String warpName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_warp_error"), warpName);
    }

    // 已传送到传送点消息
    public static void tpToWarpMessage(@NotNull CommandSender executor, @NotNull String warpName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("tp_to_warp_message"), warpName);
    }

    // 传送点成功保存消息
    public static void setWarpSuccess(@NotNull CommandSender executor, @NotNull String warpName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("set_warp_success"), warpName);
    }

    // 传送点成功删除消息
    public static void delWarpSuccess(@NotNull CommandSender executor, @NotNull String warpName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("del_warp_success"), warpName);
    }

    // 找不到家消息
    public static void notHomeError(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_home_error"), homeName);
    }

    // 成功保存家消息
    public static void setHomeSuccess(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("set_home_success"), homeName);
    }

    // 成功删除家消息
    public static void delHomeSuccess(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("del_home_success"), homeName);
    }

    // 已传送到家消息
    public static void tpToHomeMessage(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("tp_to_home_message"), homeName);
    }

    // 拥有的家已达上限消息
    public static void homeAmountMaxError(@NotNull CommandSender executor, int maxHomeAmount){
        MessageUtil.sendMessage(setLang(executor), lang.getString("home_amount_max_error"), String.valueOf(maxHomeAmount));
    }

    // 不能使用关键字消息
    public static void notUseKeyWordError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_use_keyword_error"));
    }

    // 找不到上一次的位置消息
    public static void notLastLocationError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_last_location_error"));
    }

    // 未设置主城消息
    public static void notSetSpawnError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_set_spawn_error"));
    }

    // 返回主城成功消息
    public static void backSpawnSuccessMessage(@NotNull CommandSender executor, @NotNull String target){
        MessageUtil.sendMessage(setLang(executor), lang.getString("back_spawn_success_message"), lang.getString(target));
    }

    // 成功设置主城消息
    public static void setSpawnSuccess(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("set_spawn_success"), lang.getString("spawn_point"));
    }

    // 成功删除主城消息
    public static void delSpawnSuccess(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("del_spawn_success"), lang.getString("spawn_point"));
    }

    // 返回上一次的位置成功消息
    public static void backLastLocationSuccessMessage(@NotNull CommandSender executor, @NotNull String target){
        MessageUtil.sendMessage(setLang(executor), lang.getString("back_last_location_success_message"), lang.getString(target));
    }

    // 配置文件不存在消息
    public static void configNotFound(@NotNull CommandSender sender){
        MessageUtil.sendMessage(setLang(sender), lang.getString("config_not_found"));
    }
}
