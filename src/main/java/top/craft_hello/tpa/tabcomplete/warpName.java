package top.craft_hello.tpa.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.TPA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;

public class warpName implements TabCompleter {
    private static final FileConfiguration langConfig = TPA.getPlugin(TPA.class).getLangConfig();
    private static final FileConfiguration warpConfig = TPA.getPlugin(TPA.class).getWarpConfig();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player){
            try {
                warpConfig.load(TPA.getPlugin(TPA.class).getWarpFile());
            } catch (IOException | InvalidConfigurationException e) {
                Messages.configNotFound(getServer().getConsoleSender());
            }
            Set<String> warpNames = warpConfig.getKeys(false);
            List<String> warpNameList = new ArrayList<>(warpNames);
            if (warpNameList.isEmpty()) warpNameList.add(langConfig.getString("warp_name"));
            if (args.length == 0 || args.length == 1) return warpNameList;
            return new ArrayList<>();
        }
        return null;
    }
}
