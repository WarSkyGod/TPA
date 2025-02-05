package top.craft_hello.tpa.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.objects.PlayerDataConfig;

import java.util.ArrayList;
import java.util.List;

import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;


public class SetDefaultHomeTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            LanguageConfig language = LanguageConfig.getLanguage(player);
            List<String> homeNameList = new ArrayList<>();
            try {
                homeNameList = PlayerDataConfig.getPlayerData(player).getHomeNameList(null);
            } catch (Exception exception) {
                homeNameList.add(language.getMessage("home_name"));
                if (getConfig().isDebug()) exception.printStackTrace();
            }
            return homeNameList;
        }
        return null;
    }
}
