package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorSyntaxGenericException extends ErrorException {
    public ErrorSyntaxGenericException(CommandSender sendTarget, String command) {
        // 命令错误
        super(sendTarget, "error.syntax_generic", command);
    }
}
