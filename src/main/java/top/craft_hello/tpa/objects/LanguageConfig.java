package top.craft_hello.tpa.objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.abstracts.Configuration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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
        if (LANGUAGES.containsKey(languageStr)) return;
        loadConfiguration();
        LANGUAGES.put(languageStr, this);
    }

    private void loadConfiguration() {
        if (updateConfiguration) updateConfiguration();
    }

    private void updateConfiguration() {
        File backupFolder;
        File oldLanguageFolder;
        switch (configVersion) {
            case "3.2.0":
                backupFolder = new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/language");
                if (!backupFolder.exists()) backupFolder.mkdirs();
                try {
                    Files.move(configurationFile.toPath(), backupFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception ignored) {}
                break;
            case "3.1.3":
            case "3.1.2":
            case "3.1.1":
            case "3.1.0":
            case "3.0.0":
            case "2.0.0":
            case "1.3":
            case "1.2":
            case "1.1":
            case "1.0":
                backupFolder = new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/lang");
                if (!backupFolder.exists()) backupFolder.mkdirs();
                oldLanguageFolder = new File(PLUGIN.getDataFolder(), "lang/");
                if (oldLanguageFolder.exists()) {
                    try {
                        Files.move(oldLanguageFolder.toPath(), backupFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception ignored) {}
                }
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
                    .replaceAll("<command>", vars[vars.length - 1])
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
            try {
                PLUGIN.saveResource("language/" + configurationFile.getName(), isReplace);
            } catch (Exception ignored){}
            if (!configurationFile.exists()) {
                String langStr1 = configurationFile.getName().substring(0, 2);
                for (String langStr : LANGUAGES.keySet()) {
                    if (langStr.substring(0, 2).equals(langStr1)) {
                        LANGUAGES.put(formatLangStr(configurationFile.getName().replace(".yml", "")), LANGUAGES.get(langStr));
                        return;
                    }
                }

                configurationFile = new File(PLUGIN.getDataFolder(), "language/" + getConfig().getDefaultLanguageStr() + ".yml");
                PLUGIN.saveResource("language/" + configurationFile.getName(), isReplace);
                if (!configurationFile.exists()) {
                    configurationFile = new File(PLUGIN.getDataFolder(), "language/zh_CN.yml");
                    PLUGIN.saveResource("language/" + configurationFile.getName(), isReplace);
                }
            }
        }
        try {
            Reader reader = new InputStreamReader(Files.newInputStream(configurationFile.toPath()), StandardCharsets.UTF_8);
            configuration = YamlConfiguration.loadConfiguration(reader);
        } catch (Exception ignored){}
    }

    public static void loadAllLanguage() {
        loadAllDefaultLanguage();
        File langFolder = new File(PLUGIN.getDataFolder(), "/language");
        File[] files = langFolder.listFiles();
        if (!isNull(files)) for (File file : files) new LanguageConfig(file);
    }

    private static void loadAllDefaultLanguage() {
        // 加载插件自带的语言文件
        // 简体中文
        File languageFile = new File(PLUGIN.getDataFolder(), "language/zh_CN.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/zh_CN.yml", false);
        new LanguageConfig(languageFile);
        // 繁体中文（中国香港特别行政区）
        languageFile = new File(PLUGIN.getDataFolder(), "language/zh_HK.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/zh_HK.yml", false);
        new LanguageConfig(languageFile);
        // 繁体中文（中国台湾）
        languageFile = new File(PLUGIN.getDataFolder(), "language/zh_TW.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/zh_TW.yml", false);
        new LanguageConfig(languageFile);
        // 英语
        languageFile = new File(PLUGIN.getDataFolder(), "language/en_US.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/en_US.yml", false);
        new LanguageConfig(languageFile);
        // 俄语
        languageFile = new File(PLUGIN.getDataFolder(), "language/ru_RU.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/ru_RU.yml", false);
        new LanguageConfig(languageFile);
        // 法语
        languageFile = new File(PLUGIN.getDataFolder(), "language/fr_FR.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/fr_FR.yml", false);
        new LanguageConfig(languageFile);
        // 德语
        languageFile = new File(PLUGIN.getDataFolder(), "language/de_DE.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/de_DE.yml", false);
        new LanguageConfig(languageFile);
        // 日语
        languageFile = new File(PLUGIN.getDataFolder(), "language/ja_JP.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/ja_JP.yml", false);
        new LanguageConfig(languageFile);
        // 韩语
        languageFile = new File(PLUGIN.getDataFolder(), "language/ko_KR.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/ko_KR.yml", false);
        new LanguageConfig(languageFile);
        // 葡萄牙语
        languageFile = new File(PLUGIN.getDataFolder(), "language/pt_PT.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/pt_PT.yml", false);
        new LanguageConfig(languageFile);
        // 西班牙语
        languageFile = new File(PLUGIN.getDataFolder(), "language/es_ES.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/es_ES.yml", false);
        new LanguageConfig(languageFile);
        // 意大利语
        languageFile = new File(PLUGIN.getDataFolder(), "language/it_IT.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/it_IT.yml", false);
        new LanguageConfig(languageFile);
        // 挪威语
        languageFile = new File(PLUGIN.getDataFolder(), "language/no_NO.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/no_NO.yml", false);
        new LanguageConfig(languageFile);
        // 土耳其语
        languageFile = new File(PLUGIN.getDataFolder(), "language/tr_TR.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/tr_TR.yml", false);
        new LanguageConfig(languageFile);
        // 斯洛文尼亚语
        languageFile = new File(PLUGIN.getDataFolder(), "language/sl_SL.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/sl_SL.yml", false);
        new LanguageConfig(languageFile);
        // 瑞典语
        languageFile = new File(PLUGIN.getDataFolder(), "language/sv_SE.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/sv_SE.yml", false);
        new LanguageConfig(languageFile);
        // 芬兰语
        languageFile = new File(PLUGIN.getDataFolder(), "language/fi_FI.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/fi_FI.yml", false);
        new LanguageConfig(languageFile);
        // 丹麦语
        languageFile = new File(PLUGIN.getDataFolder(), "language/da_DK.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/da_DK.yml", false);
        new LanguageConfig(languageFile);
        // 希伯来语
        languageFile = new File(PLUGIN.getDataFolder(), "language/he_IL.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/he_IL.yml", false);
        new LanguageConfig(languageFile);
        // 荷兰语
        languageFile = new File(PLUGIN.getDataFolder(), "language/nl_NL.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/nl_NL.yml", false);
        new LanguageConfig(languageFile);
        // 希腊语
        languageFile = new File(PLUGIN.getDataFolder(), "language/el_GR.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/el_GR.yml", false);
        new LanguageConfig(languageFile);
        // 匈牙利语
        languageFile = new File(PLUGIN.getDataFolder(), "language/hu_HU.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/hu_HU.yml", false);
        new LanguageConfig(languageFile);
        // 捷克语
        languageFile = new File(PLUGIN.getDataFolder(), "language/cs_CZ.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/cs_CZ.yml", false);
        new LanguageConfig(languageFile);
        // 波兰语
        languageFile = new File(PLUGIN.getDataFolder(), "language/pl_PL.yml");
        if (!languageFile.exists()) PLUGIN.saveResource("language/pl_PL.yml", false);
        new LanguageConfig(languageFile);
    }

    public static LanguageConfig getLanguage() {
        return getLanguage(getConfig().getDefaultLanguageStr());
    }

    public static LanguageConfig getLanguage(String languageStr) {
        languageStr = formatLangStr(languageStr);
        if (!containsLanguage(languageStr)) {
            new LanguageConfig(new File(PLUGIN.getDataFolder(), "language/" + languageStr + ".yml"));
            if (!containsLanguage(languageStr)) return getLanguage();
        }
        return LANGUAGES.get(languageStr);
    }

    public static LanguageConfig getLanguage(CommandSender sender) {
        String languageStr = null;
        if (sender instanceof OfflinePlayer) {
            OfflinePlayer offlinePlayer = (OfflinePlayer) sender;
            PlayerDataConfig playerDataConfig = PlayerDataConfig.getPlayerData(offlinePlayer);
            languageStr = playerDataConfig.getLanguageStr();
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
