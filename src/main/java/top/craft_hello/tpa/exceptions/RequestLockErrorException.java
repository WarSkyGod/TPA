package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class RequestLockErrorException extends ErrorException {
    public RequestLockErrorException(CommandSender sendTarget) {
        // 自己或对方有尚未结束的请求
        super(sendTarget, "request_lock_error");
    }
}
