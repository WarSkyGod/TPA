package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class ErrorNotBlockedException extends ErrorException {
    public ErrorNotBlockedException(CommandSender sendTarget) {
        // 未拉黑任何人
        super(sendTarget, "error.block_list_empty");
    }
}
