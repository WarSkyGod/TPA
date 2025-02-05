package top.craft_hello.tpa.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import top.craft_hello.tpa.objects.PlayerDataConfig;

public class TPAPlayerDeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent){
        Player player = playerDeathEvent.getEntity();
        Location lastLocation = player.getLocation();
        PlayerDataConfig.getPlayerData(player).setLastLocation(lastLocation);
    }
}