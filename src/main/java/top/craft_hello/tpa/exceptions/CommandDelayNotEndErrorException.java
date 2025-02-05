package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class CommandDelayNotEndErrorException extends ErrorException {
    public CommandDelayNotEndErrorException(CommandSender sendTarget, String delay) {
        // 命令需要等待才能再次使用错误
        super(sendTarget, "command_delay_not_end_error", delay);
    }
}
