package top.craft_hello.tpa.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.objects.WarpConfig;
import top.craft_hello.tpa.utils.LoadingConfigUtil;

import java.util.ArrayList;
import java.util.List;

public class TpAllTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0 || args.length == 1 && "spawn".equalsIgnoreCase(args[args.length - 1])){
            return list;
        }

        if (args.length == 1){
            list.add("player");
            list.add("spawn");
            list.add("warp");
            list.add(" ");
            return list;
        }

        if (args.length == 2){
            String subCommand = args[args.length - 2].toLowerCase();
            switch (subCommand) {
                case "player":
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    return list;
                case "warp":
                    LanguageConfig language = LanguageConfig.getLanguage(sender);
                    WarpConfig warp = LoadingConfigUtil.getWarpConfig();
                    list = warp.getWarpNameList();
                    if (list.isEmpty()) list.add(language.getMessage("warp_name"));
                    return list;
            }
        }
        return list;
    }
}
