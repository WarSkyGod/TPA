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


public class HomeName implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            String langStr = player.locale().getLanguage() + "_" + player.locale().getCountry();
            FileConfiguration lang = LoadingConfigFileUtil.getLang(langStr);
            FileConfiguration home = LoadingConfigFileUtil.getHome();
            Set<String> homeNameSet = home.getKeys(true);
            List<String> homeNameList = new ArrayList<>();
            for (String homeName : homeNameSet) {
                if (homeName.contains(".") && !homeName.contains("home_amount")) {
                    String homeName2 = homeName.substring(0, homeName.indexOf("."));
                    String homeName3 = homeName.substring(homeName2.length() + 1);
                    if (homeName2.equals(sender.getName())) {
                        homeNameList.add(homeName3);
                    }
                }
            }
            if (homeNameList.isEmpty()) homeNameList.add(lang.getString("home_name"));
            if (args.length == 0 || args.length == 1) return homeNameList;
            return new ArrayList<>();
        }
        return null;
    }
}
