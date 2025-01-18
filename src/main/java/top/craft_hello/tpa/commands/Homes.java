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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Homes implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (ErrorCheckUtil.check(executor, args, RequestType.HOMES)){
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
            Set<String> homeSet = playerData.getKeys(true);
            List<String> list = new ArrayList<>();
            for (String homeName : homeSet) {
                if (homeName.contains("homes.")) {
                    String homeName2 = homeName.substring(homeName.indexOf(".") + 1);
                    if (!homeName2.contains(".")) list.add(homeName2);
                }
            }
            Messages.homeListMessage(executor, list);
        }
        return true;
    }
}
