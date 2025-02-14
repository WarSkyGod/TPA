package top.craft_hello.tpa.exceptions;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.ErrorException;

public class RtpFailedException extends ErrorException {
    public RtpFailedException(CommandSender sendTarget) {
        // 生成随机传送点失败
        super(sendTarget, "rtp.failed");
    }
}
