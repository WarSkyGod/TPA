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


public class WarpTabCompletes implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2){
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                list.add(onlinePlayer.getName());
            }
            if (list.isEmpty()) list.add(LoadingConfigFileUtil.getLang(sender).getString("not_online_player"));
            return list;
        }
        FileConfiguration lang = LoadingConfigFileUtil.getLang(sender);
        FileConfiguration warp = LoadingConfigFileUtil.getWarp();
        List<String> list = new ArrayList<>(warp.getKeys(false));
        if (list.isEmpty()) list.add(lang.getString("warp_name"));
        return list;
    }
}
