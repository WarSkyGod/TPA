package top.craft_hello.tpa.exception;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

public class SetLangCommandErrorException extends Exception {
    CommandSender sendTarget;
    String command;
    boolean sendMessage;

    public SetLangCommandErrorException() {
        this.sendMessage = false;
    }

    public SetLangCommandErrorException(@NotNull CommandSender sendTarget, @NotNull String command) {
        this.sendTarget = sendTarget;
        this.command = command;
        this.sendMessage = true;
    }

    public void sendMessage(){
        if (this.sendMessage){
            Messages.setLangCommandError(sendTarget, command);
        }
    }
}
