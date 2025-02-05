package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotSetHomesErrorException extends ErrorException {
    public NotSetHomesErrorException(CommandSender sendTarget) {
        // 未设置家
        super(sendTarget, "not_set_homes_error");
    }
}
