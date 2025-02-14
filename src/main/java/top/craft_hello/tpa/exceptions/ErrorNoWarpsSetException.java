package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorNoWarpsSetException extends ErrorException {
    public ErrorNoWarpsSetException(CommandSender sendTarget) {
        // 未设置传送点
        super(sendTarget, "error.no_warps_set");
    }
}
