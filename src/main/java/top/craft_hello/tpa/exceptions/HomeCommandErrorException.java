package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class HomeCommandErrorException extends ErrorException {
    public HomeCommandErrorException(CommandSender sendTarget, String command) {
        // home命令错误消息
        super(sendTarget, "home_command_error", command);
    }

}
