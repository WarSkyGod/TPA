package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class HomeMaxLimitErrorException extends ErrorException {
    public HomeMaxLimitErrorException(CommandSender sendTarget, int homeMaxAmount) {
        // 拥有的家已达上限消息
        super(sendTarget, "home.max_limit_error", String.valueOf(homeMaxAmount));
    }
}
