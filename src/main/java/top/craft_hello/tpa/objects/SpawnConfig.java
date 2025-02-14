package top.craft_hello.tpa.objects;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.Configuration;
import top.craft_hello.tpa.exceptions.ErrorSpawnNotSetException;

import java.io.File;

import static java.util.Objects.isNull;

public class SpawnConfig extends Configuration {
    private static volatile SpawnConfig instance;
    private Location location;

    private SpawnConfig() {
        this.configurationFile = new File(PLUGIN.getDataFolder(), "spawn.yml");
        loadConfiguration(false);
        loadConfiguration();
    }

    public static SpawnConfig getInstance() {
        if (isNull(instance)) synchronized (SpawnConfig.class) { if (isNull(instance)) instance = new SpawnConfig(); }
        return instance;
    }

    private void loadConfiguration() {
        if (updateConfiguration) updateConfiguration();
        location = loadLocation("spawn");
    }

    private void updateConfiguration() {
        if (configVersion.equals("3.0.0")) {
            location = configuration.getLocation("spawn");
            if (isNull(location)) return;
            configurationFile.renameTo(new File(PLUGIN.getDataFolder(), "backup/" + configVersion + "/" + configurationFile.getName()));
            loadConfiguration(true);
            setLocation("spawn", location);
            saveConfiguration(null);
        }
    }

    @Override
    public void reloadConfiguration() {
        loadConfiguration(false);
        loadConfiguration();
    }

    public boolean containsSpawnLocation() {
        return !isNull(location);
    }

    public Location getSpawnLocation(CommandSender sender)  {
        if (!containsSpawnLocation()) throw new ErrorSpawnNotSetException(sender);
        return location;
    }

    public void setSpawnLocation(Location location) {
        if (isNull(location)) return;
        this.location = location;
        setLocation("spawn", location);
        saveConfiguration(null);
    }

    public void delSpawnLocation() {
        if (containsSpawnLocation()) {
            location = null;
            configuration.set("spawn", null);
            saveConfiguration(null);
        }
    }
}
