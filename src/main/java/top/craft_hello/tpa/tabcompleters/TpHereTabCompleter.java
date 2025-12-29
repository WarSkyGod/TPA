package top.craft_hello.tpa.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.utils.OptimizedAsyncTabCompleteUtil;

import java.util.List;

public class TpHereTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            return null;
        }

        if (sender instanceof Player && args.length == 1) {
            Player player = (Player) sender;
            String input = args[0]; // 获取用户输入的部分名称

            // 使用优化的异步工具类过滤玩家名称
            List<String> filteredPlayers = OptimizedAsyncTabCompleteUtil.filterOnlinePlayers(input, player);

            // 如果没有匹配的玩家，显示"没有在线玩家"消息
            if (filteredPlayers.isEmpty()) {
                LanguageConfig language = LanguageConfig.getLanguage(sender);
                filteredPlayers.add(language.getMessage("not_online_players"));
            }

            return filteredPlayers;
        }

        return null;
    }
}
