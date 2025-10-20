package top.craft_hello.tpa.objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import top.craft_hello.tpa.abstracts.Configuration;
import top.craft_hello.tpa.enums.CommandType;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.utils.SendMessageUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.utils.VersionUtil.versionComparison;

public class Config extends Configuration {
    private static volatile Config instance;
    private String defaultLanguageStr;
    private Boolean isOldServer;
    private boolean debug;
    private boolean updateCheck;
    private boolean forceSpawn;
    private boolean enableTitleMessage;
    private boolean enableSound;
    private boolean nonTpaOrTphereDisableCheck;
    private int acceptDelay;
    private boolean enableTeleportDelay;
    private boolean enableCommandDelay;
    private final Map<PermissionType, Integer> TELEPORT_DELAYS = new HashMap<>();
    private final Map<PermissionType, Integer> COMMAND_DELAYS = new HashMap<>();
    private final Map<CommandType, Boolean> ENABLE_COMMANDS = new HashMap<>();
    private final Map<PermissionType, Boolean> ENABLE_PERMISSIONS = new HashMap<>();
    private List<String> rtpDisableWorlds;
    private int rtpLimitX;
    private int rtpLimitZ;
    private final Map<PermissionType, Integer> HOME_AMOUNTS = new HashMap<>();


    private Config() {
        this.configurationFile = new File(PLUGIN.getDataFolder(), "config.yml");
        loadConfiguration(false);
        loadConfiguration();
    }


    public static Config getInstance() {
        if (isNull(instance)) synchronized (Config.class) { if (isNull(instance)) instance = new Config(); }
        return instance;
    }

