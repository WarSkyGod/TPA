package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotSetSpawnErrorException extends ErrorException {
    public NotSetSpawnErrorException(CommandSender sendTarget) {
        // 未设置主城
        super(sendTarget, "not_set_spawn_error");
    }
}
