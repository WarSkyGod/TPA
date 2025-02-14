package top.craft_hello.tpa.exceptions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.craft_hello.tpa.abstracts.ErrorException;

import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;
import static top.craft_hello.tpa.utils.SendMessageUtil.sendMessage;

public class ErrorConfigNotFoundException extends ErrorException {
    public ErrorConfigNotFoundException(CommandSender sendTarget) {
        // 配置文件不存在
        super(sendTarget, "error.config_not_found");
    }

    @Override
    public void sendErrorMessage(){
        CommandSender console = Bukkit.getConsoleSender();
        sendMessage(console, getLanguage(console).getFormatPrefixMessage(MESSAGE_INDEX, VARS));
        if (SEND_TARGET instanceof Player) sendMessage(SEND_TARGET, LANGUAGE.getFormatPrefixMessage(SEND_TARGET, MESSAGE_INDEX, VARS));
    }
}
