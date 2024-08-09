package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Event.PlayerDeathEvent;
import top.craft_hello.tpa.Event.PlayerTeleportEvent;
import top.craft_hello.tpa.command.*;
import top.craft_hello.tpa.tabcomplete.NullList;
import top.craft_hello.tpa.tabcomplete.OnlinePlayers;
import top.craft_hello.tpa.tabcomplete.warpName;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class TPA extends JavaPlugin {

    private final FileConfiguration config = getConfig();
    private final File langFile = new File(getDataFolder(), "lang/" + config.getString("lang") + ".yml");
    private final File warpFile = new File(getDataFolder(), "warp.yml");
    private final FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
    private final FileConfiguration warpConfig = YamlConfiguration.loadConfiguration(warpFile);
    private final String lang = config.getString("lang") == null ? "zh_CN" : config.getString("lang");

    @Override
    public void onEnable() {
        // Plugin startup logic
        HandySchedulerUtil.init(this);
        saveDefaultConfig();
        saveResource("warp.yml", false);
        saveResource("lang/" + lang + ".yml", false);
        reloadConfig();
        try {
            langConfig.load(langFile);
            warpConfig.load(warpFile);
        } catch (IOException | InvalidConfigurationException e) {
            Messages.configNotFound(getServer().getConsoleSender());
        }
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new Tpa());
        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new OnlinePlayers());
        Objects.requireNonNull(this.getCommand("tphere")).setExecutor(new TpHere());
        Objects.requireNonNull(this.getCommand("tphere")).setTabCompleter(new OnlinePlayers());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpAccept());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpDeny());
        Objects.requireNonNull(this.getCommand("tpdeny")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("warp")).setExecutor(new Warp());
        Objects.requireNonNull(this.getCommand("warp")).setTabCompleter(new warpName());
        Objects.requireNonNull(this.getCommand("setwarp")).setExecutor(new SetWarp());
        Objects.requireNonNull(this.getCommand("setwarp")).setTabCompleter(new warpName());
        Objects.requireNonNull(this.getCommand("back")).setExecutor(new Back());
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(),this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportEvent(),this);
        Messages.pluginLoaded(getServer().getConsoleSender());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Messages.pluginUnLoaded(getServer().getConsoleSender());
    }

    public void reloadAllConfig(@NotNull CommandSender sender) {
        boolean isPlayer = sender instanceof Player;
        reloadConfig();
        try {
            config.load(new File(getDataFolder(), "config.yml"));
            langConfig.load(langFile);
            warpConfig.load(warpFile);
        } catch (IOException | InvalidConfigurationException e) {
            saveDefaultConfig();
            saveResource("warp.yml", false);
            saveResource("lang/" + lang + ".yml", false);
            Messages.configNotFound(getServer().getConsoleSender());
            if (isPlayer)
                Messages.configNotFound(sender);
            return;
        }
        Messages.configReloaded(getServer().getConsoleSender());
        if (isPlayer)
            Messages.configReloaded(sender);
    }

    public File getWarpFile() {
        return warpFile;
    }

    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    public FileConfiguration getWarpConfig() {
        return warpConfig;
    }
}
