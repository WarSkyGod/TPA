package top.craft_hello.tpa.utils;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import top.craft_hello.tpa.abstracts.Configuration;
import top.craft_hello.tpa.abstracts.Request;
import top.craft_hello.tpa.objects.*;

import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;

public class LoadingConfigUtil {
    private static Plugin plugin;
    private static Config config;
    private static WarpConfig warpConfig;
    private static SpawnConfig spawnConfig;

    public static void init(Plugin plugin) {
        LoadingConfigUtil.plugin = plugin;
        Configuration.configVersionCheck();
        LanguageConfig.loadAllLanguage();
        config = Config.getInstance();
        warpConfig = WarpConfig.getInstance();
        spawnConfig = SpawnConfig.getInstance();
        PlayerDataConfig.loadAllPlayerData();
        if (config.isDebug()) Bukkit.getServer().getLogger().warning("[TPA] " + getLanguage().getMessage("debug_mode_on"));
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static Config getConfig() {
        return config;
    }

    public static WarpConfig getWarpConfig() {
        return warpConfig;
    }

    public static SpawnConfig getSpawnConfig() {
        return spawnConfig;
    }

    public static void reloadALLConfig(CommandSender sender) {
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            HandySchedulerUtil.cancelTask();
            Request.clearRequestQueue();
            Request.clearCommandDelayQueue();
            LanguageConfig.reloadAllLanguage();
            warpConfig.reloadConfiguration();
            spawnConfig.reloadConfiguration();
            PlayerDataConfig.reloadAllPlayerData();
            SendMessageUtil.configReloaded(sender);
            if (config.isDebug()) Bukkit.getServer().getLogger().warning("[TPA] " + getLanguage().getMessage("debug_mode_on"));
        });
    }
}
