package top.craft_hello.tpa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.TeleportUtil;


public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender executor, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // 判断是否是重新加载命令
        if (args.length == 1 && args[0].equals("reload")){
            if (ErrorCheckUtil.reload(executor, RequestType.RELOAD)){
                LoadingConfigFileUtil.reloadALLConfig(executor);
            }
            return true;
        }
        TeleportUtil.addRequest(executor, args, RequestType.TPA);
        return true;
    }
}
