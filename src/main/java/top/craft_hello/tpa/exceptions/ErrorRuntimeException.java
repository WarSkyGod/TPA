package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorRuntimeException extends ErrorException {
    public ErrorRuntimeException(CommandSender sendTarget, String errorMessage) {
        // 插件错误
        super(sendTarget, "error.runtime", errorMessage);
    }
}
