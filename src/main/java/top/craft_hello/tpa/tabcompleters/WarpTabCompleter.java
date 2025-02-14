package top.craft_hello.tpa.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.utils.LoadingConfigUtil;

import java.util.List;

import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;


public class WarpTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        LanguageConfig language = getLanguage(sender);
        List<String> warpNameList = LoadingConfigUtil.getWarpConfig().getWarpNameList();
        if (warpNameList.isEmpty()) warpNameList.add(language.getMessage("not_warps"));
        return warpNameList;
    }
}
