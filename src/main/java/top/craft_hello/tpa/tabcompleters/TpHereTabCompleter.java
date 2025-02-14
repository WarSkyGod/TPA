package top.craft_hello.tpa.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.objects.LanguageConfig;

import java.util.ArrayList;
import java.util.List;

public class TpHereTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        LanguageConfig language = LanguageConfig.getLanguage(sender);
        if (args.length == 0){
            return list;
        }

        if (sender instanceof Player && args.length == 1){
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!sender.equals(player)){
                    list.add(player.getName());
                }
            }
            if (list.isEmpty()) list.add(language.getMessage("not_online_players"));
        }
        return list;
    }
}
