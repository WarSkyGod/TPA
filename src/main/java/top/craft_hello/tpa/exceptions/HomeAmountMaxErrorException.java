package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class HomeAmountMaxErrorException extends ErrorException {
    public HomeAmountMaxErrorException(CommandSender sendTarget, int homeMaxAmount) {
        // 拥有的家已达上限消息
        super(sendTarget, "home_amount_max_error", String.valueOf(homeMaxAmount));
    }
}
