package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotOnlinePlayerErrorException extends ErrorException {
    public NotOnlinePlayerErrorException(CommandSender sendTarget) {
        // 没有在线玩家
        super(sendTarget, "not_online_player_error");
    }
}
