package top.craft_hello.tpa.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.utils.OptimizedAsyncTabCompleteUtil;

import java.util.List;

public class TpLogoutTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length != 1) return null;

        if (sender instanceof Player){
            Player player = (Player) sender;
            String input = args[0]; // 获取用户输入的部分名称

            // 使用优化的异步工具类过滤离线玩家名称
            return OptimizedAsyncTabCompleteUtil.filterOfflinePlayers(input, player.getName());
        }
        return null;
    }
}
