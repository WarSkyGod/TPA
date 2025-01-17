package top.craft_hello.tpa.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.TeleportUtil;
import top.craft_hello.tpa.utils.VersionUtil;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent playerJoinEvent){
        Player player = playerJoinEvent.getPlayer();
        LoadingConfigFileUtil.getPlayerData(player.getName());
        Location location = LoadingConfigFileUtil.getLocation(RequestType.SPAWN, null, "spawn");
        if (LoadingConfigFileUtil.getConfig().getBoolean("force_spawn") && !ErrorCheckUtil.isNull(location)){
            TeleportUtil.tp(player, location);
        }

        if (ErrorCheckUtil.check(player, null, RequestType.VERSION) ){
            VersionUtil.updateCheck(player);
        }
    }
}