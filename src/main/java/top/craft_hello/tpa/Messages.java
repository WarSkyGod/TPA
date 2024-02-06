package top.craft_hello.tpa;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    static FileConfiguration config = TPA.getPlugin(TPA.class).getConfig();
    static File langFile = new File(TPA.getPlugin(TPA.class).getDataFolder(), "lang/" + config.getString("lang") + ".yml");
    static FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);


    static String formatText(String text){
        return text == null ? error : text.replaceAll("&", "§");

    }

    static String formatTextVar(String text, List list){
        switch (text == null || list == null || list.isEmpty() ? 0 : list.size()){
            case 1:
                return list.get(0) instanceof String ? formatText(text).replaceAll("<target>", (String) list.get(0)) : formatText(text).replaceAll("<seconds>", Long.toString((Long) list.get(0)));
            case 2:
                return list.get(0) instanceof String && list.get(1) instanceof String ? formatText(text).replaceAll("<executor>", (String) list.get(0)).replaceAll("<target>", (String) list.get(1)) : formatText(text).replaceAll("<target>", (String) list.get(0)).replaceAll("<seconds>", Long.toString((Long) list.get(1)));
            case 3:
                return formatText(text).replaceAll("<executor>", (String) list.get(0)).replaceAll("<target>", (String) list.get(1)).replaceAll("<seconds>", Long.toString((Long) list.get(2)));
            default:
                return error;
        }
    }


    static String prefix = formatText(langConfig.getString("prefix") == null ? "§a§l> " : langConfig.getString("prefix"));
    static String consolePrefix = formatText(langConfig.getString("console_prefix") == null ? "§7[TPA] " : langConfig.getString("console_prefix"));
    static String error = formatText(langConfig.getString("error") == null ? prefix + "§c§l插件出错，请联系插件作者！" : prefix + langConfig.getString("error"));
    static String consoleError = formatText(langConfig.getString("error") == null ? consolePrefix + "§c§l插件出错，请联系插件作者！" : consolePrefix + langConfig.getString("error"));

    static void acceptOrDeny(Player target){
        TextComponent acceptMessage = new TextComponent(prefix + formatText(langConfig.getString("tpaccept")));
        acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(formatText(langConfig.getString("accept")))));
        TextComponent denyMessage = new TextComponent(formatText(langConfig.getString("tpdeny")));
        denyMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        denyMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(formatText(langConfig.getString("deny")))));
        target.spigot().sendMessage(acceptMessage, denyMessage);
    }
    static void move(Player executor, Player target){
        executor.sendMessage(prefix + formatText(langConfig.getString("move")));
        if (executor != target){
            target.sendMessage(prefix + formatText(langConfig.getString("target_canceled_teleport")));
        }
    }
    static void successSentRequest(Player executor, String target, long delay){
        List vars = new ArrayList();
        vars.add(target);
        vars.add(delay);
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("success_sent_request"), vars));
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("target_seconds"), vars));
    }

    static void offlineOrNull(Player executor, String target){
        List vars = new ArrayList();
        vars.add(target);
        executor.sendMessage( prefix + formatTextVar(langConfig.getString("offline_or_null"), vars));
    }

    static void resNull(Player executor, String resName){
        List vars = new ArrayList();
        vars.add(resName);
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("res_null"), vars));
    }

    static void requestYou(Player executor){
        executor.sendMessage(prefix + formatText(langConfig.getString("request_you")));
    }

    static void targetToYou(Player executor, Player target, long delay){
        List vars = new ArrayList();
        vars.add(executor.getName());
        vars.add(delay);
        target.sendMessage("\n" + prefix + formatTextVar(langConfig.getString("target_to_you"), vars));
        acceptOrDeny(target);
        Messages.successSentRequest(executor, target.getName(), delay);
    }

    static void youToTarget(Player executor, Player target, long delay){
        List vars = new ArrayList();
        vars.add(executor.getName());
        vars.add(delay);
        target.sendMessage("\n" + prefix + formatTextVar(langConfig.getString("you_to_target"), vars));
        acceptOrDeny(target);
        Messages.successSentRequest(executor, target.getName(), delay);
    }

    static void acceptMessage(Player executor, Player target, Long delay, boolean isTphere){
        List vars = new ArrayList();
        vars.add(target.getName());
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("you_accept"), vars));
        vars.clear();
        vars.add(executor.getName());
        target.sendMessage(prefix + formatTextVar(langConfig.getString("target_accept"), vars));
        if (isTphere){
            tpTimeMessage(executor, target.getName(), delay);
            return;
        }
        tpTimeMessage(target, executor.getName(), delay);
    }

    static void deny(Player executor, Player target){
        List vars = new ArrayList();
        vars.add(target.getName());
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("you_deny"), vars));
        vars.clear();
        vars.add(executor.getName());
        target.sendMessage(prefix + formatTextVar(langConfig.getString("target_deny"), vars));
    }

    static void tpTimeMessage(Player executor, String target, Long delay){
        List vars = new ArrayList();
        vars.add(target);
        vars.add(delay);
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("tp_time"), vars));
    }
    static void resTpMessage(Player executor, String resTpName){
        List vars = new ArrayList();
        vars.add(resTpName);
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("tp_to_res"), vars));
    }

    static void resTpSet(Player executor, String resTpName){
        List vars = new ArrayList();
        vars.add(resTpName);
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("set_res_loc"), vars));
    }

    static void timeOverDeny(Player executor, Player target){
        List vars = new ArrayList();
        vars.add(executor.getName());
        target.sendMessage(prefix + formatTextVar(langConfig.getString("from_target_expired"), vars));
        vars.clear();
        vars.add(target.getName());
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("to_target_expired"), vars));
    }

    static void noRequestAccept(Player executor){
        executor.sendMessage(prefix + formatText(langConfig.getString("no_request_accept")));
    }
    static void noRequestDeny(Player executor){
        executor.sendMessage(prefix + formatText(langConfig.getString("no_request_deny")));
    }

    static void requestLock(Player executor){
        executor.sendMessage(prefix + formatText(langConfig.getString("request_lock")));
    }

    static void commandError(Player executor, String label){
        List vars = new ArrayList();
        vars.add(label);
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("command_error"), vars));
    }

    static void resTpCommandError(Player executor, String label){
        List vars = new ArrayList();
        vars.add(label);
        executor.sendMessage(prefix + formatTextVar(langConfig.getString("restp_command_error"), vars));
    }

    public static void consoleUseError(CommandSender sender){
        sender.sendMessage(consolePrefix + formatText(langConfig.getString("console_use_error")));
    }

    static void pluginLoaded(CommandSender sender){
        List vars = new ArrayList();
        vars.add(sender.getServer().getName());
        sender.sendMessage(consolePrefix + formatTextVar(langConfig.getString("plugin_loaded"), vars));
    }
    static void forcePaperMode(CommandSender sender) {
        sender.sendMessage(consolePrefix + formatText(langConfig.getString("force_paper_mode")));
    }

    static void forceBukkitMode(CommandSender sender) {
        sender.sendMessage(consolePrefix + formatText(langConfig.getString("force_bukkit_mode")));
    }

    static void autoPaperMode(CommandSender sender) {
        sender.sendMessage(consolePrefix + formatText(langConfig.getString("auto_paper_mode")));
    }

    static void autoBukkitMode(CommandSender sender) {
        sender.sendMessage(consolePrefix + formatText(langConfig.getString("auto_bukkit_mode")));
    }

    static void pluginUnLoaded(CommandSender sender){
        sender.sendMessage(consolePrefix + formatText(langConfig.getString("plugin_unloaded")));
    }


}
