package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorPermissionDeniedException extends ErrorException {
    public ErrorPermissionDeniedException(CommandSender sendTarget) {
        // 没有权限
        super(sendTarget, "error.permission_denied");
    }
}