    private void loadConfiguration() {
        if (updateConfiguration) updateConfiguration();

        defaultLanguageStr = configuration.getString("language");
        if (isNull(defaultLanguageStr)) defaultLanguageStr = "zh_CN";
        defaultLanguageStr = formatLangStr(defaultLanguageStr);
        isOldServer = isOldServer();
        debug = configuration.getBoolean("debug");
        updateCheck = configuration.getBoolean("update_check");
        forceSpawn = configuration.getBoolean("force_spawn");
        enableTitleMessage = !isOldServer && configuration.getBoolean("enable_title_message");
        enableSound = !isOldServer && configuration.getBoolean("enable_sound");

        acceptDelay = configuration.getInt("delay.accept");
        if (acceptDelay < 3) acceptDelay = 3;
        enableTeleportDelay = configuration.getBoolean("delay.enable_teleport");
        enableCommandDelay = configuration.getBoolean("delay.enable_command");
        nonTpaOrTphereDisableCheck = configuration.getBoolean("delay.non_tpa_or_tphere_disable_check");
        TELEPORT_DELAYS.put(PermissionType.DEFAULT, configuration.getInt("delay.default.teleport"));
        COMMAND_DELAYS.put(PermissionType.DEFAULT, configuration.getInt("delay.default.command"));
        TELEPORT_DELAYS.put(PermissionType.VIP, configuration.getInt("delay.vip.teleport"));
        COMMAND_DELAYS.put(PermissionType.VIP, configuration.getInt("delay.vip.command"));
        TELEPORT_DELAYS.put(PermissionType.VIP_PLUS, configuration.getInt("delay.vip+.teleport"));
        COMMAND_DELAYS.put(PermissionType.VIP_PLUS, configuration.getInt("delay.vip+.command"));
        TELEPORT_DELAYS.put(PermissionType.MVP, configuration.getInt("delay.mvp.teleport"));
        COMMAND_DELAYS.put(PermissionType.MVP, configuration.getInt("delay.mvp.command"));
        TELEPORT_DELAYS.put(PermissionType.MVP_PLUS, configuration.getInt("delay.mvp+.teleport"));
        COMMAND_DELAYS.put(PermissionType.MVP_PLUS, configuration.getInt("delay.mvp+.command"));
        TELEPORT_DELAYS.put(PermissionType.MVP_PLUS_PLUS, configuration.getInt("delay.mvp++.teleport"));
        COMMAND_DELAYS.put(PermissionType.MVP_PLUS_PLUS, configuration.getInt("delay.mvp++.command"));
        TELEPORT_DELAYS.put(PermissionType.ADMIN, configuration.getInt("delay.admin.teleport"));
        COMMAND_DELAYS.put(PermissionType.ADMIN, configuration.getInt("delay.admin.command"));

        ENABLE_COMMANDS.put(CommandType.TPA, configuration.getBoolean("tpa.enable"));
        ENABLE_PERMISSIONS.put(PermissionType.TPA, configuration.getBoolean("tpa.permission"));
        ENABLE_COMMANDS.put(CommandType.TP_HERE, configuration.getBoolean("tphere.enable"));
        ENABLE_PERMISSIONS.put(PermissionType.TP_HERE, configuration.getBoolean("tphere.permission"));
        ENABLE_PERMISSIONS.put(PermissionType.DENYS, configuration.getBoolean("denys.permission"));
        ENABLE_COMMANDS.put(CommandType.RTP, configuration.getBoolean("rtp.enable"));
        ENABLE_PERMISSIONS.put(PermissionType.RTP, configuration.getBoolean("rtp.permission"));
        ENABLE_COMMANDS.put(CommandType.WARP, configuration.getBoolean("warp.enable"));
        ENABLE_PERMISSIONS.put(PermissionType.WARP, configuration.getBoolean("warp.permission"));
        ENABLE_COMMANDS.put(CommandType.HOME, configuration.getBoolean("home.enable"));
        ENABLE_PERMISSIONS.put(PermissionType.HOME, configuration.getBoolean("home.permission"));
        ENABLE_COMMANDS.put(CommandType.SPAWN, configuration.getBoolean("spawn.enable"));
        ENABLE_PERMISSIONS.put(PermissionType.SPAWN, configuration.getBoolean("spawn.permission"));
        ENABLE_COMMANDS.put(CommandType.BACK, configuration.getBoolean("back.enable"));
        ENABLE_PERMISSIONS.put(PermissionType.BACK, configuration.getBoolean("back.permission"));

        rtpDisableWorlds = configuration.getStringList("rtp.disable_worlds");
        rtpLimitX = configuration.getInt("rtp.limit.x");
        rtpLimitZ = configuration.getInt("rtp.limit.z");

        HOME_AMOUNTS.put(PermissionType.DEFAULT, configuration.getInt("home.amount.default"));
        HOME_AMOUNTS.put(PermissionType.VIP, configuration.getInt("home.amount.vip"));
        HOME_AMOUNTS.put(PermissionType.VIP_PLUS, configuration.getInt("home.amount.vip+"));
        HOME_AMOUNTS.put(PermissionType.MVP, configuration.getInt("home.amount.mvp"));
        HOME_AMOUNTS.put(PermissionType.MVP_PLUS, configuration.getInt("home.amount.mvp+"));
        HOME_AMOUNTS.put(PermissionType.MVP_PLUS_PLUS, configuration.getInt("home.amount.mvp++"));
        HOME_AMOUNTS.put(PermissionType.ADMIN, configuration.getInt("home.amount.admin"));
    }

    public boolean isOldServer() {
        if (!isNull(isOldServer)) return isOldServer;
        return versionComparison(SERVER_VERSION, "1.12");
    }

