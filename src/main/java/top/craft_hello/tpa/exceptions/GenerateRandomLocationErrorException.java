package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class GenerateRandomLocationErrorException extends ErrorException {
    public GenerateRandomLocationErrorException(CommandSender sendTarget) {
        // 生成随机传送点失败
        super(sendTarget, "generate_random_location_error");
    }
}
