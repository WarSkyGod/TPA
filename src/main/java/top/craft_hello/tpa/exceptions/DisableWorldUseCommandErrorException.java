package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class DisableWorldUseCommandErrorException extends ErrorException {
    public DisableWorldUseCommandErrorException(CommandSender sendTarget) {
        // 在此世界中无法使用该命令
        super(sendTarget, "disable_world_use_command_error");
    }
}
