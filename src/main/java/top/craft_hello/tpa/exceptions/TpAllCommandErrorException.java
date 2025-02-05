package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class TpAllCommandErrorException extends ErrorException {
    public TpAllCommandErrorException(CommandSender sendTarget, String command) {
        // tpall命令错误
        super(sendTarget, "tpall_command_error", command);
    }
}
