package top.craft_hello.tpa.exception;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

public class HomeCommandErrorException extends Exception {
    CommandSender sendTarget;
    String command;
    boolean sendMessage;

    public HomeCommandErrorException() {
        this.sendMessage = false;
    }

    public HomeCommandErrorException(@NotNull CommandSender sendTarget, @NotNull String command) {
        this.sendTarget = sendTarget;
        this.command = command;
        this.sendMessage = true;
    }

    public void sendMessage(){
        if (this.sendMessage){
            Messages.homeCommandError(sendTarget, command);
        }
    }
}
