package top.craft_hello.tpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoadingConfigFileUtil {
    protected static Plugin PLUGIN;
    protected static ConsoleCommandSender CONSOLE;
    private static FileConfiguration config;
    private static File warpFile;
    private static FileConfiguration warp;
    private static File homeFile;
    private static FileConfiguration home;
    private static File lastLocationFile;
    private static FileConfiguration lastLocation;
    private static File spawnFile;
    private static FileConfiguration spawn;
    private static File langFile;

    private static FileConfiguration lang;
    public static void init(final Plugin plugin) {
        PLUGIN = plugin;
        CONSOLE = Bukkit.getConsoleSender();
        PLUGIN.saveDefaultConfig();
        PLUGIN.reloadConfig();
        config = PLUGIN.getConfig();
        String configVersion = config.getString("version");
        if (configVersion == null || !configVersion.equals(PLUGIN.getPluginMeta().getVersion())) {
            configVersionUpdate(configVersion);
        } else {
            saveAllDefaultLangFile();
            Messages.reloadDefaultLang();
        }
        String langStr = Objects.requireNonNull(config.getString("lang")).toLowerCase();
        if (langStr.contains("_")){
            String langStr2 = langStr.substring(0, langStr.indexOf("_"));
            String langStr3 = langStr.substring(langStr2.length() + 1).toUpperCase();
            langStr = langStr2 + "_" +langStr3;
        } else {
            langStr = "en_US";
        }
        lang = loadingLangConfig(langStr);
        Messages.reloadDefaultLang();
        warp = loadingWarpConfig();
        home = loadingHomeConfig();
        lastLocation = loadingLastLocationConfig();
        spawn = loadingSpawnConfig();
    }

    public static void saveAllDefaultLangFile(){
        lang = loadingLangConfig("en_US");
        lang = loadingLangConfig("zh_CN");
    }

    public static void configVersionUpdate(String configVersion){
        String lang = config.getString("lang");
        int acceptDelay = config.getInt("accept_delay");
        int teleportDelay = config.getInt("teleport_delay");
        configVersion = configVersion == null ? "1.0" : configVersion;
        new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/lang").mkdirs();
        File configFile = new File(PLUGIN.getDataFolder(), "config.yml");
        configFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/config.yml"));
        configFile.delete();
        PLUGIN.saveDefaultConfig();
        PLUGIN.reloadConfig();
        config = PLUGIN.getConfig();
        File langFiles = new File(PLUGIN.getDataFolder() + "/lang");
        File[] files = langFiles.listFiles();
        if (files != null) {
            for (File file : files) {
                file.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/lang/" + file.getName()));
                file.delete();
            }
        }
        saveAllDefaultLangFile();
        LoadingConfigFileUtil.lang = loadingLangConfig(lang == null ? "en_US" : lang);
        Messages.reloadDefaultLang();
        Messages.configVersionUpdate(Bukkit.getConsoleSender());
        switch (configVersion){
            case "2.0.0":
            case "1.3":
            case "1.2":
                config.set("lang", lang);
                config.set("accept_delay", acceptDelay);
                config.set("teleport_delay", teleportDelay);
                warpFile = new File(PLUGIN.getDataFolder(), "warp.yml");
                warpFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/warp.yml"));
                break;
            case "1.1":
                config.set("lang", lang);
                config.set("accept_delay", acceptDelay);
                config.set("teleport_delay", teleportDelay);
                File resLocFile = new File(PLUGIN.getDataFolder(), "res_loc.yml");
                warpFile = new File(PLUGIN.getDataFolder(), "warp.yml");
                resLocFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/res_loc.yml"));
                resLocFile.renameTo(warpFile);
                resLocFile.delete();
                break;
            case "1.0":
                config.set("lang", lang);
                resLocFile = new File(PLUGIN.getDataFolder(), "res_loc.yml");
                warpFile = new File(PLUGIN.getDataFolder(), "warp.yml");
                resLocFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/res_loc.yml"));
                resLocFile.renameTo(warpFile);
                resLocFile.delete();
            default:
                LoadingConfigFileUtil.lang = loadingLangConfig(lang == null ? "en_US" : lang);
                Messages.pluginError(Bukkit.getConsoleSender(), "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                return;
        }
        configFile = new File(PLUGIN.getDataFolder(), "config.yml");
        try {
            config.save(configFile);
        } catch (IOException ignored) {} finally {
            PLUGIN.saveDefaultConfig();
            PLUGIN.reloadConfig();
            config = PLUGIN.getConfig();
        }
        LoadingConfigFileUtil.lang = loadingLangConfig(lang == null ? "en_US" : lang);
        Messages.reloadDefaultLang();
        Messages.configVersionUpdateSuccess(Bukkit.getConsoleSender());
    }

    public static FileConfiguration loadingLangConfig(@NotNull String lang){
        langFile = new File(PLUGIN.getDataFolder(), "lang/" + lang + ".yml");
        if (!langFile.exists()) {
            try {
                PLUGIN.saveResource("lang/" + lang + ".yml", false);
                langFile = new File(PLUGIN.getDataFolder(), "lang/" + lang + ".yml");
            } catch (IllegalArgumentException ex){
                // 如果无法加载该语言，就尝试加载配置文件中的默认语言
                langFile = new File(PLUGIN.getDataFolder(), "lang/" + config.getString("lang") + ".yml");
                if (!langFile.exists()){
                    PLUGIN.saveResource("lang/" + config.getString("lang") + ".yml", false);
                    langFile = new File(PLUGIN.getDataFolder(), "lang/" + config.getString("lang") + ".yml");
                }
            }
        }
        return YamlConfiguration.loadConfiguration(langFile);
    }
    public static FileConfiguration loadingConfig(@NotNull String fileName, @NotNull File file){
        if (!file.exists()){
            PLUGIN.saveResource(fileName, false);
        }
        file = new File(PLUGIN.getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }


    public static FileConfiguration loadingWarpConfig(){
        String fileName = "warp.yml";
        warpFile = new File(PLUGIN.getDataFolder(), fileName);
        return loadingConfig(fileName, warpFile);
    }

    public static FileConfiguration loadingHomeConfig(){
        String fileName = "home.yml";
        homeFile = new File(PLUGIN.getDataFolder(), fileName);
        return loadingConfig(fileName, homeFile);
    }

    public static FileConfiguration loadingLastLocationConfig(){
        String fileName = "last_location.yml";
        lastLocationFile = new File(PLUGIN.getDataFolder(), fileName);
        return loadingConfig(fileName, lastLocationFile);
    }

    public static FileConfiguration loadingSpawnConfig(){
        String fileName = "spawn.yml";
        spawnFile = new File(PLUGIN.getDataFolder(), fileName);
        return loadingConfig(fileName, spawnFile);
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getLang() {
        return lang;
    }

    public static FileConfiguration getLang(@NotNull String lang) {
        return loadingLangConfig(lang);
    }


    public static FileConfiguration getWarp() {
        return warp;
    }

    public static FileConfiguration getHome() {
        return home;
    }

    public static FileConfiguration getLastLocation() {
        return lastLocation;
    }

    public static FileConfiguration getSpawn() {
        return spawn;
    }

    public static boolean saveConfig(@NotNull CommandSender sender, FileConfiguration config, File configFile){
        try {
            config.save(configFile);
            return true;
        } catch (IOException ex) {
            Messages.configNotFound(sender);
            reloadALLConfig(sender);
            return false;
        }
    }

    public static FileConfiguration setConfig(@NotNull CommandSender sender, @NotNull FileConfiguration config, @NotNull File file, @NotNull String target, Location location){
        config.set(target, location);
        while (!saveConfig(sender, config, file))
            config.set(target, location);
        return config;
    }

    public static FileConfiguration setWarp(@NotNull CommandSender sender, @NotNull String warpName, @NotNull Location location){
        return setConfig(sender, warp, warpFile, warpName, location);
    }

    public static FileConfiguration delWarp(@NotNull CommandSender sender, @NotNull String warpName){
        return setConfig(sender, warp, warpFile, warpName, null);
    }

    public static FileConfiguration setHome(@NotNull CommandSender sender, @NotNull String homeName, @NotNull Location location){
        String target = sender.getName() + "." + homeName;
        String target2 = sender.getName() + "." + "home_amount";
        if (!home.contains(target)){
            int homeAmount = home.getInt(target2);
            home.set(target2, ++homeAmount);
            while (!saveConfig(sender, home, homeFile))
                home.set(target2, homeAmount);
        }
        return setConfig(sender, home, homeFile, target, location);
    }

    public static FileConfiguration delHome(@NotNull CommandSender sender, @NotNull String homeName){
        String target = sender.getName() + "." + homeName;
        String target2 = sender.getName() + "." + "home_amount";
        int homeAmount = home.getInt(target2);
        if (--homeAmount <= 0) return setConfig(sender, home, homeFile, sender.getName(), null);
        home.set(target2, homeAmount);
        while (!saveConfig(sender, home, homeFile)) {
            home.set(target2, homeAmount);
        }
        return setConfig(sender, home, homeFile, target, null);
    }

    public static FileConfiguration setLastLocation(@NotNull CommandSender sender, @NotNull Location location){
        return setConfig(sender, lastLocation, lastLocationFile, sender.getName(), location);
    }

    public static FileConfiguration setSpawn(@NotNull CommandSender sender, @NotNull Location location){
        return setConfig(sender, spawn, spawnFile, "spawn", location);
    }

    public static FileConfiguration delSpawn(@NotNull CommandSender sender){
        return setConfig(sender, spawn, spawnFile, "spawn", null);
    }

    public static void reloadALLConfig(@NotNull CommandSender sender){
        init(PLUGIN);
        Messages.reloadDefaultLang();
        Messages.configReloaded(sender);
    }
}
