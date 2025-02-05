package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotAddDenysErrorException extends ErrorException {
    public NotAddDenysErrorException(CommandSender sendTarget) {
        // 未拉黑任何人
        super(sendTarget, "not_add_deny_error");
    }
}
