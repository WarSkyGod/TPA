package top.craft_hello.tpa.tabcompletes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DenysTabCompletes implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0){
            return list;
        }

        if (args.length == 1){
            list.add("add");
            list.add("remove");
            list.add(" ");
            return list;
        }

        if (args.length == 2){
            String subCommand = args[args.length - 2];
            List<String> denys = LoadingConfigFileUtil.getDenysList(sender.getName());
            switch (subCommand) {
                case "add":
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                        list.add(player.getName());
                    }

                    for (String deny : denys) {
                        list.remove(Bukkit.getOfflinePlayer(UUID.fromString(deny)).getName());
                    }

                    list.remove(sender.getName());
                    return list;
                case "remove":
                    for (String deny : denys) {
                        list.add(Bukkit.getOfflinePlayer(UUID.fromString(deny)).getName());
                        return list;
                    }
            }
        }
        return list;
    }
}
