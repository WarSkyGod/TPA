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
import java.util.Collection;
import java.util.List;

public class OnlinePlayers implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){

            FileConfiguration lang = LoadingConfigFileUtil.getLang(((Player) sender).locale().getLanguage() + "_" + ((Player) sender).locale().getCountry());
            List<String> playerList = new ArrayList<>();
            Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            onlinePlayers.remove(sender);
            for (Player onlinePlayer : onlinePlayers) {
                playerList.add(onlinePlayer.getName());
            }
            if (playerList.isEmpty()) playerList.add(lang.getString("not_online_player"));
            if (args.length == 0 || args.length == 1) return playerList;
            return new ArrayList<>();
        }
        return null;
    }
}
