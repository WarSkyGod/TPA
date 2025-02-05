package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class PluginErrorException extends ErrorException {
    public PluginErrorException(CommandSender sendTarget, String errorMessage) {
        // 插件错误
        super(sendTarget, "plugin_error", errorMessage);
    }
}
