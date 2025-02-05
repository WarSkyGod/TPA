package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotPermissionErrorException extends ErrorException {
    public NotPermissionErrorException(CommandSender sendTarget) {
        // 没有权限
        super(sendTarget, "not_permission_error");
    }
}
