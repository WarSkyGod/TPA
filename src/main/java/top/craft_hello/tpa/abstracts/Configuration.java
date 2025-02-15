package top.craft_hello.tpa.abstracts;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import top.craft_hello.tpa.objects.Config;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.interfaces.ConfigurationInterface;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getPlugin;
import static top.craft_hello.tpa.utils.VersionUtil.versionComparison;

public abstract class Configuration implements ConfigurationInterface {
    protected static String configVersion;
    protected static boolean updateConfiguration = false;
    protected static final Plugin PLUGIN = getPlugin();
    protected static final String VERSION = PLUGIN.getDescription().getVersion();
    protected String languageStr;
    protected File configurationFile;
    protected FileConfiguration configuration;

    public static String formatLangStr(String languageStr){
        Config config = getConfig();
        if (isNull(config)) {
            if (isNull(languageStr)) languageStr = PLUGIN.getConfig().getString("lang");
            if (isNull(languageStr)) languageStr = PLUGIN.getConfig().getString("language");
            if (isNull(languageStr)) languageStr = "zh_CN";
        } else {
            if (isNull(languageStr)) languageStr = config.getDefaultLanguageStr();
        }

        if (languageStr.matches("^[a-zA-Z]{2}_[a-zA-Z]{2}$")){
            languageStr = languageStr.toLowerCase();
            String langStr2 = languageStr.substring(0, languageStr.indexOf("_"));
            String langStr3 = languageStr.substring(languageStr.indexOf("_")).toUpperCase();
            return langStr2 + langStr3;
        }

        return "zh_CN";
    }

    protected void loadConfiguration(boolean isReplace){
        if (isReplace || !configurationFile.exists()){
            PLUGIN.saveResource(configurationFile.getName(), isReplace);
            configurationFile = new File(configurationFile.getAbsolutePath());
        }
        try {
            Reader reader = new InputStreamReader(Files.newInputStream(configurationFile.toPath()), StandardCharsets.UTF_8);
            configuration = YamlConfiguration.loadConfiguration(reader);
        } catch (Exception ignored){}
    }

    protected void saveConfiguration(CommandSender sender) {
        if (!ErrorException.trySaveConfiguration(sender, configuration, configurationFile)) {
            reloadConfiguration();
        }
    }

    protected Location loadLocation(String index) {
        String worldName = configuration.getString(index + ".world");
        if (isNull(worldName)) return null;
        World world = Bukkit.getWorld(worldName);
        double x = configuration.getDouble(index + ".x");
        double y = configuration.getDouble(index + ".y");
        double z = configuration.getDouble(index + ".z");
        float pitch = (float) configuration.getDouble(index + ".pitch");
        float yaw = (float) configuration.getDouble(index + ".yaw");
        return new Location(world, x, y, z, yaw, pitch);
    }

    protected void setLocation(String index, Location location) {
        if (isNull(location)) return;
        configuration.set(index + ".world", location.getWorld().getName());
        configuration.set(index + ".x", location.getX());
        configuration.set(index + ".y", location.getY());
        configuration.set(index + ".z", location.getZ());
        configuration.set(index + ".pitch", location.getPitch());
        configuration.set(index + ".yaw", location.getYaw());
    }

    public static void configVersionCheck(){
        PLUGIN.reloadConfig();
        configVersion = PLUGIN.getConfig().getString("version");
        configVersion = isNull(configVersion) ? "1.0" : configVersion;
        if (versionComparison(configVersion, VERSION)) onUpdateConfiguration();
    }

    private static void onUpdateConfiguration() {
        if (isNull(configVersion)) return;
        updateConfiguration = true;
    }

    public static void offUpdateConfiguration() {
        if (updateConfiguration) {
            updateConfiguration = false;
            PLUGIN.reloadConfig();
            SendMessageUtil.configVersionUpdateSuccess(Bukkit.getConsoleSender());
        }
    }
}
