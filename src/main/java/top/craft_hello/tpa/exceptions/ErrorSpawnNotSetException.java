package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorSpawnNotSetException extends ErrorException {
    public ErrorSpawnNotSetException(CommandSender sendTarget) {
        // 未设置主城
        super(sendTarget, "error.spawn_not_set");
    }
}
