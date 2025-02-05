package top.craft_hello.tpa.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import top.craft_hello.tpa.objects.PlayerDataConfig;

public class TPAPlayerTeleportEvent implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent){
        Player player = playerTeleportEvent.getPlayer();
        Location lastLocation = playerTeleportEvent.getFrom();
        switch (playerTeleportEvent.getCause()){
            case COMMAND:
            case PLUGIN:
            case UNKNOWN:
                PlayerDataConfig.getPlayerData(player).setLastLocation(lastLocation);
                break;
        }
    }
}