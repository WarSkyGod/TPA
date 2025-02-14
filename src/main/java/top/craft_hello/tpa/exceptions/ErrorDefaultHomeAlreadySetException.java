package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorDefaultHomeAlreadySetException extends ErrorException {
    public ErrorDefaultHomeAlreadySetException(CommandSender sendTarget, String homeName) {
        // 已是默认的家
        super(sendTarget, "error.default_home_already_set", homeName);
    }
}
