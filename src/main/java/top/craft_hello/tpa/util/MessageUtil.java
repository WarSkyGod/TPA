package top.craft_hello.tpa.util;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.TPA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MessageUtil {

    private static final FileConfiguration langConfig = TPA.getPlugin(TPA.class).getLangConfig();
    private static final String prefix = langConfig.getString("prefix") == null ? "§a&l> " : langConfig.getString("prefix");
    private static final String consolePrefix = langConfig.getString("console_prefix") == null ? "§7[TPA] " : langConfig.getString("console_prefix");
    private static final String error = langConfig.getString("error") == null ? "§c§l插件出错！" : langConfig.getString("error");

    public static String formatText(@NotNull String text){
        return text.replaceAll("&", "§");
    }

    public static String formatText(@NotNull String text, @NotNull List<String> vars){
        if (vars.size() == 0) {
            return formatText(text);
        }
        else if (vars.size() == 1) {
            return formatText(text).replaceAll("<target>", vars.get(0))
                    .replaceAll("<seconds>", vars.get(0));
        }
        else if (vars.size() == 2){
            return formatText(text).replaceAll("<target>", vars.get(0))
                    .replaceAll("<seconds>", vars.get(1));
        }
        return error;
    }

    public static void sendMessage(CommandSender sender, String text, String... vars){
        List<String> lists = new ArrayList<>(Arrays.asList(vars));
        sender.sendMessage(formatText((sender instanceof Player ? prefix : consolePrefix) + text, lists));
    }
}
