package top.craft_hello.tpa.objects;

import cn.handyplus.lib.adapter.HandyRunnable;
import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import top.craft_hello.tpa.abstracts.ErrorException;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.abstracts.Configuration;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.exceptions.*;

import java.io.File;
import java.util.*;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;

public class PlayerDataConfig extends Configuration {
    private static final Map<UUID, PlayerDataConfig> PLAYER_DATAS = new HashMap<>();
    private Player player;
    private String playerName;
    private String defaultHomeName;
    private final Map<String, Location> HOMES = new HashMap<>();
    private List<String> denyList = new ArrayList<>();
    private UUID playerUUID;
    private Location lastLocation;
    private Location logoutLocation;

    public PlayerDataConfig(UUID playerUUID){
        this(new File(PLUGIN.getDataFolder(), "playerdata/" + playerUUID.toString() + ".yml"));
    }

    public PlayerDataConfig(String playerName){
        this(new File(PLUGIN.getDataFolder(), "playerdata/" + Bukkit.getOfflinePlayer(playerName).getUniqueId() + ".yml"), playerName);
    }

    public PlayerDataConfig(File configurationFile) {
        this(configurationFile, null);
    }

    public PlayerDataConfig(File configurationFile, String playerName) {
        this.playerName = playerName;
        if (isNull(configurationFile)) return;
        this.configurationFile = configurationFile;
        String fileName = configurationFile.getName().replace(".yml", "");
        if (!fileName.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) return;
        this.playerUUID = UUID.fromString(fileName);
        if (PlayerDataConfig.containsPlayerData(playerUUID)) return;
        loadConfiguration(false);
        if (isNull(configuration)) return;
        if (updateConfiguration) {
            updateConfiguration();
            return;
        }
        loadConfiguration();
        PLAYER_DATAS.put(playerUUID, this);
    }

