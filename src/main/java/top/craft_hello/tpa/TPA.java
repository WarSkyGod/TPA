package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import top.craft_hello.tpa.commands.*;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.events.*;
import top.craft_hello.tpa.tabcompletes.*;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.VersionUtil;

import java.util.Objects;


public final class TPA extends JavaPlugin {
    @Override
    public void onEnable() {
        // 插件加载时执行
        HandySchedulerUtil.init(this);
        PluginDescriptionFile pluginDescriptionFile;
        String pluginName;
        String version;

        try {
            pluginDescriptionFile = this.getPluginLoader().getPluginDescription(this.getFile());
            pluginName = pluginDescriptionFile.getName();
            version = pluginDescriptionFile.getVersion();

        } catch (Exception ex) {
            pluginName = "TPA";
            version = "3.1.2";
        }
        LoadingConfigFileUtil.init(this, version);
        VersionUtil.init(pluginName);
        CommandSender sender = getServer().getConsoleSender();
        registerCommands();
        registerEvents();


        Messages.pluginLoaded(version);
        if (ErrorCheckUtil.check(sender, null, RequestType.VERSION)){
            VersionUtil.updateCheck(sender);
        }
    }


    @Override
    public void onDisable() {
        // 插件卸载时执行
        Messages.pluginUnLoaded();
    }

    // 注册命令
    public void registerCommands(){
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new Tpa());
        Objects.requireNonNull(this.getCommand("tphere")).setExecutor(new TpHere());
        Objects.requireNonNull(this.getCommand("tpall")).setExecutor(new TpAll());
        Objects.requireNonNull(this.getCommand("tplogout")).setExecutor(new TpLogout());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpAccept());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpDeny());
        Objects.requireNonNull(this.getCommand("denys")).setExecutor(new Denys());
        Objects.requireNonNull(this.getCommand("warp")).setExecutor(new Warp());
        Objects.requireNonNull(this.getCommand("setwarp")).setExecutor(new SetWarp());
        Objects.requireNonNull(this.getCommand("delwarp")).setExecutor(new DelWarp());
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new Home());
        Objects.requireNonNull(this.getCommand("homes")).setExecutor(new Homes());
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHome());
        Objects.requireNonNull(this.getCommand("setdefaulthome")).setExecutor(new SetDefaultHome());
        Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DelHome());
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new Spawn());
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawn());
        Objects.requireNonNull(this.getCommand("delspawn")).setExecutor(new DelSpawn());
        Objects.requireNonNull(this.getCommand("back")).setExecutor(new Back());

        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new TpaTabCompletes());
        Objects.requireNonNull(this.getCommand("tphere")).setTabCompleter(new TpHereTabCompletes());
        Objects.requireNonNull(this.getCommand("tpall")).setTabCompleter(new TpAllTabCompletes());
        Objects.requireNonNull(this.getCommand("tplogout")).setTabCompleter(new TpLogoutTabCompletes());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new EmptyListTabCompletes());
        Objects.requireNonNull(this.getCommand("tpdeny")).setTabCompleter(new EmptyListTabCompletes());
        Objects.requireNonNull(this.getCommand("denys")).setTabCompleter(new DenysTabCompletes());
        Objects.requireNonNull(this.getCommand("warp")).setTabCompleter(new WarpTabCompletes());
        Objects.requireNonNull(this.getCommand("setwarp")).setTabCompleter(new WarpTabCompletes());
        Objects.requireNonNull(this.getCommand("delwarp")).setTabCompleter(new WarpTabCompletes());
        Objects.requireNonNull(this.getCommand("home")).setTabCompleter(new HomeTabCompletes());
        Objects.requireNonNull(this.getCommand("homes")).setTabCompleter(new EmptyListTabCompletes());
        Objects.requireNonNull(this.getCommand("sethome")).setTabCompleter(new HomeTabCompletes());
        Objects.requireNonNull(this.getCommand("setdefaulthome")).setTabCompleter(new SerDefaultHomeTabCompletes());
        Objects.requireNonNull(this.getCommand("delhome")).setTabCompleter(new HomeTabCompletes());
        Objects.requireNonNull(this.getCommand("spawn")).setTabCompleter(new EmptyListTabCompletes());
        Objects.requireNonNull(this.getCommand("setspawn")).setTabCompleter(new EmptyListTabCompletes());
        Objects.requireNonNull(this.getCommand("delspawn")).setTabCompleter(new EmptyListTabCompletes());
        Objects.requireNonNull(this.getCommand("back")).setTabCompleter(new EmptyListTabCompletes());
    }

    // 注册事件
    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportEvent(), this);
    }
}
