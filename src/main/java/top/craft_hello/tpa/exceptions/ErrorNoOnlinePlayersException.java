package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorNoOnlinePlayersException extends ErrorException {
    public ErrorNoOnlinePlayersException(CommandSender sendTarget) {
        // 没有在线玩家
        super(sendTarget, "error.no_online_players");
    }
}
