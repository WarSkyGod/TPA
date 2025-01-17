package top.craft_hello.tpa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

public class SetDefaultHome implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (ErrorCheckUtil.check(executor, args, RequestType.SETDEFAULTHOME)){
            String defaultHome = args[args.length - 1];
            LoadingConfigFileUtil.setPlayerDataString(executor, "default_home", defaultHome);
            Messages.setDefaultHomeSuccess(executor, defaultHome);
        }
        return true;
    }
}
