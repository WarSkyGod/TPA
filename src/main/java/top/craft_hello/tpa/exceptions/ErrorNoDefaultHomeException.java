package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorNoDefaultHomeException extends ErrorException {
    public ErrorNoDefaultHomeException(CommandSender sendTarget) {
        // 没有默认的家
        super(sendTarget, "error.no_default_home");
    }
}
