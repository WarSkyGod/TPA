package top.craft_hello.tpa.commands;

import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Denys implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (ErrorCheckUtil.denys(executor, args, RequestType.DENYS)) {
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
            Set<String> denySet = playerData.getKeys(true);
            List<String> stringList = new ArrayList<>();
            for (String deny : denySet) {
                if (deny.contains("denys.")) {
                    String deny2 = deny.substring(deny.indexOf(".") + 1);
                    if (!deny2.contains(".")) stringList.add(deny2);
                }
            }
            if (args.length == 0) {
                Messages.denysMessage(executor, stringList);
                return true;
            }

            if (args.length == 2) {
                String targetName = args[args.length - 1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                switch (args[args.length - 2]) {
                    case "add":
                        LoadingConfigFileUtil.setDenys(executor, target.getUniqueId().toString(), targetName);
                        if (!ErrorCheckUtil.notRequest(executor)) {
                            PlayerSchedulerUtil.performCommand((Player) executor, "tpdeny");
                        }
                        Messages.addDenysSuccess(executor, targetName);
                        return true;
                    case "remove":
                        LoadingConfigFileUtil.delDenys(executor, target.getUniqueId().toString());
                        Messages.removeDenySuccess(executor, targetName);
                        return true;
                }
            }
        }
        return true;
    }
}