    @Override
    protected void loadConfiguration(boolean isReplace){
        if (isReplace || !configurationFile.exists()){
            ErrorException.tryCreateConfiguration(Bukkit.getConsoleSender(), configurationFile);
            configuration = YamlConfiguration.loadConfiguration(configurationFile);
            configuration.set("player_name", Bukkit.getOfflinePlayer(playerUUID).getName());
            configuration.set("language", getConfig().getDefaultLanguageStr());
            saveConfiguration(null);
        }
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    private void loadConfiguration(){
        HandyRunnable loadPlayerTimer = new HandyRunnable() {
            long sec = 200;
            @Override
            public void run() {
                try {
                    player = Bukkit.getPlayer(playerUUID);
                    if (!isNull(player)){
                        this.cancel();
                    }
                    if (--sec < 0) {
                        this.cancel();
                    }
                } catch (Exception ignored) {
                    this.cancel();
                }
            }
        };
        HandySchedulerUtil.runTaskTimerAsynchronously(loadPlayerTimer, 0, 1);
        this.playerName = configuration.getString("player_name");
        this.languageStr = configuration.getString("language");
        defaultHomeName = configuration.getString("default_home");
        Set<String> homeSet = configuration.getKeys(true);
        for (String homeName : homeSet) {
            if (homeName.contains("homes.")) {
                String homeName2 = homeName.substring(homeName.indexOf(".") + 1);
                if (!homeName2.contains(".")) {
                    Location location = loadLocation(homeName);
                    if (isNull(location)) continue;
                    HOMES.put(homeName2, location);
                }
            }
        }
        denyList = configuration.getStringList("deny_list");
        Location location = loadLocation("last_location");
        if (!isNull(location)) lastLocation = location;

        location = loadLocation("logout_location");
        if (!isNull(location)) logoutLocation = location;
    }

    private void updateConfiguration() {
        switch (configVersion){
            case "3.1.3":
            case "3.1.2":
            case "3.1.1":
            case "3.1.0":
                configurationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/playerdata/" + configurationFile.getName()));
                configuration.set("language", configuration.getString("lang"));
                configuration.set("lang", null);
                configuration.set("home_amount", null);
                configuration.set("denys_amount", null);
                if (configuration.contains("denys")) {
                    Set<String> denysSet = configuration.getKeys(true);
                    for (String deny : denysSet) {
                        if (deny.contains("denys.")) {
                            String playerUUID = deny.substring(deny.indexOf(".") + 1);
                            if (!playerUUID.contains(".")) denyList.add(playerUUID);
                        }
                    }
                    configuration.set("deny_list", denyList);
                }
                configuration.set("denys", null);
                break;
            case "3.0.0":
                configuration.set("lang", null);
                configuration.set("language", getConfig().getDefaultLanguageStr());

                File homeFile = new File(PLUGIN.getDataFolder(), "home.yml");
                File oldHomeFile = new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + homeFile.getName());
                if (homeFile.exists()) {
                    homeFile.renameTo(oldHomeFile);
                    homeFile.delete();
                }

                if (oldHomeFile.exists()){
                    FileConfiguration oldHomes = YamlConfiguration.loadConfiguration(oldHomeFile);
                    Set<String> homeSet = oldHomes.getKeys(true);
                    for (String homeName : homeSet) {
                        if (homeName.contains(playerName + ".")) {
                            String homeName2 = homeName.substring(homeName.indexOf(".") + 1);
                            if (!homeName2.contains(".")) {
                                Location location = oldHomes.getLocation(playerName + "." + homeName2);
                                if (isNull(location)) continue;
                                if (isNull(defaultHomeName)) configuration.set("default_home", homeName2);
                                HOMES.put(homeName2, location);
                                loadLocation(homeName);
                            }
                        }
                    }
                    if (HOMES.isEmpty()) return;
                    for (Map.Entry<String, Location> locationMap : HOMES.entrySet()){
                        setLocation("homes." + locationMap.getKey(), locationMap.getValue());
                    }
                }

                File lastLocationFile = new File(PLUGIN.getDataFolder(), "last_location.yml");
                File oldLastLocationFile = new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + lastLocationFile.getName());
                if (lastLocationFile.exists()) {
                    lastLocationFile.renameTo(oldLastLocationFile);
                    lastLocationFile.delete();
                }

                if (oldLastLocationFile.exists()) {
                    FileConfiguration oldLastLocation = YamlConfiguration.loadConfiguration(oldLastLocationFile);
                    if (oldLastLocation.contains(playerName)) {
                        lastLocation = oldLastLocation.getLocation(playerName);
                        if (isNull(lastLocation)) break;
                        setLocation("last_location", lastLocation);
                    }
                }
                break;
            default:
                return;
        }
        saveConfiguration(null);
    }

    @Override
    public void reloadConfiguration(){
        loadConfiguration(false);
        loadConfiguration();
    }

    public static void reloadAllPlayerData(){
        for (String playerUUID : getPlayerUUIDList()) PLAYER_DATAS.get(UUID.fromString(playerUUID)).reloadConfiguration();
    }

    public static void loadAllPlayerData() {
        File oldHomeFile = new File(PLUGIN.getDataFolder(), "home.yml");
        if (oldHomeFile.exists()) {
            FileConfiguration oldHome = YamlConfiguration.loadConfiguration(oldHomeFile);
            Set<String> playerNames = oldHome.getKeys(false);
            for (String playerName : playerNames) getPlayerData(playerName);
        }

        File oldLastLocationFile = new File(PLUGIN.getDataFolder(), "last_location.yml");
        if (oldLastLocationFile.exists()) {
            FileConfiguration oldLastLocation = YamlConfiguration.loadConfiguration(oldLastLocationFile);
            Set<String> playerNames = oldLastLocation.getKeys(false);
            for (String playerName : playerNames) getPlayerData(playerName);
        }

        Configuration.offUpdateConfiguration();

        File playerDataFolder = new File(PLUGIN.getDataFolder(),"/playerdata");
        File[] files = playerDataFolder.listFiles();
        if (!isNull(files)) for (File file : files) new PlayerDataConfig(file);

    }

    public static PlayerDataConfig getPlayerData(UUID playerUUID) {
        if (!containsPlayerData(playerUUID)) new PlayerDataConfig(playerUUID);
        return PLAYER_DATAS.get(playerUUID);
    }

    public static PlayerDataConfig getPlayerData(Player player) {
        UUID playerUUID = player.getUniqueId();
        return getPlayerData(playerUUID);
    }

    public static PlayerDataConfig getPlayerData(OfflinePlayer offlinePlayer) {
        UUID playerUUID = offlinePlayer.getUniqueId();
        return getPlayerData(playerUUID);
    }

    public static PlayerDataConfig getPlayerData(String playerName) {
        UUID playerUUID = Bukkit.getPlayerUniqueId(playerName);
        if (!containsPlayerData(playerUUID)) new PlayerDataConfig(playerName);
        return PLAYER_DATAS.get(playerUUID);
    }

    public static List<String> getPlayerUUIDList(){
        List<String> playerUUIDList = new ArrayList<>();
        for (Map.Entry<UUID, PlayerDataConfig> langMap : PLAYER_DATAS.entrySet()) playerUUIDList.add(langMap.getKey().toString());
        return playerUUIDList;
    }

    public static boolean containsPlayerData(UUID playerUUID) {
        return PLAYER_DATAS.containsKey(playerUUID);
    }

    public static void removePlayerData(UUID playerUUID) {
        PLAYER_DATAS.remove(playerUUID);
    }

    public static void removePlayerData(Player player) {
        UUID playerUUID = player.getUniqueId();
        removePlayerData(playerUUID);
    }

    public static void removePlayerData(OfflinePlayer offlinePlayer) {
        UUID playerUUID = offlinePlayer.getUniqueId();
        removePlayerData(playerUUID);
    }

    public void updatePlayerName(String playerName) {
        if (!this.playerName.equals(playerName)) return;
        configuration.set("playerName", playerName);
        saveConfiguration(null);
        reloadConfiguration();
    }

    public String getLanguageStr() {
        return languageStr;
    }

    public boolean containsDefaultHome(){
        return !isNull(defaultHomeName);
    }

    public String getDefaultHomeName() {
        return defaultHomeName;
    }

    public boolean equalsDefaultHomeName(String homeName) {
        return !isNull(defaultHomeName) && defaultHomeName.equalsIgnoreCase(homeName);
    }

    public boolean containsHomeLocation(String homeName) {
        return HOMES.containsKey(homeName);
    }

    public PermissionType getPermissionType(Player player) {
        if (player.hasPermission("tpa.admin")) return PermissionType.ADMIN;
        if (player.hasPermission("tpa.mvp++")) return PermissionType.MVP_PLUS_PLUS;
        if (player.hasPermission("tpa.mvp+")) return PermissionType.MVP_PLUS;
        if (player.hasPermission("tpa.mvp")) return PermissionType.MVP;
        if (player.hasPermission("tpa.vip+")) return PermissionType.VIP_PLUS;
        if (player.hasPermission("tpa.vip")) return PermissionType.VIP;
        return PermissionType.DEFAULT;
    }

    public void checkHomeAmountIsMax()  {
        if (isNull(player) || !player.isOnline()) throw new OfflineOrNullErrorException(null);
        PermissionType permissionType = getPermissionType(player);
        int homeAmount = HOMES.size();
        int maxHomeAmount = getConfig().getHomeAmountMax(permissionType);
        if (maxHomeAmount < 1) return;
        if (homeAmount >= maxHomeAmount) throw new HomeAmountMaxErrorException(player, maxHomeAmount);
    }

    public Location getHomeLocation()  {
        if (isNull(defaultHomeName)) throw new NotDefaultHomeErrorException(player);
        return getHomeLocation(defaultHomeName);
    }

    public Location getHomeLocation(String homeName)  {
        if (!containsHomeLocation(homeName)) throw new NotHomeErrorException(player, homeName);
        return HOMES.get(homeName);
    }

    public boolean containsLastLocation() {
        return !isNull(lastLocation);
    }

    public Location getLastLocation()  {
        if (!containsLastLocation()) throw new NotLastLocationErrorException(player);
        return lastLocation;
    }

    public boolean containsLogoutLocation() {
        return !isNull(logoutLocation);
    }

    public Location getLogoutLocation(Player executor)  {
        if (!containsLogoutLocation()) throw new NotLogoutLocationErrorException(executor);
        return logoutLocation;
    }

    public boolean isDeny(String playerUUID) {
        return denyList.contains(playerUUID);
    }


    public void checkIsNoDeny(String playerUUID, Player executor)  {
        if (!isDeny(playerUUID)) throw new TargetIsNoDenysErrorException(executor);
    }

    public List<String> getDenyList(CommandSender sender)  {
        if (denyList.isEmpty()) throw new NotAddDenysErrorException(sender);
        return denyList;
    }

    public void addDeny(String playerUUID) {
        if (isDeny(playerUUID)) return;
        denyList.add(playerUUID);
        configuration.set("deny_list", denyList);
        saveConfiguration(null);
    }

    public void delDeny(String playerUUID) {
        if (!isDeny(playerUUID)) return;
        denyList.remove(playerUUID);
        configuration.set("deny_list", denyList);
        if (denyList.isEmpty()) {
            configuration.set("deny_list", null);
        }

        saveConfiguration(null);
    }


    public boolean equalsLanguageStr(String languageStr) {
        if (isNull(languageStr) || isNull(this.languageStr)) return false;
        return this.languageStr.equalsIgnoreCase(languageStr);
    }

    public void setLanguage(String languageStr) {
        if (isNull(languageStr) || equalsLanguageStr(languageStr)) return;
        this.languageStr = formatLangStr(languageStr);
        configuration.set("language", this.languageStr);
        saveConfiguration(null);
        SendMessageUtil.setLangCommandSuccess(player, this.languageStr);
    }

    public void setDefaultHomeName(String homeName)  {
        if (isNull(homeName) || !containsHomeLocation(homeName)) throw new NotHomeErrorException(player, homeName);
        if (homeName.equalsIgnoreCase(defaultHomeName)) throw new IsDefaultHomeErrorException(player, homeName);
        defaultHomeName = homeName;
        SendMessageUtil.setDefaultHomeSuccess(player, homeName);
    }

    public void setHomeLocation(Location location)  {
        String defaultHomeName = "default";
        if (containsDefaultHome()) defaultHomeName = this.defaultHomeName;
        setHomeLocation(defaultHomeName, location);
    }

    public void setHomeLocation(String homeName, Location location)  {
        if (isNull(homeName) || isNull(location)) return;
        if (!containsHomeLocation(homeName)) checkHomeAmountIsMax();
        if (isNull(defaultHomeName)) defaultHomeName = homeName;
        HOMES.put(homeName, location);
        setLocation("homes." + homeName, location);
        saveConfiguration(null);
        SendMessageUtil.setHomeSuccess(player, homeName);
    }

    public void delHomeLocation(String homeName)  {
        if (containsHomeLocation(homeName)) {
            HOMES.remove(homeName);
            defaultHomeName = null;
            configuration.set("homes." + homeName, null);
            if (HOMES.isEmpty()) {
                configuration.set("homes", null);
                configuration.set("default_home", null);
            } else {
                for (Map.Entry<String, Location> homeMap : HOMES.entrySet()) {
                    if (isNull(defaultHomeName)) {
                        defaultHomeName = homeMap.getKey();
                        configuration.set("default_home", defaultHomeName);
                        break;
                    }
                }
            }
            saveConfiguration(null);
            SendMessageUtil.delHomeSuccess(player, homeName);
            return;
        }
        throw new NotHomeErrorException(player, homeName);
    }

    public void setLastLocation(Location location) {
        if (isNull(location)) return;
        lastLocation = location;
        setLocation("last_location", location);
        saveConfiguration(null);
    }

    public void setLogoutLocation(Location location) {
        if (isNull(location)) return;
        logoutLocation = location;
        setLocation("logout_location", location);
        saveConfiguration(null);
    }

    public List<String> getHomeNameList(CommandSender sender)  {
        if (HOMES.isEmpty()) throw new NotSetHomesErrorException(sender);
        List<String> homeNameList = new ArrayList<>();
        for (Map.Entry<String, Location> homeMap : HOMES.entrySet()){
            homeNameList.add(homeMap.getKey());
        }
        return homeNameList;
    }

    public List<String> getHomeNameList()  {
        return getHomeNameList(player);
    }
}
