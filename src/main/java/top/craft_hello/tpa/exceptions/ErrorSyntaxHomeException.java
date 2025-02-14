package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorSyntaxHomeException extends ErrorException {
    public ErrorSyntaxHomeException(CommandSender sendTarget, String command) {
        // home命令错误消息
        super(sendTarget, "error.syntax_home", command);
    }

}
