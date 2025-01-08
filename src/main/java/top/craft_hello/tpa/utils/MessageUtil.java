package top.craft_hello.tpa.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageUtil {
    private static FileConfiguration lang = LoadingConfigFileUtil.getLang();
    private static String prefix = lang.getString("prefix") == null ? "§a&l> " : lang.getString("prefix");
    private static String consolePrefix = lang.getString("console_prefix") == null ? "§a&l> " : lang.getString("console_prefix");
    private static String error = lang.getString("error") == null ? "§c§l插件出错！" : lang.getString("error");

    // 将字符串中的&自动替换为§
    public static String formatText(@NotNull String text){
        return text.replaceAll("&", "§");
    }

    // 用于自定义变量的替换
    public static String formatText(@NotNull String text, @NotNull List<String> vars){
        if (vars.size() == 0) {
            return formatText(text);
        }
        else if (vars.size() == 1) {
            return formatText(text).replaceAll("<target>", vars.get(0))
                    .replaceAll("<max_home_amount>", vars.get(0))
                    .replaceAll("<seconds>", vars.get(0));
        }
        else if (vars.size() == 2){
            return formatText(text).replaceAll("<target>", vars.get(0))
                    .replaceAll("<seconds>", vars.get(1));
        }
        return formatText(error);
    }

    // 发送消息
    public static void sendMessage(CommandSender sender, String text, String... vars){
        List<String> lists = new ArrayList<>(Arrays.asList(vars));
        sender.sendMessage(formatText(((sender instanceof Player) ? prefix : consolePrefix) + text, lists));
    }
}
