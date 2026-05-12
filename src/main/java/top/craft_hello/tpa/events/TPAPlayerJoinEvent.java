package top.craft_hello.tpa.events;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.objects.Config;
import top.craft_hello.tpa.objects.PlayerDataConfig;
import top.craft_hello.tpa.utils.ErrorCheckUtil;

import static top.craft_hello.tpa.abstracts.Request.teleport;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getSpawnConfig;

public class TPAPlayerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        PlayerDataConfig playerDataConfig = PlayerDataConfig.getPlayerData(player);
        Config config = getConfig();
        if (!config.isOldServer() && !playerDataConfig.isSetlang()) {
            String lang = playerDataConfig.getLanguageStr();
            String clientLang = player.getLocale();
            if (lang != null && lang.equalsIgnoreCase(clientLang)) {
                playerDataConfig.setLanguage(clientLang);
            }
        }
        try {
            if (config.isForceSpawn()) {
                if (getSpawnConfig() != null) {
                    Location location = getSpawnConfig().getSpawnLocation(null);
                    if (location != null) {
                        teleport(player, location);
                    }
                }
            }
        } catch (Exception exception) {
            if (config.isDebug()) {
                exception.printStackTrace();
            }
        }
        if (config.isUpdateCheck() && config.hasPermission(player, PermissionType.VERSION)) {
            HandySchedulerUtil.runTaskAsynchronously(() -> ErrorCheckUtil.executeCommand(player, null, "version"));
        }
    }
}