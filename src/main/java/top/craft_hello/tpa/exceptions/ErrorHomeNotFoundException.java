package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorHomeNotFoundException extends ErrorException {
    public ErrorHomeNotFoundException(CommandSender sendTarget, String homeName) {
        // 找不到家
        super(sendTarget, "error.home_not_found", homeName);
    }
}
