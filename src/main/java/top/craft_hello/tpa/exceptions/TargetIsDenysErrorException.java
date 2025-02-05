package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class TargetIsDenysErrorException extends ErrorException {
    public TargetIsDenysErrorException(CommandSender sendTarget) {
        // 对方已在拒绝请求列表
        super(sendTarget, "target_is_denys_error");
    }
}
