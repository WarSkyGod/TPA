package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotLastLocationErrorException extends ErrorException {
    public NotLastLocationErrorException(CommandSender sendTarget) {
        // 找不到上一次的位置
        super(sendTarget, "not_last_location_error");
    }
}
