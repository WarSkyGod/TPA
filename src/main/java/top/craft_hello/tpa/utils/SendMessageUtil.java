package top.craft_hello.tpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.craft_hello.tpa.enums.ClickEventType;
import top.craft_hello.tpa.enums.CommandType;
import top.craft_hello.tpa.enums.HoverEventType;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.objects.Config;
import top.craft_hello.tpa.objects.JsonMessage;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.objects.PlayerDataConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;


public class SendMessageUtil {

    // 发送消息
    public static void sendMessage(CommandSender sender, String message) {
        if (!isNull(sender)) sender.sendMessage(message);
    }

    // 配置文件迁移消息
    public static void configVersionUpdate(CommandSender sender) {
        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "system.config_migrated"));
    }

    // 配置文件迁移成功消息
    public static void configVersionUpdateSuccess(CommandSender sender) {
        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "system.config_migrated_success"));
    }

    // 正在检查更新消息
    public static void checkUpdate(CommandSender sender) {
        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "update.checking"));
    }

    // 发现更新消息
    public static void pluginUpdateMessage(CommandSender sender, String latestVersion) {
        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "update.available", latestVersion));
    }

    // 当前已是最新版本消息
    public static void pluginLatestVersion(CommandSender sender) {
        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "update.latest"));
    }

    // 插件加载消息
    public static void pluginLoaded(CommandSender sender, String pluginVersion) {
        sendMessage(sender, getLanguage().getFormatPrefixMessage("system.plugin_loaded", pluginVersion));
    }

    // 插件卸载消息
    public static void pluginUnLoaded(CommandSender sender) {
        sendMessage(sender, getLanguage().getFormatPrefixMessage("system.plugin_unloaded"));
    }

    // 配置文件重载消息
    public static void configReloaded(CommandSender sender) {
        // 向服务器后台发送重载消息
        sendMessage(Bukkit.getConsoleSender(), getLanguage().getFormatPrefixMessage("system.config_reloaded"));
        // 如果是玩家执行，则也向该玩家发送重载消息
        if (sender instanceof Player) sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "system.config_reloaded"));
    }

    // 请求传送到对方的位置消息
    public static void requestTeleportToTarget(Player executor, Player target, String delay) {
        String executorName = isNull(executor) || !executor.isOnline() ? "null" : executor.getName();
        String targetName = isNull(target) || !target.isOnline() ? "null" : target.getName();
        sendMessage(target, getLanguage(target).getFormatPrefixMessage(target, "request.to_here", executorName, delay));
        acceptOrDeny(target, executorName);
        successSentRequest(executor, targetName, delay);
    }

    // 请求对方传送到你的位置消息
    public static void requestTargetTeleportToHere(Player executor, Player target, String delay) {
        String executorName = isNull(executor) || !executor.isOnline() ? "null" : executor.getName();
        String targetName = isNull(target) || !target.isOnline() ? "null" : target.getName();
        sendMessage(target, getLanguage(target).getFormatPrefixMessage(target, "request.to_target", executorName, delay));
        acceptOrDeny(target, executorName);
        successSentRequest(executor, targetName, delay);
    }

    // 列表消息
    public static void listMessage(Player executor, List<String> targetNames, String command, boolean teleportButton, boolean settingButton, boolean settingDefaultHomeButton, boolean deleteButton, boolean removeDenysButton) {
        JsonMessage jsonMessage;
        String teleportCommand = "/" + command + " ";
        String settingCommand = "/set" + command + " ";
        String settingDefaultHomeCommand = "/setdefault" + command + " ";
        String deleteCommand = "/del" + command + " ";
        String removeDenysCommand = "/" + command + " remove ";
        LanguageConfig language = getLanguage(executor);
        for (String targetName : targetNames) {
            jsonMessage = new JsonMessage(executor, language.getPrefix(executor));
            jsonMessage.addText("&6&l" + targetName);
            jsonMessage.sendMessage();

            jsonMessage = new JsonMessage(executor, language.getPrefix(executor));
            if (teleportButton){
                jsonMessage.addText(language.getMessage("teleport_button"))
                        .addInsertion(teleportCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, teleportCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("teleport_click_hint"));
            }

            if (settingButton){
                jsonMessage.addText(language.getMessage("location_set_button"))
                        .addInsertion(settingCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, settingCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("set_location_hint"));
            }

            if (settingDefaultHomeButton && command.equals("home") && !PlayerDataConfig.getPlayerData(executor).equalsDefaultHomeName(targetName)){
                jsonMessage.addText(language.getMessage("default_home_set_button"))
                        .addInsertion(settingDefaultHomeCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, settingDefaultHomeCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("set_default_home_hint"));
            }

            if (deleteButton){
                jsonMessage.addText(language.getMessage("delete_button"))
                        .addInsertion(deleteCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, deleteCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("delete_click_hint"));
            }

            if (removeDenysButton){
                jsonMessage.addText(language.getMessage("blacklist.remove_button"))
                        .addInsertion(removeDenysCommand + targetName)
                        .addClickEvent(ClickEventType.RUN_COMMAND, removeDenysCommand + targetName)
                        .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("blacklist.remove_hint"));
            }
            jsonMessage.sendMessage();
        }
    }

    // 当前的黑名单列表消息
    public static void denysMessage(Player executor, List<String> denyList) {
        List<String> denyNameList = new ArrayList<>();
        for (String playerUUID : denyList) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            denyNameList.add(offlinePlayer.getName());
        }

        Config config = LoadingConfigUtil.getConfig();
        boolean removeDenysButton = config.isEnableCommand(CommandType.TPA, CommandType.TP_HERE) && config.hasPermission(executor, PermissionType.DENYS);
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "blacklist.list_header"));
        listMessage(executor, denyNameList, "denys", false, false, false, false, removeDenysButton);
    }

    // 当前可用的传送点消息
    public static void warpListMessage(CommandSender sender, List<String> warpList) {
        if (warpList.isEmpty()){
            sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "error.no_warps_set"));
            return;
        }

        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "warp.list_header"));
        if (!(sender instanceof Player)){
            StringBuilder warps = new StringBuilder();
            int count = 0;
            for (String warp : warpList) {
                if (++count > 1){
                    warps.append("§e§l, ");
                }
                warps.append("§a§l").append(warp);
            }
            sendMessage(sender, getLanguage().getPrefix() + warps);
            return;
        }

        Config config = LoadingConfigUtil.getConfig();
        Player executor = (Player) sender;
        boolean settingButton = config.hasPermission(executor, PermissionType.SET_WARP);
        boolean deleteButton = config.hasPermission(executor, PermissionType.DEL_WARP);
        listMessage(executor, warpList, "warp", true, settingButton, false, deleteButton, false);
    }

    // 当前可用的家消息
    public static void homeListMessage(Player executor, List<String> homeNameList) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "home.list_header"));
        listMessage(executor, homeNameList, "home", true, true, true, true, false);
    }

    // 接受或拒绝消息
    public static void acceptOrDeny(Player target, String executorName) {
        Config config = LoadingConfigUtil.getConfig();
        LanguageConfig language = getLanguage(target);
        JsonMessage jsonMessage = new JsonMessage(target, language.getPrefix(target))
                .addText(language.getMessage("accept_button"))
                .addInsertion("/tpaccept")
                .addClickEvent(ClickEventType.RUN_COMMAND, "/tpaccept")
                .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("accept_click_hint"))
                .addText()
                .addText()
                .addText(language.getMessage("deny_button"))
                .addInsertion("/tpdeny")
                .addClickEvent(ClickEventType.RUN_COMMAND, "/tpdeny")
                .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("deny_click_hint"));

        if ((config.isEnableCommand(CommandType.TPA) || config.isEnableCommand(CommandType.TP_HERE)) && config.hasPermission(target, PermissionType.DENYS)){
            jsonMessage.addText()
                    .addText()
                    .addText(language.getMessage("blacklist.add_button"))
                    .addInsertion("/denys add " + executorName)
                    .addClickEvent(ClickEventType.RUN_COMMAND, "/denys add " + executorName)
                    .addHoverEvent(HoverEventType.SHOW_TEXT, language.getFormatMessage("blacklist.add_hint"));
        }
        jsonMessage.sendMessage();
    }

    // 成功发送请求消息
    public static void successSentRequest(Player executor, String target, String delay) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "request.sent_success", target));
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "request.timeout_notice", delay));
    }

    // 接受传送消息
    public static void acceptMessage(Player executor, Player target) {
        String executorName = isNull(executor) ? "null" : executor.getName();
        String targetName = isNull(target) ? "null" : target.getName();
        sendMessage(target, getLanguage(target).getFormatPrefixMessage(target, "teleport.accept.self", executorName));
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.accept.target", targetName));
    }

    // 拒绝传送消息
    public static void denyMessage(Player executor, Player target) {
        String executorName = isNull(executor) ? "null" : executor.getName();
        String targetName = isNull(target) ? "null" : target.getName();
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.deny.self", targetName));
        sendMessage(target, getLanguage(target).getFormatPrefixMessage(target, "teleport.deny.target", executorName));
    }

    // title 样式的传送倒计时消息
    public static void titleCountdownMessage(Player executor, String... vars) {
        String target = vars[vars.length - 2];
        LanguageConfig language = getLanguage(executor);
        if ("last_location".equals(target) || "rtp_name".equals(target) || "spawn_name".equals(target)) vars[vars.length - 2] = language.getMessage(target);
        String title = language.getFormatMessage("teleport.countdown", vars);
        String subTitle = language.getFormatMessage("teleport.cancel_on_move");
        executor.sendTitle(title, subTitle);
    }

    // title 样式的传送倒计时结束消息
    public static void titleCountdownOverMessage(Player executor, String target) {
        LanguageConfig language = getLanguage(executor);
        if ("last_location".equals(target) || "rtp_name".equals(target) || "spawn_name".equals(target)) target = language.getMessage(target);
        Bukkit.getConsoleSender().sendMessage(target);
        String title = language.getFormatMessage("teleport.generic_success", target);
        try {
            executor.sendTitle(title, "");
        } catch (Throwable ignored){}
    }

    // 传送倒计时消息
    public static void teleportCountdown(Player executor, String target, String delay) {
        LanguageConfig language = getLanguage(executor);
        if ("last_location".equals(target) || "rtp_name".equals(target) || "spawn_name".equals(target)) target = language.getMessage(target);
        sendMessage(executor, language.getFormatPrefixMessage(executor, "teleport.countdown", target, delay));
        sendMessage(executor, language.getFormatPrefixMessage(executor, "teleport.cancel_on_move"));
    }

    // title 样式的正在为您生成随机传送点消息
    public static void titleGenerateRandomLocationMessage(Player executor) {
        String title = getLanguage(executor).getFormatMessage("rtp.generating");
        executor.sendTitle(title, "");
    }

    // 正在为您生成随机传送点消息
    public static void generateRandomLocationMessage(Player executor) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "rtp.generating"));
    }

    // 您已传送到消息
    public static void youTeleportedToMessage(Player executor, String target) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.generic_success", target));
    }

    // 因移动而取消传送消息
    public static void move(Player executor, Player target) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.canceled.self"));
        if (executor != target) sendMessage(target, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.canceled.target"));
    }

    // 因倒计时结束而取消传送消息
    public static void timeOverDeny(Player executor, Player target) {
        String executorName = isNull(executor) ? "null" : executor.getName();
        String targetName = isNull(target) ? "null" : target.getName();
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "request.expired_to", targetName));
        sendMessage(target, getLanguage(executor).getFormatPrefixMessage(executor, "request.expired_from", executorName));
    }

    // 管理员将您传送至消息
    public static void adminTpYouToMessage(Player executor, String targetName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.tpall.to_target", targetName));
    }

    // 管理员传送所有玩家到您的位置消息
    public static void adminTpAllPlayerToYouMessage(Player executor) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.tpall.to_self"));
    }

    // tpall成功执行消息
    public static void tpAllCommandSuccess(CommandSender sender, String targetName) {
        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "teleport.tpall.success", targetName));
    }

    // 已传送到传送点消息
    public static void tpToWarpMessage(Player executor, String warpName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "warp.teleport_success", warpName));
    }

    // 传送点成功保存消息
    public static void setWarpSuccess(Player executor, String warpName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "warp.set_success", warpName));
    }

    // 传送点成功删除消息
    public static void delWarpSuccess(CommandSender sender, String warpName) {
        sendMessage(sender, getLanguage(sender).getFormatPrefixMessage(sender, "warp.delete_success", warpName));
    }

    // 成功添加进黑名单消息
    public static void addDenysSuccess(Player executor, String targetName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "blacklist.add_success", targetName));
    }

    // 成功移出黑名单消息
    public static void removeDenySuccess(Player executor, String targetName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "blacklist.remove_success", targetName));
    }

    // 成功保存家消息
    public static void setHomeSuccess(Player executor, String homeName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "home.set_success", homeName));
    }

    // 成功设置默认家消息
    public static void setDefaultHomeSuccess(Player executor, String homeName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "home.default_set_success", homeName));
    }

    // 成功删除家消息
    public static void delHomeSuccess(Player executor, String homeName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "home.delete_success", homeName));
    }

    // 已传送到家消息
    public static void tpToHomeMessage(Player executor, String homeName) {
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "home.teleport_success", homeName));
    }

    // 返回主城成功消息
    public static void backSpawnSuccessMessage(Player executor) {
        LanguageConfig language = getLanguage(executor);
        sendMessage(executor, language.getFormatPrefixMessage(executor, "spawn.teleport_success", language.getMessage("spawn_name")));
    }

    // 成功设置主城消息
    public static void setSpawnSuccess(Player executor) {
        LanguageConfig language = getLanguage(executor);
        sendMessage(executor, language.getFormatPrefixMessage(executor, "spawn.set_success"));
    }

    // 成功删除主城消息
    public static void delSpawnSuccess(CommandSender sender) {
        LanguageConfig language = getLanguage(sender);
        sendMessage(sender, language.getFormatPrefixMessage(sender, "spawn.delete_success"));
    }

    // 传送到随机传送点成功消息
    public static void rtpSuccessMessage(Player executor) {
        LanguageConfig language = getLanguage(executor);
        sendMessage(executor, language.getFormatPrefixMessage(executor, "rtp.success"));
    }

    // 返回上一次的位置成功消息
    public static void backLastLocationSuccessMessage(Player executor) {
        LanguageConfig language = getLanguage(executor);
        sendMessage(executor, language.getFormatPrefixMessage(executor, "back.teleport_success", language.getMessage("last_location")));
    }

    // 传送到最后下线地点成功消息
    public static void tpLogoutCommandSuccess(Player executor, String targetName){
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "teleport.logout_location", targetName));
    }

    // 设置语言成功消息
    public static void setLangCommandSuccess(Player executor, String target){
        sendMessage(executor, getLanguage(executor).getFormatPrefixMessage(executor, "lang.set_success", target));
    }
}
