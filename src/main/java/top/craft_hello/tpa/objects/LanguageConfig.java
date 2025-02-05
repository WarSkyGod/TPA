package top.craft_hello.tpa.objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.abstracts.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;

public class LanguageConfig extends Configuration {
    private static final Map<String, LanguageConfig> LANGUAGES = new HashMap<>();

    public LanguageConfig(String languageStr){
        this(new File(PLUGIN.getDataFolder(), "language/" + languageStr + ".yml"));
    }

    public LanguageConfig(File configurationFile){
        if (isNull(configurationFile)) return;
        this.configurationFile = configurationFile;
        String fileName = configurationFile.getName().replace(".yml", "");
        if (!fileName.matches("^[a-zA-Z]{2}_[a-zA-Z]{2}$")) return;
        this.languageStr = formatLangStr(fileName);
        if (containsLanguage(languageStr)) return;
        loadConfiguration(false);
        loadConfiguration();
        LANGUAGES.put(languageStr, this);
    }

    private void loadConfiguration() {
        if (updateConfiguration) updateConfiguration();
    }

    private void updateConfiguration() {
        File backupFolder = new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/lang");
        if (!backupFolder.exists()) backupFolder.mkdirs();
        File oldLanguageFolder = new File(PLUGIN.getDataFolder(), "lang/");
        if (oldLanguageFolder.exists()) {
            try {
                Files.move(oldLanguageFolder.toPath(), backupFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) {}
        }
    }

    // 将字符串中的&自动替换为§
    private String formatText(@NotNull String text){
        return text.replaceAll("&", "§");
    }

    // 用于自定义变量的替换
    private String formatText(@NotNull String text, String... vars){
        if (vars.length == 1) {
            if (isNull(vars[vars.length - 1])) return formatText(text);
            return formatText(text).replaceAll("<target>", vars[vars.length - 1])
                    .replaceAll("<message>", vars[vars.length - 1])
                    .replaceAll("<max_home_amount>", vars[vars.length - 1])
                    .replaceAll("<seconds>", vars[vars.length - 1]);
        }

        if (vars.length == 2) {
            if (isNull(vars[vars.length - 2])) vars[vars.length - 2] = "null";
            if (isNull(vars[vars.length - 1])) vars[vars.length - 1] = "null";
            return formatText(text).replaceAll("<target>", vars[vars.length - 2])
                    .replaceAll("<seconds>", vars[vars.length - 1]);
        }

        return formatText(text);
    }

    @Override
    public void reloadConfiguration(){
        loadConfiguration(false);
        loadConfiguration();
    }

    public static void reloadAllLanguage(){
        for (String langStr : getLanguageTextList()) LANGUAGES.remove(langStr);
        LanguageConfig.loadAllLanguage();
    }

    @Override
    protected void loadConfiguration(boolean isReplace){
        if (isReplace || !configurationFile.exists()){
            PLUGIN.saveResource("language/" + configurationFile.getName(), isReplace);
            if (!configurationFile.exists()) {
                configurationFile = new File(PLUGIN.getDataFolder(), "language/" + getConfig().getDefaultLanguageStr() + ".yml");
                PLUGIN.saveResource("language/" + configurationFile.getName(), isReplace);
                if (!configurationFile.exists()) {
                    configurationFile = new File(PLUGIN.getDataFolder(), "language/zh_CN.yml");
                    PLUGIN.saveResource("language/" + configurationFile.getName(), isReplace);
                }
            }
        }
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    public static void loadAllLanguage() {
        loadAllDefaultLanguage();
        File langFolder = new File(PLUGIN.getDataFolder(), "/language");
        File[] files = langFolder.listFiles();
        if (!isNull(files)) for (File file : files) new LanguageConfig(file);
    }

    private static void loadAllDefaultLanguage() {
        File languageFile = new File(PLUGIN.getDataFolder(), "language/zh_CN.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/zh_CN.yml", false);
        new LanguageConfig(languageFile);
        languageFile = new File(PLUGIN.getDataFolder(), "language/en_US.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/en_US.yml", false);
        new LanguageConfig(languageFile);
    }

    public static LanguageConfig getLanguage() {
        return getLanguage(getConfig().getDefaultLanguageStr());
    }

    public static LanguageConfig getLanguage(String languageStr) {
        languageStr = formatLangStr(languageStr);
        if (!containsLanguage(languageStr)) {
            if (LANGUAGES.isEmpty()) {
                new LanguageConfig(new File(PLUGIN.getDataFolder(), "language/" + languageStr + ".yml"));
            } else {
                return getLanguage();
            }
        }
        return LANGUAGES.get(languageStr);
    }

    public static LanguageConfig getLanguage(CommandSender sender) {
        String languageStr = null;
        if (sender instanceof OfflinePlayer) {
            OfflinePlayer offlinePlayer = (OfflinePlayer) sender;
            if (offlinePlayer.isOnline()){
                Player player = offlinePlayer.getPlayer();
                if (getConfig().isOldServer()){
                    if (!isNull(player)) languageStr = PlayerDataConfig.getPlayerData(player).getLanguageStr();
                } else languageStr = player.getLocale();
            } else languageStr = PlayerDataConfig.getPlayerData(offlinePlayer).getLanguageStr();
        }
        return getLanguage(languageStr);
    }

    public static List<String> getLanguageTextList(){
        return new ArrayList<>(LANGUAGES.keySet());
    }

    public static boolean containsLanguage(String languageStr){
        return LANGUAGES.containsKey(languageStr);
    }

    public String getPrefix(){
        return getPrefix(Bukkit.getConsoleSender());
    }

    public String getPrefix(CommandSender sender){
        return sender instanceof Player ? getMessage("prefix") : getMessage("console_prefix");
    }

    public String getMessage(String messageIndex){
        if (isNull(messageIndex)) return "null";
        String message = configuration.getString(messageIndex);
        return message == null ? "null" : message;
    }

    public String getFormatMessage(String messageIndex, String... vars){
        return formatText(getMessage(messageIndex), vars);
    }

    public String getPrefixMessage(String messageIndex){
        return getPrefix() + getMessage(messageIndex);
    }

    public String getPrefixMessage(CommandSender sender, String messageIndex){
        return getPrefix(sender) + getMessage(messageIndex);
    }

    public String getFormatPrefixMessage(String messageIndex, String... vars){
        return formatText(getPrefixMessage(messageIndex), vars);
    }

    public String getFormatPrefixMessage(CommandSender sender, String messageIndex, String... vars){
        return formatText(getPrefixMessage(sender, messageIndex), vars);
    }
}
