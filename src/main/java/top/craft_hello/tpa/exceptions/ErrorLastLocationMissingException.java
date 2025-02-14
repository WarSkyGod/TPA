package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorLastLocationMissingException extends ErrorException {
    public ErrorLastLocationMissingException(CommandSender sendTarget) {
        // 找不到上一次的位置
        super(sendTarget, "error.last_location_missing");
    }
}
