package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorCommandDisabledException extends ErrorException {
    public ErrorCommandDisabledException(CommandSender sendTarget) {
        // 服务器未启用此命令
        super(sendTarget, "error.command_disabled");
    }
}
