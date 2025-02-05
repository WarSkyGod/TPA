package top.craft_hello.tpa.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import top.craft_hello.tpa.exceptions.ConfigNotFoundErrorException;
import top.craft_hello.tpa.exceptions.PluginErrorException;
import top.craft_hello.tpa.objects.LanguageConfig;

import java.io.File;
import java.io.IOException;

import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;
import static top.craft_hello.tpa.utils.SendMessageUtil.sendMessage;


public abstract class ErrorException extends RuntimeException {
    protected final LanguageConfig LANGUAGE;
    protected final CommandSender SEND_TARGET;
    protected final String MESSAGE_INDEX;
    protected final String[] VARS;

    public ErrorException(CommandSender sendTarget, String messageIndex, String... vars) {
        this.LANGUAGE = getLanguage(sendTarget);
        this.SEND_TARGET = sendTarget;
        this.MESSAGE_INDEX = messageIndex;
        this.VARS = vars;
        sendErrorMessage();
    }

    public void sendErrorMessage() {
        sendMessage(SEND_TARGET, LANGUAGE.getFormatPrefixMessage(SEND_TARGET, MESSAGE_INDEX, VARS));
    }

    @Override
    public void printStackTrace() {
        CommandSender console = Bukkit.getConsoleSender();
        String target = SEND_TARGET instanceof Player ? SEND_TARGET.getName() : "Console";
        sendMessage(console, getLanguage().getFormatPrefixMessage("form_target_error_message", target));
        sendMessage(console, getLanguage().getFormatPrefixMessage(MESSAGE_INDEX, VARS));
    }

    public static void catchException(Exception exception) {
        if (getConfig().isDebug()) exception.printStackTrace();
    }

    public static boolean trySaveConfiguration(CommandSender sender, FileConfiguration config, File configFile) {
        try {
            config.save(configFile);
            return true;
        } catch (IOException ignored) {
            catchException(new ConfigNotFoundErrorException(sender));
            return false;
        }
    }

    public static void tryCreateConfiguration(CommandSender sender, File configFile) {
        try {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        } catch (IOException ignored) {
            catchException(new PluginErrorException(sender, "创建配置文件的时候出现问题，请联系开发者（https://github.com/WarSkyGod/TPA/issues）"));
        }
    }
}
