package top.craft_hello.tpa.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import top.craft_hello.tpa.objects.PlayerDataConfig;


public class TPAPlayerLocaleChangeEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerLocaleChangeEvent playerLocaleChangeEvent){
        Player player = playerLocaleChangeEvent.getPlayer();
        String languageStr = playerLocaleChangeEvent.getLocale();
        PlayerDataConfig playerDataConfig = PlayerDataConfig.getPlayerData(player);
        if (!playerDataConfig.isSetlang()) PlayerDataConfig.getPlayerData(player).setLanguage(languageStr);
    }
}