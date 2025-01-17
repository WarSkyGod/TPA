package top.craft_hello.tpa.exception;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

public class HomeAmountMaxErrorException extends Exception {
    CommandSender sendTarget;
    int homeMaxAmount;
    boolean sendMessage;

    public HomeAmountMaxErrorException() {
        this.sendMessage = false;
    }

    public HomeAmountMaxErrorException(@NotNull CommandSender sendTarget, int homeMaxAmount) {
        this.sendTarget = sendTarget;
        this.homeMaxAmount = homeMaxAmount;
        this.sendMessage = true;
    }

    public void sendMessage(){
        if (this.sendMessage){
            Messages.homeAmountMaxError(sendTarget, homeMaxAmount);
        }
    }
}
