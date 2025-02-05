package top.craft_hello.tpa.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import top.craft_hello.tpa.objects.Config;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.abstracts.Request.teleport;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getSpawnConfig;

public class TPAPlayerRespawnEvent implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent){
        Player player = playerRespawnEvent.getPlayer();
        Config config = getConfig();
        try {
            Location location = getSpawnConfig().getSpawnLocation(null);
            if (config.isForceSpawn() && !isNull(location)) teleport(player, location);
        } catch (Exception exception) {
            if (config.isDebug()) exception.printStackTrace();
        }
    }
}