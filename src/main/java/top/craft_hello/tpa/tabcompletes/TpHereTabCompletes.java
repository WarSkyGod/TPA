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

public class TpHereTabCompletes implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        FileConfiguration lang = LoadingConfigFileUtil.getLang(sender);
        if (args.length == 0){
            return list;
        }

        if (sender instanceof Player && args.length == 1){
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!sender.equals(player)){
                    list.add(player.getName());
                }
            }
            if (list.isEmpty()) list.add(lang.getString("not_online_player"));
        }
        return list;
    }
}
