package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotRequestAcceptException extends ErrorException {
    public NotRequestAcceptException(CommandSender sendTarget) {
        // 没有待接受的请求
        super(sendTarget, "not_request_accept");
    }
}
