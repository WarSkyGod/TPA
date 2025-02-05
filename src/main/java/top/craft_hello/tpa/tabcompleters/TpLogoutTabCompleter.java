package top.craft_hello.tpa.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TpLogoutTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        if (args.length != 1) return list;

        if (sender instanceof Player){
            for (Player player : Bukkit.getOnlinePlayers()) {
                list.add(player.getName());
            }
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                list.add(player.getName());
            }
        }
        return list;
    }
}
