package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotHomeErrorException extends ErrorException {
    public NotHomeErrorException(CommandSender sendTarget, String homeName) {
        // 找不到家
        super(sendTarget, "not_home_error", homeName);
    }
}
