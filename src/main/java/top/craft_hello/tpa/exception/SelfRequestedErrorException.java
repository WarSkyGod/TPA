package top.craft_hello.tpa.exception;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

public class SelfRequestedErrorException extends Exception {
    CommandSender sendTarget;
    boolean sendMessage;

    public SelfRequestedErrorException() {
        this.sendMessage = false;
    }

    public SelfRequestedErrorException(@NotNull CommandSender sendTarget) {
        this.sendTarget = sendTarget;
        this.sendMessage = true;
    }

    public void sendMessage(){
        if (this.sendMessage){
            Messages.selfRequestedError(sendTarget);
        }
    }
}
