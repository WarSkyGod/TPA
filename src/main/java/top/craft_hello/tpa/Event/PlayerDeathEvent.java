package top.craft_hello.tpa.Event;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDeathEvent implements Listener {
    public static Location lastLocation;
    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent playerDeathEvent){
        lastLocation = playerDeathEvent.getPlayer().getLocation();
    }
}