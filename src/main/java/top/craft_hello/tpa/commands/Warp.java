package top.craft_hello.tpa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.TeleportUtil;

public class Warp implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TeleportUtil.addRequest(executor, args, RequestType.WARP);
        return true;
    }
}
