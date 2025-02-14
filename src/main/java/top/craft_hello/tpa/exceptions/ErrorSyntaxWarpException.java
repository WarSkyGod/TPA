package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorSyntaxWarpException extends ErrorException {
    public ErrorSyntaxWarpException(CommandSender sendTarget, String command) {
        // warp命令错误
        super(sendTarget, "error.syntax_warp", command);
    }
}
