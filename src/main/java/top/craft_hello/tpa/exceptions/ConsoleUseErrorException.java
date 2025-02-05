package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ConsoleUseErrorException extends ErrorException {
    public ConsoleUseErrorException(CommandSender sendTarget) {
        // 命令只能由玩家执行
        super(sendTarget, "console_use_error");
    }
}
