package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorConsoleRestrictedException extends ErrorException {
    public ErrorConsoleRestrictedException(CommandSender sendTarget) {
        // 命令只能由玩家执行
        super(sendTarget, "error.console_restricted");
    }
}
