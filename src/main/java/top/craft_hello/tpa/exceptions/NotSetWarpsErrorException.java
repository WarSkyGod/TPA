package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class NotSetWarpsErrorException extends ErrorException {
    public NotSetWarpsErrorException(CommandSender sendTarget) {
        // 未设置传送点
        super(sendTarget, "not_set_warps_error");
    }
}
