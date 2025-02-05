package top.craft_hello.tpa.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.utils.LoadingConfigUtil;

import java.util.ArrayList;
import java.util.List;

import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;


public class WarpTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        LanguageConfig language = getLanguage(sender);
        if (args.length == 2){
            List<String> list = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                list.add(onlinePlayer.getName());
            }
            if (list.isEmpty()) list.add(language.getMessage("not_online_player"));
            return list;
        }
        List<String> warpNameList = LoadingConfigUtil.getWarpConfig().getWarpNameList();
        if (warpNameList.isEmpty()) warpNameList.add(language.getMessage("warp_name"));
        return warpNameList;
    }
}
