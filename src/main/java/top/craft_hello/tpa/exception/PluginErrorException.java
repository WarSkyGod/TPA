package top.craft_hello.tpa.exception;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

public class PluginErrorException extends Exception {
    CommandSender sendTarget;
    String message;
    boolean sendMessage;

    public PluginErrorException() {
        this.sendMessage = false;
    }

    public PluginErrorException(@NotNull CommandSender sendTarget, @NotNull String message) {
        this.sendTarget = sendTarget;
        this.message = message;
        this.sendMessage = true;
    }

    public void sendMessage(){
        if (this.sendMessage){
            Messages.pluginError(sendTarget, message);
        }
    }
}
