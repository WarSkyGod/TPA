package top.craft_hello.tpa.tabcompletes;

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
import java.util.Set;


public class HomeTabCompletes implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            FileConfiguration lang = LoadingConfigFileUtil.getLang(player);
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(sender.getName());
            Set<String> homeSet = playerData.getKeys(true);
            List<String> list = new ArrayList<>();
            for (String homeName : homeSet) {
                if (homeName.contains("homes.")) {
                    String homeName2 = homeName.substring(homeName.indexOf(".") + 1);
                    if (!homeName2.contains(".")) list.add(homeName2);
                }
            }
            if (list.isEmpty()) list.add(lang.getString("home_name"));
            return list;
        }
        return null;
    }
}
