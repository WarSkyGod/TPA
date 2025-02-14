package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorNotBlacklistedException extends ErrorException {
    public ErrorNotBlacklistedException(CommandSender sendTarget) {
        // 对方不在黑名单中
        super(sendTarget, "error.not_blacklisted");
    }
}
