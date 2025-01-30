package top.craft_hello.tpa.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

public class PlayerTeleportEvent implements Listener {
    @EventHandler
    public void onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent playerTeleportEvent) {
        if (playerTeleportEvent.getCause() == org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND
                || playerTeleportEvent.getCause() == org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN
                || playerTeleportEvent.getCause() == org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            LoadingConfigFileUtil.setLastLocation(playerTeleportEvent.getPlayer(), playerTeleportEvent.getFrom());
        }
    }
}