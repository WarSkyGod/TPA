package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class CannotGetTheLatestVersionErrorException extends ErrorException {
    public CannotGetTheLatestVersionErrorException(CommandSender sendTarget) {
        // 无法获取最新版本
        super(sendTarget, "cannot_get_the_latest_version_error");
    }
}
