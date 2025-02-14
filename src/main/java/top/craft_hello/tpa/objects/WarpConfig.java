package top.craft_hello.tpa.objects;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.craft_hello.tpa.abstracts.Configuration;
import top.craft_hello.tpa.exceptions.ErrorWarpNotFoundException;

import java.io.File;
import java.util.*;

import static java.util.Objects.isNull;

public class WarpConfig extends Configuration {
    private static volatile WarpConfig instance;
    private static final Map<String, Location> LOCATIONS = new HashMap<>();

    private WarpConfig() {
        this.configurationFile = new File(PLUGIN.getDataFolder(), "warp.yml");
        loadConfiguration(false);
        loadConfiguration();
    }

    public static WarpConfig getInstance() {
        if (isNull(instance)) synchronized (WarpConfig.class) { if (isNull(instance)) instance = new WarpConfig(); }
        return instance;
    }

    private void loadConfiguration() {
        if (updateConfiguration) updateConfiguration();

        for (String warpName : configuration.getKeys(false)) {
            Location location = loadLocation(warpName);
            if (isNull(location)) continue;
            LOCATIONS.put(warpName, location);
        }
    }

    private void updateConfiguration() {
        Set<String> warpNames;
        switch (configVersion){
            case "3.0.0":
            case "2.0.0":
            case "1.3":
            case "1.2":
                warpNames = configuration.getKeys(false);
                if (warpNames.isEmpty()) return;
                for (String warpName : warpNames) LOCATIONS.put(warpName, configuration.getLocation(warpName));
                configurationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + configurationFile.getName()));
                break;
            case "1.1":
            case "1.0":
                File resLocFile = new File(PLUGIN.getDataFolder(), "res_loc.yml");
                if (resLocFile.exists()) return;
                FileConfiguration resLoc = YamlConfiguration.loadConfiguration(resLocFile);
                warpNames = resLoc.getKeys(false);
                if (warpNames.isEmpty()) {
                    resLocFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + resLocFile.getName()));
                    resLocFile.delete();
                    return;
                }
                for (String warpName : warpNames) LOCATIONS.put(warpName, configuration.getLocation(warpName));
                resLocFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + resLocFile.getName()));
                resLocFile.delete();
                break;
            default:
                return;
        }
        loadConfiguration(true);
        if (LOCATIONS.isEmpty()) return;
        for (Map.Entry<String, Location> locationMap : LOCATIONS.entrySet()){
            setLocation(locationMap.getKey(), locationMap.getValue());
        }
        saveConfiguration(null);
    }

    @Override
    public void reloadConfiguration() {
        loadConfiguration(false);
        loadConfiguration();
    }

    public boolean containsWarpLocation(String warpName) {
        return LOCATIONS.containsKey(warpName);
    }

    public List<String> getWarpNameList() {
        List<String> locationNameList = new ArrayList<>();
        for (Map.Entry<String, Location> locationMap : LOCATIONS.entrySet()){
            locationNameList.add(locationMap.getKey());
        }
        return locationNameList;
    }

    public Location getWarpLocation(CommandSender sender, String warpName)  {
        if (!containsWarpLocation(warpName)) throw new ErrorWarpNotFoundException(sender, warpName);
        return LOCATIONS.get(warpName);
    }

    public void setWarpLocation(String warpName, Location location) {
        if (isNull(location)) return;
        LOCATIONS.put(warpName, location);
        setLocation(warpName, location);
        saveConfiguration(null);
    }

    public void delWarpLocation(String warpName) {
        if (containsWarpLocation(warpName)) {
            LOCATIONS.remove(warpName);
            configuration.set(warpName, null);
            saveConfiguration(null);
        }
    }
}
