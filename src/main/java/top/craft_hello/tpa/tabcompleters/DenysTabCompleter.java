package top.craft_hello.tpa.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.objects.PlayerDataConfig;
import top.craft_hello.tpa.utils.OptimizedAsyncTabCompleteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;

public class DenysTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0){
            return list;
        }

        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length == 1){
                list.add("add");
                list.add("remove");
                list.add(" ");
                return list;
            }

            if (args.length == 2){
                String subCommand = args[args.length - 2];
                PlayerDataConfig playerDataConfig = PlayerDataConfig.getPlayerData(player);
                List<String> denyList;
                try {
                    denyList = playerDataConfig.getDenyList(null);
                } catch (Exception exception) {
                    denyList = new ArrayList<>();
                    if (getConfig().isDebug()) exception.printStackTrace();
                }
                switch (subCommand) {
                    case "add":
                        // 获取所有未被拒绝的离线玩家
                        List<String> allowedPlayers = new ArrayList<>();
                        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                            if (!playerDataConfig.isDeny(offlinePlayer.getUniqueId().toString()) && 
                                !offlinePlayer.getName().equals(sender.getName())) {
                                allowedPlayers.add(offlinePlayer.getName());
                            }
                        }

                        // 使用优化的异步工具类过滤玩家名称
                        return OptimizedAsyncTabCompleteUtil.filterPlayerNames(args[1], allowedPlayers);
                    case "remove":
                        // 获取所有被拒绝的玩家
                        List<String> deniedPlayers = new ArrayList<>();
                        for (String playerUUID : denyList) {
                            deniedPlayers.add(Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName());
                        }

                        // 使用优化的异步工具类过滤玩家名称
                        return OptimizedAsyncTabCompleteUtil.filterPlayerNames(args[1], deniedPlayers);
                }
            }
        }
        return list;
    }
}
