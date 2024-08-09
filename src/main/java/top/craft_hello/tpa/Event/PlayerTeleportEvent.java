package top.craft_hello.tpa.Event;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerTeleportEvent implements Listener {
    public static Location lastLocation;
    @EventHandler
    public void onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent playerTeleportEvent){
        lastLocation = playerTeleportEvent.getFrom();
    }
}