    private void updateConfiguration() {
        defaultLanguageStr = configuration.getString("lang");
        if (isNull(defaultLanguageStr)) defaultLanguageStr = "zh_CN";
        defaultLanguageStr = formatLangStr(defaultLanguageStr);
        SendMessageUtil.configVersionUpdate(Bukkit.getConsoleSender());
        isOldServer = isOldServer();
        updateCheck = configuration.getBoolean("update_check");
        forceSpawn = configuration.getBoolean("force_spawn");
        enableTitleMessage = !isOldServer && configuration.getBoolean("enable_title_message");
        enableSound = !isOldServer && configuration.getBoolean("enable_playsound");

        acceptDelay = configuration.getInt("accept_delay");
        if (acceptDelay < 3) acceptDelay = 3;
        TELEPORT_DELAYS.put(PermissionType.DEFAULT, configuration.getInt("teleport_delay"));

        ENABLE_COMMANDS.put(CommandType.TPA, configuration.getBoolean("enable_command.tpa"));
        ENABLE_PERMISSIONS.put(PermissionType.TPA, configuration.getBoolean("enable_permission.tpa"));
        ENABLE_COMMANDS.put(CommandType.TP_HERE, configuration.getBoolean("enable_command.tphere"));
        ENABLE_PERMISSIONS.put(PermissionType.TP_HERE, configuration.getBoolean("enable_permission.tphere"));
        ENABLE_PERMISSIONS.put(PermissionType.DENYS, configuration.getBoolean("enable_permission.denys"));
        ENABLE_COMMANDS.put(CommandType.WARP, configuration.getBoolean("enable_command.warp"));
        ENABLE_PERMISSIONS.put(PermissionType.WARP, configuration.getBoolean("enable_permission.warp"));
        ENABLE_COMMANDS.put(CommandType.HOME, configuration.getBoolean("enable_command.home"));
        ENABLE_PERMISSIONS.put(PermissionType.HOME, configuration.getBoolean("enable_permission.home"));
        ENABLE_COMMANDS.put(CommandType.SPAWN, configuration.getBoolean("enable_command.spawn"));
        ENABLE_PERMISSIONS.put(PermissionType.SPAWN, configuration.getBoolean("enable_permission.spawn"));
        ENABLE_COMMANDS.put(CommandType.BACK, configuration.getBoolean("enable_command.back"));
        ENABLE_PERMISSIONS.put(PermissionType.BACK, configuration.getBoolean("enable_permission.back"));

        HOME_AMOUNTS.put(PermissionType.DEFAULT, configuration.getInt("home_amount.default"));
        HOME_AMOUNTS.put(PermissionType.VIP, configuration.getInt("home_amount.vip"));
        HOME_AMOUNTS.put(PermissionType.VIP_PLUS, configuration.getInt("home_amount.svip"));
        HOME_AMOUNTS.put(PermissionType.ADMIN, configuration.getInt("home_amount.admin"));
        ConfigurationSection keySection;
        ConfigurationSection keySection2;
        switch (configVersion) {
            case "3.2.3":
            case "3.2.2":
            case "3.2.1":
            case "3.2.0":
                configurationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + configurationFile.getName()));
                configuration.set("version", VERSION);
                offUpdateConfiguration();
                break;
            case "3.1.3":
            case "3.1.2":
            case "3.1.1":
            case "3.1.0":
            case "3.0.0":
                configurationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + configurationFile.getName()));
                loadConfiguration(true);
                configuration.set("language", defaultLanguageStr);
                configuration.set("update_check", updateCheck);

                if (!configVersion.equals("3.0.0")) {
                    configuration.set("force_spawn", forceSpawn);
                    configuration.set("enable_title_message", enableTitleMessage);
                    configuration.set("enable_sound", enableSound);
                }
                keySection = configuration.getConfigurationSection("delay");
                if (isNull(keySection)) keySection = configuration.createSection("delay");
                keySection.set("accept", acceptDelay);
                keySection2 = keySection.getConfigurationSection("default");
                if(isNull(keySection2)) keySection2 = keySection.createSection("default");
                keySection2.set("teleport", TELEPORT_DELAYS.get(PermissionType.DEFAULT));

                keySection = configuration.getConfigurationSection("tpa");
                if (isNull(keySection)) keySection = configuration.createSection("tpa");
                keySection.set("enable", ENABLE_COMMANDS.get(CommandType.TPA));
                keySection.set("permission", ENABLE_PERMISSIONS.get(PermissionType.TPA));

                keySection = configuration.getConfigurationSection("tphere");
                if (isNull(keySection)) keySection = configuration.createSection("tphere");
                keySection.set("enable", ENABLE_COMMANDS.get(CommandType.TP_HERE));
                keySection.set("permission", ENABLE_PERMISSIONS.get(PermissionType.TP_HERE));

                keySection = configuration.getConfigurationSection("denys");
                if (isNull(keySection)) keySection = configuration.createSection("denys");
                keySection.set("permission", ENABLE_PERMISSIONS.get(PermissionType.DENYS));

                keySection = configuration.getConfigurationSection("warp");
                if (isNull(keySection)) keySection = configuration.createSection("warp");
                keySection.set("enable", ENABLE_COMMANDS.get(CommandType.WARP));
                keySection.set("permission", ENABLE_PERMISSIONS.get(PermissionType.WARP));

                keySection = configuration.getConfigurationSection("home");
                if (isNull(keySection)) keySection = configuration.createSection("home");
                keySection.set("enable", ENABLE_COMMANDS.get(CommandType.HOME));
                keySection.set("permission", ENABLE_PERMISSIONS.get(PermissionType.HOME));

                keySection = configuration.getConfigurationSection("spawn");
                if (isNull(keySection)) keySection = configuration.createSection("spawn");
                keySection.set("enable", ENABLE_COMMANDS.get(CommandType.SPAWN));
                keySection.set("permission", ENABLE_PERMISSIONS.get(PermissionType.SPAWN));

                keySection = configuration.getConfigurationSection("back");
                if (isNull(keySection)) keySection = configuration.createSection("back");
                keySection.set("enable", ENABLE_COMMANDS.get(CommandType.BACK));
                keySection.set("permission", ENABLE_PERMISSIONS.get(PermissionType.BACK));

                keySection = configuration.getConfigurationSection("home");
                if (isNull(keySection)) keySection = configuration.createSection("home");
                keySection2 = keySection.getConfigurationSection("amount");
                if (isNull(keySection2)) keySection2 = keySection.createSection("amount");
                keySection2.set("default", HOME_AMOUNTS.get(PermissionType.DEFAULT));
                keySection2.set("vip", HOME_AMOUNTS.get(PermissionType.VIP));
                keySection2.set("vip+", HOME_AMOUNTS.get(PermissionType.VIP_PLUS));
                keySection2.set("admin", HOME_AMOUNTS.get(PermissionType.ADMIN));
                break;
            case "2.0.0":
            case "1.3":
            case "1.2":
            case "1.1":
                configurationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + configurationFile.getName()));
                loadConfiguration(true);
                configuration.set("language", defaultLanguageStr);
                keySection = configuration.getConfigurationSection("delay");
                if (isNull(keySection)) keySection = configuration.createSection("delay");
                keySection.set("accept", acceptDelay);
                keySection2 = keySection.getConfigurationSection("default");
                if(isNull(keySection2)) keySection2 = keySection.createSection("default");
                keySection2.set("teleport", TELEPORT_DELAYS.get(PermissionType.DEFAULT));
                break;
            case "1.0":
                configurationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + configurationFile.getName()));
                loadConfiguration(true);
                configuration.set("language", defaultLanguageStr);
                break;
            default:
                configuration.set("version", VERSION);
                offUpdateConfiguration();
        }
        saveConfiguration(null);
    }

    @Override
    public void reloadConfiguration() {
        loadConfiguration(false);
        loadConfiguration();
    }

    public String getDefaultLanguageStr() {
        if (isNull(defaultLanguageStr) || !new File(PLUGIN.getDataFolder(), "language/" + defaultLanguageStr + ".yml").exists()) {
            defaultLanguageStr = "zh_CN";
        }
        return defaultLanguageStr;
    }

    public boolean isUpdateCheck() {
        return updateCheck;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isForceSpawn() {
        return forceSpawn;
    }

    public boolean isEnableCommand(CommandType... commandTypes) {
        for (CommandType commandType : commandTypes) {
            if (!ENABLE_COMMANDS.containsKey(commandType) || ENABLE_COMMANDS.get(commandType)) return true;
        }
        return false;
    }

    public boolean isEnablePermission(PermissionType permissionType) {
        if (ENABLE_PERMISSIONS.containsKey(permissionType)) return ENABLE_PERMISSIONS.get(permissionType);
        return true;
    }

    public boolean isEnableTitleMessage(){
        return enableTitleMessage;
    }

    public boolean isEnableSound(){
        return isEnableTitleMessage() && enableSound;
    }

    public boolean hasPermission(CommandSender sender, PermissionType permissionType) {
        return !isEnablePermission(permissionType) || PermissionType.hasPermission(sender, permissionType) || PermissionType.hasPermission(sender, PermissionType.ADMIN);
    }

    public int getAcceptDelay(){
        return acceptDelay;
    }

    public int getTeleportDelay(CommandSender sender) {
        int teleportDelay = TELEPORT_DELAYS.get(PermissionType.DEFAULT);
        if (hasPermission(sender, PermissionType.VIP)) teleportDelay = TELEPORT_DELAYS.get(PermissionType.VIP);
        if (hasPermission(sender, PermissionType.VIP_PLUS)) teleportDelay = TELEPORT_DELAYS.get(PermissionType.VIP_PLUS);
        if (hasPermission(sender, PermissionType.MVP)) teleportDelay = TELEPORT_DELAYS.get(PermissionType.MVP);
        if (hasPermission(sender, PermissionType.MVP_PLUS)) teleportDelay = TELEPORT_DELAYS.get(PermissionType.MVP_PLUS);
        if (hasPermission(sender, PermissionType.MVP_PLUS_PLUS)) teleportDelay = TELEPORT_DELAYS.get(PermissionType.MVP_PLUS_PLUS);
        if (hasPermission(sender, PermissionType.ADMIN)) teleportDelay = TELEPORT_DELAYS.get(PermissionType.ADMIN);
        return Math.max(teleportDelay, 0);
    }

    public int getCommandDelay(CommandSender sender) {
        int commandDelay = COMMAND_DELAYS.get(PermissionType.DEFAULT);
        if (hasPermission(sender, PermissionType.VIP)) commandDelay = COMMAND_DELAYS.get(PermissionType.VIP);
        if (hasPermission(sender, PermissionType.VIP_PLUS)) commandDelay = COMMAND_DELAYS.get(PermissionType.VIP_PLUS);
        if (hasPermission(sender, PermissionType.MVP)) commandDelay = COMMAND_DELAYS.get(PermissionType.MVP);
        if (hasPermission(sender, PermissionType.MVP_PLUS)) commandDelay = COMMAND_DELAYS.get(PermissionType.MVP_PLUS);
        if (hasPermission(sender, PermissionType.MVP_PLUS_PLUS)) commandDelay = COMMAND_DELAYS.get(PermissionType.MVP_PLUS_PLUS);
        if (hasPermission(sender, PermissionType.ADMIN)) commandDelay = COMMAND_DELAYS.get(PermissionType.ADMIN);
        return Math.max(commandDelay, 0);
    }

    public boolean isEnableTeleportDelay(CommandSender sender) {
        return enableTeleportDelay && !sender.hasPermission("tpa.nodelay") && getTeleportDelay(sender) != 0;
    }

    public boolean isEnableCommandDelay(CommandSender sender){
        return enableCommandDelay && !sender.hasPermission("tpa.nodelay") && getCommandDelay(sender) != 0;
    }

    public boolean isNonTpaOrTphereDisableCheck(){
        return nonTpaOrTphereDisableCheck;
    }

    public int getHomeAmountMax(PermissionType permissionType) {
        return HOME_AMOUNTS.get(permissionType);
    }

    public boolean isRtpDisableWorld(World world){
        for (String worldName : rtpDisableWorlds) if (worldName.equalsIgnoreCase(world.getName())) return true;
        return false;
    }

    public int getRtpLimitX(){
        return rtpLimitX;
    }

    public int getRtpLimitZ(){
        return rtpLimitZ;
    }
}
