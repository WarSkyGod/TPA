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


public class WarpName implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        FileConfiguration lang = LoadingConfigFileUtil.getLang(((Player) sender).locale().getLanguage() + "_" + ((Player) sender).locale().getCountry());
        FileConfiguration warp = LoadingConfigFileUtil.getWarp();
        List<String> warpNameList = new ArrayList<>(warp.getKeys(false));
        if (warpNameList.isEmpty()) warpNameList.add(lang.getString("warp_name"));
        if (args.length == 0 || args.length == 1) return warpNameList;
        return new ArrayList<>();
    }
}
