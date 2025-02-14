package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorSyntaxTpAllException extends ErrorException {
    public ErrorSyntaxTpAllException(CommandSender sendTarget, String command) {
        // tpall命令错误
        super(sendTarget, "error.syntax_tpall", command);
    }
}
