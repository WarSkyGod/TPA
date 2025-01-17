package top.craft_hello.tpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoadingConfigFileUtil {
    protected static Plugin PLUGIN;
    private static String VERSION;
    protected static ConsoleCommandSender CONSOLE;
    private static FileConfiguration config;
    private static File warpFile;
    private static FileConfiguration warp;
    private static File spawnFile;
    private static FileConfiguration spawn;
    private static File langFile;

    private static FileConfiguration lang;

    private static Map<String, FileConfiguration> langs;
    public static void init(final Plugin plugin, String version) {
        PLUGIN = plugin;
        VERSION = version;
        CONSOLE = Bukkit.getConsoleSender();
        langs = new HashMap<>();
        PLUGIN.saveDefaultConfig();
        PLUGIN.reloadConfig();
        config = PLUGIN.getConfig();
        String configVersion = config.getString("version");
        if (ErrorCheckUtil.isNull(configVersion) || !configVersion.equals(version)) {
            configVersionUpdate(configVersion);
        } else {
            saveAllDefaultLang();
            saveALLLang();
        }
        config.set("old_server", isOldServer());
        config.set("enable_title_message", !isOldServer());
        config.set("enable_playsound", !isOldServer());
        config.set("enable_command.tpa", config.getBoolean("enable_command.tpa"));
        config.set("enable_command.tphere", config.getBoolean("enable_command.tphere"));
        config.set("enable_command.tpall", config.getBoolean("enable_command.tpall"));
        config.set("enable_command.tplogout", config.getBoolean("enable_command.tplogout"));
        config.set("enable_command.warp", config.getBoolean("enable_command.warp"));
        config.set("enable_command.home", config.getBoolean("enable_command.home"));
        config.set("enable_command.spawn", config.getBoolean("enable_command.spawn"));
        config.set("enable_command.back", config.getBoolean("enable_command.back"));
        config.set("enable_command.setlang", isOldServer());
        PLUGIN.saveConfig();
        PLUGIN.reloadConfig();
        config = PLUGIN.getConfig();
        String langStr = formatLangStr(config.getString("lang"));
        lang = loadingLangConfig(langStr);
        langs.put(langStr, lang);
        warp = loadingWarpConfig();
        spawn = loadingSpawnConfig();
    }

    public static boolean isOldServer(){
        String ver = PLUGIN.getServer().getVersion();
        ver = ver.substring(ver.indexOf("1.")).replaceAll("\\)","");
        int newServerBigVersion = 1;
        int newServerMiddleVersion = 12;
        int serverBigVersion = VersionUtil.getPluginBigVersion(ver);
        int serverMiddleVersion = VersionUtil.getPluginMiddleVersion(ver);

        return VersionUtil.isOlderThan(serverBigVersion, serverMiddleVersion, newServerBigVersion, newServerMiddleVersion);
    }

    public static void saveALLLang(){
        File langFiles = new File(PLUGIN.getDataFolder() + "/lang");
        File[] files = langFiles.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String langStr = formatLangStr(fileName.substring(0, fileName.indexOf(".")));
                langs.put(langStr, loadingLangConfig(langStr));
            }
        }
    }

    public static void saveAllDefaultLang(){
        langs.put("zh_CN", loadingLangConfig("zh_CN"));
        langs.put("en_US", loadingLangConfig("en_US"));
    }

    public static void configVersionUpdate(String configVersion){
        if (configVersion.equals("3.1.0")){
            config.set("version", VERSION);
            PLUGIN.saveConfig();
            PLUGIN.reloadConfig();
            config = PLUGIN.getConfig();
            return;
        }

        String langStr = formatLangStr(config.getString("lang"));
        int acceptDelay = config.getInt("accept_delay");
        int teleportDelay = config.getInt("teleport_delay");
        boolean enableCommandTpa = config.getBoolean("enable_command.tpa");
        boolean enableCommandTphere = config.getBoolean("enable_command.tphere");
        boolean enableCommandTpall = config.getBoolean("enable_command.tpall");
        boolean enableCommandWarp = config.getBoolean("enable_command.warp");
        boolean enableCommandHome = config.getBoolean("enable_command.home");
        boolean enableCommandSpawn = config.getBoolean("enable_command.spawn");
        boolean enableCommandBack = config.getBoolean("enable_command.back");
        boolean enablePermissionTpa = config.getBoolean("enable_permission.tpa");
        boolean enablePermissionTphere = config.getBoolean("enable_permission.tphere");
        boolean enablePermissionWarp = config.getBoolean("enable_permission.warp");
        boolean enablePermissionHome = config.getBoolean("enable_permission.home");
        boolean enablePermissionSpawn = config.getBoolean("enable_permission.spawn");
        boolean enablePermissionBack = config.getBoolean("enable_permission.back");
        int homeAmountDefault = config.getInt("home_amount.default");
        int homeAmountVip = config.getInt("home_amount.vip");
        int homeAmountSvip = config.getInt("home_amount.svip");
        int homeAmountAdmin = config.getInt("home_amount.admin");
        configVersion =  ErrorCheckUtil.isNull(configVersion) ? "1.0" : configVersion;
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
        saveAllDefaultLang();
        LoadingConfigFileUtil.lang = loadingLangConfig(langStr);
        Messages.configVersionUpdate(Bukkit.getConsoleSender());
        switch (configVersion){
            case "3.0.0":
                config.set("lang", langStr);
                config.set("accept_delay", acceptDelay);
                config.set("teleport_delay", teleportDelay);
                config.set("enable_command.tpa", enableCommandTpa);
                config.set("enable_command.tphere", enableCommandTphere);
                config.set("enable_command.tpall", enableCommandTpall);
                config.set("enable_command.warp", enableCommandWarp);
                config.set("enable_command.home", enableCommandHome);
                config.set("enable_command.spawn", enableCommandSpawn);
                config.set("enable_command.back", enableCommandBack);
                config.set("enable_permission.tpa", enablePermissionTpa);
                config.set("enable_permission.tphere", enablePermissionTphere);
                config.set("enable_permission.warp", enablePermissionWarp);
                config.set("enable_permission.home", enablePermissionHome);
                config.set("enable_permission.spawn", enablePermissionSpawn);
                config.set("enable_permission.back", enablePermissionBack);
                config.set("home_amount.default", homeAmountDefault);
                config.set("home_amount.vip", homeAmountVip);
                config.set("home_amount.svip", homeAmountSvip);
                config.set("home_amount.admin", homeAmountAdmin);
                warpFile = new File(PLUGIN.getDataFolder(), "warp.yml");
                warpFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/warp.yml"));

                File homeFile = new File(PLUGIN.getDataFolder(), "home.yml");
                homeFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/home.yml"));
                homeFile.delete();
                spawnFile = new File(PLUGIN.getDataFolder(), "spawn.yml");
                spawnFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/spawn.yml"));
                spawnFile.delete();


                File lastLocationFile = new File(PLUGIN.getDataFolder(), "last_location.yml");
                lastLocationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/last_location.yml"));
                lastLocationFile.delete();
                break;
            case "2.0.0":
            case "1.3":
            case "1.2":
                config.set("lang", langStr);
                config.set("accept_delay", acceptDelay);
                config.set("teleport_delay", teleportDelay);
                warpFile = new File(PLUGIN.getDataFolder(), "warp.yml");
                warpFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/warp.yml"));
                warpFile.delete();
                break;
            case "1.1":
                config.set("lang", langStr);
                config.set("accept_delay", acceptDelay);
                config.set("teleport_delay", teleportDelay);
                File resLocFile = new File(PLUGIN.getDataFolder(), "res_loc.yml");
                warpFile = new File(PLUGIN.getDataFolder(), "warp.yml");
                resLocFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/res_loc.yml"));
                resLocFile.delete();
                break;
            case "1.0":
                config.set("lang", langStr);
                resLocFile = new File(PLUGIN.getDataFolder(), "res_loc.yml");
                warpFile = new File(PLUGIN.getDataFolder(), "warp.yml");
                resLocFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion +"/res_loc.yml"));
                resLocFile.delete();
                break;
            default:
                LoadingConfigFileUtil.lang = loadingLangConfig(langStr);
                Messages.pluginError(Bukkit.getConsoleSender(), "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                return;
        }


        PLUGIN.saveConfig();
        PLUGIN.reloadConfig();
        config = PLUGIN.getConfig();

        if (warp != null){
            try {
                warp.save(warpFile);
            } catch (Exception ignored) {}
        }

        if (spawn != null){
            try {
                spawn.save(spawnFile);
            } catch (Exception ignored) {}
        }

        LoadingConfigFileUtil.lang = loadingLangConfig(langStr);
        Messages.configVersionUpdateSuccess(Bukkit.getConsoleSender());
    }

    public static String formatLangStr(@NotNull String langStr){
        langStr = langStr.toLowerCase();
        if (langStr.length() == 5 && langStr.contains("_")){
            String langStr2 = langStr.substring(0, langStr.indexOf("_"));
            String langStr3 = langStr.substring(langStr.indexOf("_")).toUpperCase();
            return langStr2 + langStr3;
        } else {
            return formatLangStr(config.getString("lang"));
        }
    }

    public static FileConfiguration loadingLangConfig(@NotNull String langStr){
        langFile = new File(PLUGIN.getDataFolder(), "lang/" + langStr + ".yml");
        if (!langFile.exists()) {
            try {
                PLUGIN.saveResource("lang/" + langStr + ".yml", false);
                langFile = new File(PLUGIN.getDataFolder(), "lang/" + langStr + ".yml");
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

    public static FileConfiguration loadingSpawnConfig(){
        String fileName = "spawn.yml";
        spawnFile = new File(PLUGIN.getDataFolder(), fileName);
        return loadingConfig(fileName, spawnFile);
    }

    public static FileConfiguration getPlayerData(String playerName){
        Player player = Bukkit.getPlayer(playerName);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        boolean isOldServer = config.getBoolean("old_server");
        UUID uuid;
        String langStr;
        if (player != null){
            uuid = player.getUniqueId();

        } else {
            uuid = offlinePlayer.getUniqueId();
            langStr = "";
        }
        String fileName = uuid + ".yml";
        File playerDataFile = new File(PLUGIN.getDataFolder(),"playerdata/" + fileName);
        FileConfiguration playerData;

        if (!playerDataFile.exists()){
            if (isOldServer){
                langStr = formatLangStr(config.getString("lang"));
            } else {
                langStr = formatLangStr(player.getLocale());
            }

            try {
                playerDataFile.createNewFile();
            } catch (IOException ignored) {}
            playerData = YamlConfiguration.loadConfiguration(playerDataFile);
            playerData.set("player_name", playerName);
            playerData.set("lang", langStr);
            playerData.set("home_amount", 0);
            saveConfig(Bukkit.getConsoleSender(), playerData, playerDataFile);
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        if (!playerData.getString("player_name").equals(playerName)){
            playerData.set("player_name", playerName);
            saveConfig(Bukkit.getConsoleSender(), playerData, playerDataFile);
        }

        if (isOldServer){
            langStr = formatLangStr(playerData.getString("lang"));
        } else {
            langStr = formatLangStr(player.getLocale());
        }

        if (langStr != null && !langStr.isEmpty() && !formatLangStr(playerData.getString("lang")).equals(langStr)){
            playerData.set("lang", langStr);
            saveConfig(Bukkit.getConsoleSender(), playerData, playerDataFile);
        }
        return playerData;
    }

    public static List<String> getDenysList(@NotNull String playerName) {
        FileConfiguration playerData = getPlayerData(playerName);
        Set<String> denySet = playerData.getKeys(true);
        List<String> denys = new ArrayList<>();
        for (String deny : denySet) {
            if (deny.contains("denys.")) {
                String deny2 = deny.substring(deny.indexOf(".") + 1);
                if (!deny2.contains(".")) denys.add(deny2);
            }
        }
        return denys;
    }

    public static Location getLocation(RequestType REQUEST_TYPE, String playerName, String locationName){
        FileConfiguration config;
        switch (REQUEST_TYPE){
            case WARP:
                config = warp;
                break;
            case SPAWN:
                config = spawn;
                break;
            case BACK:
            case HOME:
            case TPLOGOUT:
                config = getPlayerData(playerName);
                break;
            default:
                Messages.pluginError(Bukkit.getConsoleSender(), "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                return null;
        }
        World world = ErrorCheckUtil.isNull(config.getString(locationName + ".world")) ? null : Bukkit.getWorld(config.getString(locationName + ".world"));
        double x = config.getDouble(locationName + ".x");
        double y = config.getDouble(locationName + ".y");
        double z = config.getDouble(locationName + ".z");
        float pitch = (float) config.getDouble(locationName + ".pitch");
        float yaw = (float) config.getDouble(locationName + ".yaw");
        return ErrorCheckUtil.isNull(world) ? null : new Location(world, x, y, z, yaw, pitch);
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getLang() {
        return lang;
    }

    public static FileConfiguration getLang(@NotNull String langStr) {
        langStr = formatLangStr(langStr);
        if (!langs.containsKey(langStr)) langs.put(langStr, loadingLangConfig(langStr));
        return langs.get(langStr);
    }

    public static FileConfiguration getLang(@NotNull CommandSender executor) {
        if (!(executor instanceof Player)){
            return getLang();
        }
        String langStr;
        boolean isOldServer = config.getBoolean("old_server");
        if (isOldServer){
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
            langStr = config.getString("lang").equals(playerData.getString("lang")) ? config.getString("lang") : playerData.getString("lang");
        } else {
            langStr = ((Player) executor).getLocale();
        }
        return getLang(langStr);
    }

    public static Map<String, FileConfiguration> getLangs() {
        return langs;
    }


    public static FileConfiguration getWarp() {
        return warp;
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


    public static FileConfiguration setConfigString(@NotNull FileConfiguration config, @NotNull File file, @NotNull String target, String str){
        config.set(target, str);
        while (!saveConfig(Bukkit.getConsoleSender(), config, file)) {
            config.set(target, str);
        }
        return config;
    }

    public static FileConfiguration setConfigInteger(@NotNull FileConfiguration config, @NotNull File file, @NotNull String target, int count){
        config.set(target, count);
        while (!saveConfig(Bukkit.getConsoleSender(), config, file)) {
            config.set(target, count);
        }
        return config;
    }

    public static FileConfiguration setConfigLocation(@NotNull FileConfiguration config, @NotNull File file, @NotNull String target, Location location){

        if (ErrorCheckUtil.isNull(location)) {
            config.set(target, null);
            while (!saveConfig(Bukkit.getConsoleSender(), config, file)) {
                config.set(target, null);
            }
            return config;
        }

        config.set(target +".world", location.getWorld().getName());
        config.set(target +".x", location.getX());
        config.set(target +".y", location.getY());
        config.set(target +".z", location.getZ());
        config.set(target +".pitch", location.getPitch());
        config.set(target +".yaw", location.getYaw());
        while (!saveConfig(Bukkit.getConsoleSender(), config, file)) {
            config.set(target +".world", location.getWorld().getName());
            config.set(target +".x", location.getX());
            config.set(target +".y", location.getY());
            config.set(target +".z", location.getZ());
            config.set(target +".pitch", location.getPitch());
            config.set(target +".yaw", location.getYaw());
        }
        return config;
    }

    public static FileConfiguration setPlayerDataString(@NotNull CommandSender sender, @NotNull String target, String str){
        FileConfiguration playerData = getPlayerData(sender.getName());
        File playerDataFile = new File(PLUGIN.getDataFolder(), "playerdata/" + ((Player) sender).getUniqueId() + ".yml");

        return setConfigString(playerData, playerDataFile, target, str);
    }

    public static FileConfiguration setPlayerDataInteger(@NotNull CommandSender sender, @NotNull String target, int count){
        FileConfiguration playerData = getPlayerData(sender.getName());
        File playerDataFile = new File(PLUGIN.getDataFolder(), "playerdata/" + ((Player) sender).getUniqueId() + ".yml");
        return setConfigInteger(playerData, playerDataFile, target, count);
    }

    public static FileConfiguration setPlayerDataLocation(@NotNull CommandSender sender, @NotNull String target, Location location){
        FileConfiguration playerData = getPlayerData(sender.getName());
        File playerDataFile = new File(PLUGIN.getDataFolder(), "playerdata/" + ((Player) sender).getUniqueId() + ".yml");

        return setConfigLocation(playerData, playerDataFile, target, location);
    }

    public static FileConfiguration setWarp(@NotNull String warpName, @NotNull Location location){
        return setConfigLocation(warp, warpFile, warpName, location);
    }

    public static FileConfiguration delWarp(@NotNull String warpName){
        return setConfigLocation(warp, warpFile, warpName, null);
    }

    public static FileConfiguration setDenys(@NotNull CommandSender sender, @NotNull String uuid, @NotNull String playerName){
        FileConfiguration playerData = getPlayerData(sender.getName());

        int denysAmount = playerData.getInt("denys_amount") + 1;

        if (ErrorCheckUtil.isNull(playerData.get("denys." + uuid))) setPlayerDataInteger(sender, "denys_amount", denysAmount);

        return setPlayerDataString(sender, "denys." + uuid, playerName);
    }

    public static FileConfiguration delDenys(@NotNull CommandSender sender, @NotNull String uuid){
        FileConfiguration playerData = getPlayerData(sender.getName());
        int denysAmount = playerData.getInt("denys_amount") - 1;
        setPlayerDataInteger(sender, "denys_amount", denysAmount);
        return setPlayerDataString(sender, "denys." + uuid, null);
    }

    public static FileConfiguration setHome(@NotNull CommandSender sender, @NotNull String homeName, @NotNull Location location){
        FileConfiguration playerData = getPlayerData(sender.getName());

        int homeAmount = playerData.getInt("home_amount") + 1;

        if (ErrorCheckUtil.isNull(playerData.get("homes." + homeName))) setPlayerDataInteger(sender, "home_amount", homeAmount);

        return setPlayerDataLocation(sender, "homes." + homeName, location);
    }

    public static FileConfiguration delHome(@NotNull CommandSender sender, @NotNull String homeName){
        FileConfiguration playerData = getPlayerData(sender.getName());
        int homeAmount = playerData.getInt("home_amount") - 1;
        setPlayerDataInteger(sender, "home_amount", homeAmount);
        return setPlayerDataLocation(sender, "homes." + homeName, null);
    }

    public static FileConfiguration setLastLocation(@NotNull CommandSender sender, @NotNull Location location){
        return setPlayerDataLocation(sender, "last_location", location);
    }

    public static FileConfiguration setLogoutLocation(@NotNull CommandSender sender, @NotNull Location location){
        return setPlayerDataLocation(sender, "logout_location", location);
    }

    public static FileConfiguration setSpawn(@NotNull Location location){
        return setConfigLocation(spawn, spawnFile, "spawn", location);
    }

    public static FileConfiguration delSpawn(){
        return setConfigLocation(spawn, spawnFile, "spawn", null);
    }

    public static FileConfiguration setClientLang(@NotNull CommandSender sender, String langName){
        FileConfiguration playerData = getPlayerData(sender.getName());
        File playerDataFile = new File(PLUGIN.getDataFolder(), "playerdata/" + ((Player) sender).getUniqueId() + ".yml");
        playerData.set("lang", langName);
        while (!saveConfig(Bukkit.getConsoleSender(), playerData, playerDataFile)) {
            playerData.set("lang", langName);
        }
        return playerData;
    }

    public static void reloadALLConfig(@NotNull CommandSender sender){
        init(PLUGIN, VERSION);
        Messages.configReloaded(sender);
    }
}
