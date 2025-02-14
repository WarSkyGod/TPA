package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorBlockedByTargetException extends ErrorException {
    public ErrorBlockedByTargetException(CommandSender sendTarget) {
        // 对方已将您拉黑
        super(sendTarget, "error.blocked_by_target");
    }
}
