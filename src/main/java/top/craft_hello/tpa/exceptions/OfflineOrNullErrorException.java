package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class OfflineOrNullErrorException extends ErrorException {
    public OfflineOrNullErrorException(CommandSender sendTarget) {
        // 目标玩家离线或不存在
        super(sendTarget, "offline_or_null_error");
    }
}
