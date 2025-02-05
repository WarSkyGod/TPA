package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class IsDenysErrorException extends ErrorException {
    public IsDenysErrorException(CommandSender sendTarget) {
        // 对方已将您拉黑
        super(sendTarget, "is_denys_error");
    }
}
