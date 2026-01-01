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

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.abstracts.Request.teleport;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getSpawnConfig;

public class TPAPlayerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        PlayerDataConfig playerDataConfig = PlayerDataConfig.getPlayerData(player);
        if (!getConfig().isOldServer() && !playerDataConfig.isSetlang()) {
            String lang = playerDataConfig.getLanguageStr();    
            String clientLang = player.getLocale();             
            if (lang != null && clientLang != null && lang.equalsIgnoreCase(clientLang)) {
                playerDataConfig.setLanguage(clientLang);           
            }
        }
        Config config = getConfig();
        try {
            Location location = getSpawnConfig().getSpawnLocation(null);
            if (config.isForceSpawn() && location != null) {
                teleport(player, location);
            }
        } catch (Exception exception) {
            if (config.isDebug()) {
                exception.printStackTrace();
            }
        }
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            if (config.hasPermission(player, PermissionType.VERSION) && config.isUpdateCheck()) {
                ErrorCheckUtil.executeCommand(player, null, "version");
            }
        });
    }
}
