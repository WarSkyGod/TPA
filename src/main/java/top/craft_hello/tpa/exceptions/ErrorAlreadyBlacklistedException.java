package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorAlreadyBlacklistedException extends ErrorException {
    public ErrorAlreadyBlacklistedException(CommandSender sendTarget) {
        // 对方已在黑名单中
        super(sendTarget, "error.already_blacklisted");
    }
}
