package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorTargetOfflineException extends ErrorException {
    public ErrorTargetOfflineException(CommandSender sendTarget, String playerName) {
        // 目标玩家不在线
        super(sendTarget, "error.target_offline", playerName);
    }
}
