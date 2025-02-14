package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorWarpNotFoundException extends ErrorException {
    public ErrorWarpNotFoundException(CommandSender sendTarget, String warpName) {
        // 找不到传送点
        super(sendTarget, "error.warp_not_found", warpName);
    }
}
