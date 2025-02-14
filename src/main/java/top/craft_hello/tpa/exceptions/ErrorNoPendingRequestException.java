package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorNoPendingRequestException extends ErrorException {
    public ErrorNoPendingRequestException(CommandSender sendTarget) {
        // 没有待处理的请求
        super(sendTarget, "error.no_pending_request");
    }
}
