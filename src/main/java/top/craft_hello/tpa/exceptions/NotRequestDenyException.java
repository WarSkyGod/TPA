package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotRequestDenyException extends ErrorException {
    public NotRequestDenyException(CommandSender sendTarget) {
        // 没有待拒绝的请求
        super(sendTarget, "not_request_deny");
    }
}
