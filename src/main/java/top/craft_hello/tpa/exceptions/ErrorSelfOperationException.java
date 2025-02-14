package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorSelfOperationException extends ErrorException {
    public ErrorSelfOperationException(CommandSender sendTarget) {
        // 此命令不能对自己执行
        super(sendTarget, "error.self_operation");
    }
}
