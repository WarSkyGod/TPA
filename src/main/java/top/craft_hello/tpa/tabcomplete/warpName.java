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

public class warpName implements TabCompleter {
    private static final FileConfiguration langConfig = TPA.getPlugin(TPA.class).getLangConfig();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            List<String> warpName = new ArrayList<>();
            warpName.add(langConfig.getString("warp_name"));
            if (args.length == 0 || args.length == 1) return warpName;
            return new ArrayList<>();
        }
        return null;
    }
}
