package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotLogoutLocationErrorException extends ErrorException {
    public NotLogoutLocationErrorException(CommandSender sendTarget) {
        // 找不到最后下线地点
        super(sendTarget, "not_logout_location_error");
    }
}
