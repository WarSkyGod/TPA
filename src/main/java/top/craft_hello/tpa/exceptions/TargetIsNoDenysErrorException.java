package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class TargetIsNoDenysErrorException extends ErrorException {
    public TargetIsNoDenysErrorException(CommandSender sendTarget) {
        // 对方不在拒绝请求列表
        super(sendTarget, "target_is_no_denys_error");
    }
}
