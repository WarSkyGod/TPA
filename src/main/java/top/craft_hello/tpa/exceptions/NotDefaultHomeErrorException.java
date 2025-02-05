package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotDefaultHomeErrorException extends ErrorException {
    public NotDefaultHomeErrorException(CommandSender sendTarget) {
        // 没有默认的家
        super(sendTarget, "not_default_home_error");
    }
}
