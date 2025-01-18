package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.enums.ClickEventType;
import top.craft_hello.tpa.enums.HoverEventType;
import top.craft_hello.tpa.objects.JsonMessage;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Messages {
    // 这个随玩家语言动态变化
    private static FileConfiguration lang = LoadingConfigFileUtil.getLang();

    // 设置为指定语言
    private static void setLang(@NotNull FileConfiguration lang){
        Messages.lang = lang;
    }

    // 自动判断语言
    private static CommandSender setLang(@NotNull CommandSender executor){
        setLang(LoadingConfigFileUtil.getLang(executor));
        return executor;
    }

    // 配置文件迁移消息
    public static void configVersionUpdate(@NotNull CommandSender sender){
        MessageUtil.sendMessage(setLang(sender), lang.getString("config_version_update"), sender.getServer().getName());
    }

    // 配置文件迁移成功消息
    public static void configVersionUpdateSuccess(@NotNull CommandSender sender){
        MessageUtil.sendMessage(setLang(sender), lang.getString("config_version_update_success"), sender.getServer().getName());
    }

    // 发现更新消息
    public static void pluginUpdateMessage(@NotNull CommandSender executor, String latestVersion){
        MessageUtil.sendMessage(setLang(executor), lang.getString("plugin_update_message"), latestVersion);
    }

    // 插件加载消息
    public static void pluginLoaded(@NotNull String version){
        MessageUtil.sendMessage(setLang(Bukkit.getConsoleSender()), lang.getString("plugin_loaded"), version);
    }

    // 插件卸载消息
    public static void pluginUnLoaded(){
        MessageUtil.sendMessage(setLang(Bukkit.getConsoleSender()), lang.getString("plugin_unloaded"));
    }

    // 配置文件重载消息
    public static void configReloaded(@NotNull CommandSender executor){
        // 向服务器后台发送重载消息
        MessageUtil.sendMessage(setLang(Bukkit.getConsoleSender()), lang.getString("config_reloaded"));
        // 如果是玩家执行，则也向该玩家发送重载消息
        if(executor instanceof Player) {
            MessageUtil.sendMessage(setLang(executor), lang.getString("config_reloaded"));
        }
    }

    // 插件错误消息
    public static void pluginError(@NotNull CommandSender sender, @NotNull String message){
        // 向服务器后台发送错误消息
        MessageUtil.sendMessage(setLang(Bukkit.getConsoleSender()), lang.getString("plugin_error") + message);
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

    // tpall命令错误消息
    public static void tpAllCommandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("tpall_command_error"), label);
    }


    // warp命令错误消息
    public static void warpCommandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("warp_command_error"), label);
    }

    // home命令错误消息
    public static void homeCommandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("home_command_error"), label);
    }

    // setlang命令错误消息
    public static void setLangCommandError(@NotNull CommandSender executor, @NotNull String label){
        MessageUtil.sendMessage(setLang(executor), lang.getString("setlang_command_error"), label);
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
        acceptOrDeny((Player) target, executor.getName());
        successSentRequest(setLang(executor), target.getName(), delay);
    }

    // 请求对方传送到你的位置消息
    public static void requestTargetTeleportToHere(@NotNull CommandSender executor, @NotNull CommandSender target, long delay){
        MessageUtil.sendMessage(setLang(target), lang.getString("request_target_teleport_to_here"), executor.getName(), String.valueOf(delay));
        acceptOrDeny((Player) target, executor.getName());
        successSentRequest(setLang(executor), target.getName(), delay);
    }

    // 列表消息
    public static void listMessage(@NotNull Player executor, List<String> list, String command, boolean teleportButton, boolean settingButton, boolean settingDefaultHomeButton, boolean deleteButton, boolean removeDenysButton){
        JsonMessage jsonMessage;
        String teleportCommand = "/" + command + " ";
        String settingCommand = "/set" + command + " ";
        String settingDefaultHomeCommand = "/setdefault" + command + " ";
        String deleteCommand = "/del" + command + " ";
        String removeDenysCommand = "/" + command + " remove ";
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        String defaultHome = playerData.getString("default_home");
        for (String targetName : list) {
            jsonMessage = new JsonMessage(executor, Objects.requireNonNull(lang.getString("prefix")));
            jsonMessage.addText("&6&l" + targetName);
            PlayerSchedulerUtil.syncDispatchCommand(jsonMessage.toString());

            jsonMessage = new JsonMessage(executor, Objects.requireNonNull(lang.getString("prefix")));
            if (teleportButton){
                jsonMessage.addText(lang.getString("teleport_button"))
                        .addInsertion(teleportCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, teleportCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, lang.getString("click_teleport"));
            }

            if (settingButton){
                jsonMessage.addText(lang.getString("setting_button"))
                        .addInsertion(settingCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, settingCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, lang.getString("click_setting"));
            }

            if (settingDefaultHomeButton && command.equals("home") && (ErrorCheckUtil.isNull(defaultHome) || !defaultHome.equals(targetName))){
                jsonMessage.addText(lang.getString("setting_default_home_button"))
                        .addInsertion(settingDefaultHomeCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, settingDefaultHomeCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, lang.getString("click_setting_default_home"));
            }

            if (deleteButton){
                jsonMessage.addText(lang.getString("delete_button"))
                        .addInsertion(deleteCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, deleteCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, lang.getString("click_delete"));
            }

            if (removeDenysButton){
                jsonMessage.addText(lang.getString("remove_denys_button"))
                        .addInsertion(removeDenysCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, removeDenysCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, lang.getString("click_remove_denys"));
            }
            PlayerSchedulerUtil.syncDispatchCommand(jsonMessage.toString());
        }
    }

    // 当前的黑名单列表消息
    public static void denysMessage(@NotNull CommandSender executor, List<String> denys){
        setLang(executor);
        if (denys.isEmpty()){
            MessageUtil.sendMessage(setLang(executor), lang.getString("not_denys_error"));
            return;
        }
        List<String> denyNames = new ArrayList<>();
        for (String deny : denys) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(deny));
            denyNames.add(offlinePlayer.getName());
        }

        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        boolean removeDenysButton = (config.getBoolean("enable_command.tpa") || config.getBoolean("enable_command.tphere")) && (!config.getBoolean("enable_permission.denys") || executor.hasPermission("tpa.denys") || executor.hasPermission("tpa.admin"));
        MessageUtil.sendMessage(setLang(executor), lang.getString("denys"));
        listMessage((Player) executor, denyNames, "denys", false, false, false, false, removeDenysButton);
    }

    // 当前可用的传送点消息
    public static void warpListMessage(@NotNull CommandSender executor, List<String> warpList){
        setLang(executor);
        if (warpList.isEmpty()){
            MessageUtil.sendMessage(setLang(executor), lang.getString("not_warps_error"));
            return;
        }

        if (!(executor instanceof Player)){
            String warps = "";
            int count = 0;
            for (String warp : warpList) {
                if (++count > 1){
                    warps = warps + "&e&l, ";
                }
                warps = warps + "&a&l" + warp;
            }

            MessageUtil.sendMessage(setLang(executor), lang.getString("warps"));
            MessageUtil.sendMessage(setLang(executor), warps);
            return;
        }

        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        boolean teleportButton = !config.getBoolean("enable_permission.warp") || executor.hasPermission("tpa.setwarp") || executor.hasPermission("tpa.admin");
        boolean settingButton = executor.hasPermission("tpa.setwarp") || executor.hasPermission("tpa.admin");
        boolean deleteButton = executor.hasPermission("tpa.delwarp") || executor.hasPermission("tpa.admin");
        MessageUtil.sendMessage(setLang(executor), lang.getString("warps"));
        listMessage((Player) executor, warpList, "warp", teleportButton, settingButton, false, deleteButton, false);
    }

    // 当前可用的家消息
    public static void homeListMessage(@NotNull CommandSender executor, List<String> homeList){
        setLang(executor);
        if (homeList.isEmpty()){
            MessageUtil.sendMessage(setLang(executor), lang.getString("not_homes_error"));
            return;
        }

        FileConfiguration config = LoadingConfigFileUtil.getConfig();

        boolean teleportButton = !config.getBoolean("enable_permission.home") || executor.hasPermission("tpa.home") || executor.hasPermission("tpa.admin");
        MessageUtil.sendMessage(setLang(executor), lang.getString("homes"));
        listMessage((Player) executor, homeList, "home", teleportButton, teleportButton, teleportButton, teleportButton, false);
    }

    // 接受或拒绝消息
    public static void acceptOrDeny(@NotNull Player target, @NotNull String executorName){
        setLang(target);
        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        JsonMessage jsonMessage = new JsonMessage(target, Objects.requireNonNull(lang.getString("prefix")))
                .addText(Objects.requireNonNull(lang.getString("accept_button")))
                .addInsertion("/tpaccept")
                .addClickEvent(ClickEventType.RUN_COMMAND, "/tpaccept")
                .addHoverEvent(HoverEventType.SHOW_TEXT, Objects.requireNonNull(lang.getString("click_accept")))
                .addText()
                .addText()
                .addText(Objects.requireNonNull(lang.getString("deny_button")))
                .addInsertion("/tpdeny")
                .addClickEvent(ClickEventType.RUN_COMMAND, "/tpdeny")
                .addHoverEvent(HoverEventType.SHOW_TEXT, Objects.requireNonNull(lang.getString("click_deny")));

        if ((config.getBoolean("enable_command.tpa") || config.getBoolean("enable_command.tphere")) && (!config.getBoolean("enable_permission.denys") || (target.hasPermission("tpa.denys") || target.hasPermission("tpa.admin")))) {
            jsonMessage.addText()
                    .addText()
                    .addText(Objects.requireNonNull(lang.getString("deny_and_add_denys_button")))
                    .addInsertion("/denys add " + executorName)
                    .addClickEvent(ClickEventType.RUN_COMMAND, "/denys add " + executorName)
                    .addHoverEvent(HoverEventType.SHOW_TEXT, Objects.requireNonNull(lang.getString("click_deny_and_add_denys")));
        }
        PlayerSchedulerUtil.syncDispatchCommand(jsonMessage.toString());
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
            teleportCountdown(executor, target.getName(), delay);
            return;
        }
        MessageUtil.sendMessage(setLang(target), lang.getString("you_accept"), executor.getName());
        MessageUtil.sendMessage(setLang(executor), lang.getString("target_accept"), target.getName());
        teleportCountdown(executor, target.getName(), delay);
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

    // title 样式的传送倒计时消息
    public static void titleCountdownMessage(@NotNull Player executor, List<String> args){
        setLang(executor);
        String target = args.get(args.size() - 2);
        String delay = args.get(args.size() - 1);
        if (target.equals("last_location") || target.equals("spawn_point")){
            target = lang.getString(target);
            args.clear();
            args.add(target);
            args.add(delay);
        }
        String title = MessageUtil.formatText(lang.getString("teleport_countdown"), args);
        String subTitle = MessageUtil.formatText(lang.getString("move_cancel_message"));
        executor.sendTitle(title, subTitle);

    }

    // title 样式的传送倒计时结束消息
    public static void titleCountdownOverMessage(@NotNull Player executor, List<String> args){
        String title = MessageUtil.formatText(lang.getString("you_teleported_to_message"), args);
        executor.sendTitle(title, "");
    }


    // 传送倒计时消息
    public static void teleportCountdown(@NotNull CommandSender executor, @NotNull String target, long delay){

        if (target.equals("last_location") || target.equals("spawn_point")){
            MessageUtil.sendMessage(setLang(executor), lang.getString("teleport_countdown"),  lang.getString(target), String.valueOf(delay));
            MessageUtil.sendMessage(setLang(executor), lang.getString("move_cancel_message"));
            return;
        }
        MessageUtil.sendMessage(setLang(executor), lang.getString("teleport_countdown"), target, String.valueOf(delay));
        MessageUtil.sendMessage(setLang(executor), lang.getString("move_cancel_message"));
    }

    // 因移动而取消传送消息
    public static void move(@NotNull CommandSender executor, @NotNull CommandSender target){
        MessageUtil.sendMessage(setLang(executor), lang.getString("move_canceled_error"));
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

    // 管理员传送所有玩家到您的位置消息
    public static void adminTpAllPlayerToYouMessage(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("admin_tp_all_player_to_you_message"));
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

    // 已是默认的家消息
    public static void defaultHomeError(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("default_home_error"), homeName);
    }

    // 不能添加自己
    public static void youDenysYouError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("you_denys_you_error"));
    }

    // 对方已将您拉黑消息
    public static void isDenysError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("is_denys_error"));
    }

    // 对方已在拒绝请求列表消息
    public static void targetIsDenysError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("target_is_denys_error"));
    }

    // 对方不在拒绝请求列表消息
    public static void targetIsNoDenysError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("target_is_no_denys_error"));
    }

    // 成功添加进拒绝请求列表消息
    public static void addDenysSuccess(@NotNull CommandSender executor, @NotNull String targetName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("add_denys_success"), targetName);
    }

    // 成功移出拒绝请求列表消息
    public static void removeDenySuccess(@NotNull CommandSender executor, @NotNull String targetName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("remove_denys_success"), targetName);
    }

    // 没有默认的家消息
    public static void notDefaultHomeError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_default_home_error"));
    }

    // 成功保存家消息
    public static void setHomeSuccess(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("set_home_success"), homeName);
    }

    // 成功设置默认家消息
    public static void setDefaultHomeSuccess(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("set_default_home_success"), homeName);
    }

    // 成功删除家消息
    public static void delHomeSuccess(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("del_home_success"), homeName);
    }

    // 已传送到家消息
    public static void tpToHomeMessage(@NotNull CommandSender executor, @NotNull String homeName){
        MessageUtil.sendMessage(setLang(executor), lang.getString("you_teleported_to_message"), homeName);
    }

    // 拥有的家已达上限消息
    public static void homeAmountMaxError(@NotNull CommandSender executor, int maxHomeAmount){
        MessageUtil.sendMessage(setLang(executor), lang.getString("home_amount_max_error"), String.valueOf(maxHomeAmount));
    }

    // 找不到上一次的位置消息
    public static void notLastLocationError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_last_location_error"));
    }

    // 找不到最后下线地点消息
    public static void notLogoutLocationError(@NotNull CommandSender executor){
        MessageUtil.sendMessage(setLang(executor), lang.getString("not_logout_location_error"));
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

    // 传送到最后下线地点成功消息
    public static void tpLogoutCommandSuccess(@NotNull CommandSender executor, @NotNull String target){
        MessageUtil.sendMessage(setLang(executor), lang.getString("tplogout_command_success"), target);
    }

    // 设置语言成功消息
    public static void setLangCommandSuccess(@NotNull CommandSender executor, @NotNull String target){
        MessageUtil.sendMessage(setLang(executor), lang.getString("setlang_command_success"), target);
    }

    // 配置文件不存在消息
    public static void configNotFound(@NotNull CommandSender sender){
        MessageUtil.sendMessage(setLang(sender), lang.getString("config_not_found"));
    }
}
