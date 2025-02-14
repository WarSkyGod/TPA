package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorNoHomesSetException extends ErrorException {
    public ErrorNoHomesSetException(CommandSender sendTarget) {
        // 未设置家
        super(sendTarget, "error.no_homes_set");
    }
}
