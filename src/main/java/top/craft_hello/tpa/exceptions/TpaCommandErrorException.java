package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class TpaCommandErrorException extends ErrorException {
    public TpaCommandErrorException(CommandSender sendTarget, String command) {
        // tpa命令错误
        super(sendTarget, "tpa_command_error", command);
    }
}
