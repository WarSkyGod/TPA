package top.craft_hello.tpa.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import top.craft_hello.tpa.objects.PlayerDataConfig;

public class TPAPlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent){
        Player player = playerQuitEvent.getPlayer();
        Location logoutLocation = player.getLocation();
        PlayerDataConfig playerDataConfig = PlayerDataConfig.getPlayerData(player);
        playerDataConfig.setLogoutLocation(logoutLocation);
        PlayerDataConfig.removePlayerData(player);
    }
}