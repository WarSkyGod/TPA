package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class UpdateFailedException extends ErrorException {
    public UpdateFailedException(CommandSender sendTarget) {
        // 无法获取最新版本
        super(sendTarget, "update.failed");
    }
}
