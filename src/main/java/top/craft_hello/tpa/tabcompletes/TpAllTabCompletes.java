package top.craft_hello.tpa.tabcompletes;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

import java.util.ArrayList;
import java.util.List;

public class TpAllTabCompletes implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0 || args.length == 1 && "spawn".equals(args[args.length - 1])){
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
            String subCommand = args[args.length - 2];
            switch (subCommand) {
                case "player":
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    return list;
                case "warp":
                    FileConfiguration lang = LoadingConfigFileUtil.getLang(sender);
                    FileConfiguration warp = LoadingConfigFileUtil.getWarp();
                    list = new ArrayList<>(warp.getKeys(false));
                    if (list.isEmpty()) list.add(lang.getString("warp_name"));
                    return list;
            }
        }
        return list;
    }
}
