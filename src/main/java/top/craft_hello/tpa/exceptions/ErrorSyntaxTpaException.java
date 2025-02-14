package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorSyntaxTpaException extends ErrorException {
    public ErrorSyntaxTpaException(CommandSender sendTarget, String command) {
        // tpa命令错误
        super(sendTarget, "error.syntax_tpa", command);
    }
}
