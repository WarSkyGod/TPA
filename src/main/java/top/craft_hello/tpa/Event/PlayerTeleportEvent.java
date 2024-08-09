package top.craft_hello.tpa.Event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.craft_hello.tpa.Request;

import java.util.Map;

public class PlayerTeleportEvent implements Listener {
    private static final Map<Player, Location> lastLocationMap = Request.lastLocationMap;
    @EventHandler
    public void onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent playerTeleportEvent){
        lastLocationMap.put(playerTeleportEvent.getPlayer(), playerTeleportEvent.getFrom());
    }
}