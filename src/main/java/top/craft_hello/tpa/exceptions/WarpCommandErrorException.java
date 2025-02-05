package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class WarpCommandErrorException extends ErrorException {
    public WarpCommandErrorException(CommandSender sendTarget, String command) {
        // warp命令错误
        super(sendTarget, "warp_command_error", command);
    }
}
