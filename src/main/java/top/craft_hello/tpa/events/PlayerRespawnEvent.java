package top.craft_hello.tpa.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.TeleportUtil;

public class PlayerRespawnEvent implements Listener {
    @EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent playerRespawnEvent){
        Player player = playerRespawnEvent.getPlayer();
        Location location = LoadingConfigFileUtil.getLocation(RequestType.SPAWN, null, "spawn");
        if (LoadingConfigFileUtil.getConfig().getBoolean("force_spawn") && location != null){
            TeleportUtil.tp(player, location);
        }

    }
}