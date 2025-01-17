package top.craft_hello.tpa.exception;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

public class NotWarpErrorException extends Exception {
    CommandSender sendTarget;
    String warpName;
    boolean sendMessage;

    public NotWarpErrorException() {
        this.sendMessage = false;
    }

    public NotWarpErrorException(@NotNull CommandSender sendTarget, @NotNull String warpName) {
        this.sendTarget = sendTarget;
        this.warpName = warpName;
        this.sendMessage = true;
    }

    public void sendMessage(){
        if (this.sendMessage){
            Messages.notWarpError(sendTarget, warpName);
        }
    }
}
