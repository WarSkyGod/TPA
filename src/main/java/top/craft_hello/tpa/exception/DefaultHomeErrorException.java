package top.craft_hello.tpa.exception;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

public class DefaultHomeErrorException extends Exception {
    CommandSender sendTarget;
    String homeName;
    boolean sendMessage;

    public DefaultHomeErrorException() {
        this.sendMessage = false;
    }

    public DefaultHomeErrorException(@NotNull CommandSender sendTarget, @NotNull String homeName) {
        this.sendTarget = sendTarget;
        this.homeName = homeName;
        this.sendMessage = true;
    }

    public void sendMessage(){
        if (this.sendMessage){
            Messages.defaultHomeError(sendTarget, homeName);
        }
    }
}
