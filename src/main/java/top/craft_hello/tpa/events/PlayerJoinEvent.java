package top.craft_hello.tpa.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.VersionUtil;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent playerJoinEvent){
        if (ErrorCheckUtil.version(playerJoinEvent.getPlayer(), RequestType.VERSION) ){
            VersionUtil.updateCheck(playerJoinEvent.getPlayer());
        }
    }
}