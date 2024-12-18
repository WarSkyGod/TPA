package top.craft_hello.tpa.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.TPA;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class warpName implements TabCompleter {
    private static final FileConfiguration warpConfig = TPA.getPlugin(TPA.class).getWarpConfig();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            FileConfiguration langConfig = TPA.getPlugin(TPA.class).loadLangConfig(((Player) sender).getLocale());
            Set<String> warpNames = warpConfig.getKeys(false);
            List<String> warpNameList = new ArrayList<>(warpNames);
            if (warpNameList.isEmpty()) warpNameList.add(langConfig.getString("warp_name"));
            if (args.length == 0 || args.length == 1) return warpNameList;
            return new ArrayList<>();
        }
        return null;
    }
}
