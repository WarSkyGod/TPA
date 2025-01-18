package top.craft_hello.tpa.commands;

import org.bukkit.Location;
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

public class SetHome implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (ErrorCheckUtil.check(executor, args, RequestType.SETHOME)){
            Location location = ((Player) executor).getLocation();
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
            String defaultHome = playerData.getString("default_home");
            if (args.length == 0){
                String homeName = "default";
                if (!ErrorCheckUtil.isNull(defaultHome)){
                    homeName = defaultHome;
                } else {
                    LoadingConfigFileUtil.setPlayerDataString(executor, "default_home", homeName);
                }

                LoadingConfigFileUtil.setHome(executor, homeName, location);
                Messages.setHomeSuccess(executor, homeName);
                return true;
            }

            String homeName = args[args.length - 1];
            if (ErrorCheckUtil.isNull(defaultHome)){
                LoadingConfigFileUtil.setPlayerDataString(executor, "default_home", homeName);
            }
            LoadingConfigFileUtil.setHome(executor, homeName, location);
            Messages.setHomeSuccess(executor, homeName);
        }
        return true;
    }
}
