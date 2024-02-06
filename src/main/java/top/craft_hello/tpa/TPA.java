package top.craft_hello.tpa;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import top.craft_hello.tpa.Command.*;

import java.util.Objects;

public final class TPA extends JavaPlugin {

    static boolean isPaperServers;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        FileConfiguration config = getPlugin(TPA.class).getConfig();
        saveResource("res_loc.yml", false);
        saveResource("lang/" + config.getString("lang") + ".yml", false);
        saveResource("lang/en_US.yml", false);
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new Tpa());
        Objects.requireNonNull(this.getCommand("tphere")).setExecutor(new TpHere());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpAccept());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpDeny());
        Objects.requireNonNull(this.getCommand("restp")).setExecutor(new ResTp());
        Objects.requireNonNull(this.getCommand("restpset")).setExecutor(new ResTpSet());
        CommandSender sender = this.getServer().getConsoleSender();

        switch (Objects.requireNonNull(config.getString("force_paper_mode"))){
            case "On":
            case "true":
                Messages.pluginLoaded(sender);
                Messages.forcePaperMode(sender);
                isPaperServers = true;
                break;
            case "Off":
            case "false":
                Messages.pluginLoaded(sender);
                Messages.forceBukkitMode(sender);
                isPaperServers = false;
                break;
            case "auto":
            default:
                String[] paperServers = {"Paper", "Mohist", "Purpur", "Leaves", "Folia", "DirtyFolia", "Molia", "Kaiiju", "Luminol"};
                for (String paperServer : paperServers) {
                    if(sender.getServer().getName().equalsIgnoreCase(paperServer)){
                        Messages.pluginLoaded(sender);
                        Messages.autoPaperMode(sender);
                        isPaperServers = true;
                        return;
                    }
                }
                Messages.pluginLoaded(sender);
                Messages.autoBukkitMode(sender);
                isPaperServers = false;
                config.set("force_paper_mode", "auto");
                saveDefaultConfig();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CommandSender sender = this.getServer().getConsoleSender();
        Messages.pluginUnLoaded(sender);
    }
}
