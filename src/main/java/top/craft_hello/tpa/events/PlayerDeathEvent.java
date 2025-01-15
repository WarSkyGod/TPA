package top.craft_hello.tpa.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

public class PlayerDeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent playerDeathEvent){
        Player player = playerDeathEvent.getEntity();
        Location lastLocation = player.getLocation();
        LoadingConfigFileUtil.setLastLocation(player, lastLocation);
    }
}