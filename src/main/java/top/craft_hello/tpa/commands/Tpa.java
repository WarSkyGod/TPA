package top.craft_hello.tpa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.TeleportUtil;


public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {


        // 判断是否是重新加载命令
        if (args.length == 1 && args[args.length - 1].equals("reload")){
            if (ErrorCheckUtil.check(executor, args, RequestType.RELOAD)){
                LoadingConfigFileUtil.reloadALLConfig(executor);
            }
            return true;
        }

        // 判断是否是设置显示语言命令
        if (args.length == 2 && args[args.length - 2].equals("setlang")){
            if (ErrorCheckUtil.check(executor, args, RequestType.SETLANG)){
                LoadingConfigFileUtil.setClientLang(executor, args[args.length - 1]);
                Messages.setLangCommandSuccess(executor, args[args.length - 1]);
            }
            return true;
        }

        TeleportUtil.addRequest(executor, args, RequestType.TPA);
        return true;
    }
}
