package top.craft_hello.tpa.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

public class SetWarp implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (ErrorCheckUtil.check(executor, args, RequestType.SETWARP)){
            Location location = ((Player) executor).getLocation();
            String warpName = args[args.length - 1];
            LoadingConfigFileUtil.setWarp(warpName, location);
            Messages.setWarpSuccess(executor, warpName);
        }
        return true;
    }
}
