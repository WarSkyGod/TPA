package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class SelfRequestedErrorException extends ErrorException {
    public SelfRequestedErrorException(CommandSender sendTarget) {
        // 不能自己请求自己
        super(sendTarget, "self_requested_error");
    }
}
