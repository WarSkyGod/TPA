package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorLogoutLocationMissingException extends ErrorException {
    public ErrorLogoutLocationMissingException(CommandSender sendTarget) {
        // 找不到最后下线地点
        super(sendTarget, "error.logout_location_missing");
    }
}
