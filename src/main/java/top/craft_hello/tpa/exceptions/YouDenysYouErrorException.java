package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class YouDenysYouErrorException extends ErrorException {
    public YouDenysYouErrorException(CommandSender sendTarget) {
        // 不能添加自己
        super(sendTarget, "you_denys_you_error");
    }
}
