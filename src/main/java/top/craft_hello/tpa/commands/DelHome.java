package top.craft_hello.tpa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.TeleportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DelHome implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (ErrorCheckUtil.check(executor, args, RequestType.DELHOME)){
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
            String defaultHome = playerData.getString("default_home");
            String homeName = args[args.length - 1];
            if (!ErrorCheckUtil.isNull(defaultHome) && defaultHome.equals(homeName)){
                Set<String> homeSet = playerData.getKeys(true);
                List<String> list = new ArrayList<>();
                for (String home : homeSet) {
                    if (home.contains("homes.")) {
                        String home2 = home.substring(home.indexOf(".") + 1);
                        if (!home2.contains(".") && !home2.equals(defaultHome)) list.add(home2);
                    }
                }
                defaultHome = list.isEmpty() ? null : list.get(0);
                LoadingConfigFileUtil.setPlayerDataString(executor, "default_home", defaultHome);
            }
            LoadingConfigFileUtil.delHome(executor, homeName);
            Messages.delHomeSuccess(executor, homeName);
        }
        return true;
    }
}
