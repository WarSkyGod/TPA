package top.craft_hello.tpa.commands;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.utils.ErrorCheckUtil;



public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {


        // 判断是否是重新加载命令
        if (args.length == 1 && "reload".equalsIgnoreCase(args[args.length - 1])){
            ErrorCheckUtil.executeCommand(executor, args, "reload");
            return true;
        }

        // 判断是否是查询版本命令
        if (args.length == 1 && "version".equalsIgnoreCase(args[args.length - 1])){
            HandySchedulerUtil.runTaskAsynchronously(() -> ErrorCheckUtil.executeCommand(executor, args, "version"));
            return true;
        }

        // 判断是否是设置显示语言命令
        if (args.length == 2 && "setlang".equalsIgnoreCase(args[args.length - 2])){
            ErrorCheckUtil.executeCommand(executor, args, "setlang");
            return true;
        }

        ErrorCheckUtil.executeCommand(executor, args, command.getName());
        return true;
    }
}
