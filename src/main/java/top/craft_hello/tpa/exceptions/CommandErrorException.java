package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class CommandErrorException extends ErrorException {
    public CommandErrorException(CommandSender sendTarget, String command) {
        // 命令错误
        super(sendTarget, "command_error", command);
    }
}
