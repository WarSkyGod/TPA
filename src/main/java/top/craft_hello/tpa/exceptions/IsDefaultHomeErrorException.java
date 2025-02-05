package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class IsDefaultHomeErrorException extends ErrorException {
    public IsDefaultHomeErrorException(CommandSender sendTarget, String homeName) {
        // 已是默认的家
        super(sendTarget, "is_default_home_error", homeName);
    }
}
