package top.craft_hello.tpa.util;

import cn.handyplus.lib.adapter.HandySchedulerUtil;

import org.bukkit.Bukkit;
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

    protected static void runTask(@NotNull Runnable task) {
        Bukkit.getScheduler().runTask(TPA.getPlugin(TPA.class), task);
    }

    public static void syncPerformOpCommand(@NotNull Player player, @NotNull String command) {
        if (HandySchedulerUtil.isFolia()) {
            player.getScheduler().run(TPA.getPlugin(TPA.class), (a) -> opCommand(player, command), () -> {});
        } else {
            runTask(() -> opCommand(player, command));
        }
    }

    public static void command(@NotNull Player target, @NotNull String command) {
        target.performCommand(command);
    }

    public static void opCommand(@NotNull Player target, @NotNull String command) {
        boolean op = target.isOp();

        try {
            if (!op) {
                target.setOp(true);
            }
            command(target, command);
        } finally {
            target.setOp(op);
        }
    }

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
