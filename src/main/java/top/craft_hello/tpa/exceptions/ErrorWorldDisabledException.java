package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorWorldDisabledException extends ErrorException {
    public ErrorWorldDisabledException(CommandSender sendTarget) {
        // 在此世界中无法使用该命令
        super(sendTarget, "error.world_disabled");
    }
}
