package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotWarpErrorException extends ErrorException {
    public NotWarpErrorException(CommandSender sendTarget, String warpName) {
        // 找不到传送点
        super(sendTarget, "not_warp_error", warpName);
    }
}
