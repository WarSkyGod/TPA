package top.craft_hello.tpa.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent playerQuitEvent){
        LoadingConfigFileUtil.setLogoutLocation(playerQuitEvent.getPlayer(), playerQuitEvent.getPlayer().getLocation());
    }
}