package top.craft_hello.tpa.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.objects.Config;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.utils.LoadingConfigUtil;
import top.craft_hello.tpa.utils.OptimizedAsyncTabCompleteUtil;

import java.util.ArrayList;
import java.util.List;

public class TpaTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        LanguageConfig language = LanguageConfig.getLanguage(sender);
        Config config = LoadingConfigUtil.getConfig();
        if (args.length == 0 || (args.length == 1 && "spawn".equalsIgnoreCase(args[args.length - 1]) || args.length == 1 && "version".equalsIgnoreCase(args[args.length - 1]))){
            return list;
        }

        if (args.length == 1){
            if (sender instanceof Player){
                Player player = (Player) sender;
                String input = args[0]; // 获取用户输入的部分名称

                // 使用优化的异步工具类过滤玩家名称
                List<String> filteredPlayers = OptimizedAsyncTabCompleteUtil.filterOnlinePlayers(input, player);

                // 如果没有匹配的玩家，显示"没有在线玩家"消息
                if (filteredPlayers.isEmpty()) {
                    filteredPlayers.add(language.getMessage("not_online_players"));
                }

                // 添加命令选项
                if (config.hasPermission(sender, PermissionType.RELOAD)) filteredPlayers.add("reload");
                if (config.hasPermission(sender, PermissionType.VERSION)) filteredPlayers.add("version");
                filteredPlayers.add("setlang");

                return filteredPlayers;
            }
            return list;
        }

        if (args.length == 2 && "setlang".equalsIgnoreCase(args[args.length - 2])){
            list.add("clear");
            list.addAll(LanguageConfig.getLanguageTextList());
            return list;
        }
        return list;
    }
}
