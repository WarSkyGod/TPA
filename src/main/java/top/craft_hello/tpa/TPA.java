package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import top.craft_hello.tpa.commands.*;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.events.PlayerDeathEvent;
import top.craft_hello.tpa.events.PlayerJoinEvent;
import top.craft_hello.tpa.events.PlayerTeleportEvent;
import top.craft_hello.tpa.tabcompletes.HomeName;
import top.craft_hello.tpa.tabcompletes.NullList;
import top.craft_hello.tpa.tabcompletes.OnlinePlayers;
import top.craft_hello.tpa.tabcompletes.WarpName;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;
import top.craft_hello.tpa.utils.VersionUtil;

import java.util.Objects;


public final class TPA extends JavaPlugin {

    @Override
    public void onEnable() {
        // 插件加载时执行
        HandySchedulerUtil.init(this);
        LoadingConfigFileUtil.init(this);
        CommandSender sender = getServer().getConsoleSender();
        registerCommands();
        registerEvents();
        Messages.pluginLoaded(this.getPluginMeta().getVersion());
        if (ErrorCheckUtil.version(sender, RequestType.VERSION)){
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
        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new OnlinePlayers());
        Objects.requireNonNull(this.getCommand("tphere")).setExecutor(new TpHere());
        Objects.requireNonNull(this.getCommand("tphere")).setTabCompleter(new OnlinePlayers());
        Objects.requireNonNull(this.getCommand("tpall")).setExecutor(new TpAll());
        Objects.requireNonNull(this.getCommand("tpall")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpAccept());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpDeny());
        Objects.requireNonNull(this.getCommand("tpdeny")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("warp")).setExecutor(new Warp());
        Objects.requireNonNull(this.getCommand("warp")).setTabCompleter(new WarpName());
        Objects.requireNonNull(this.getCommand("setwarp")).setExecutor(new SetWarp());
        Objects.requireNonNull(this.getCommand("setwarp")).setTabCompleter(new WarpName());
        Objects.requireNonNull(this.getCommand("delwarp")).setExecutor(new DelWarp());
        Objects.requireNonNull(this.getCommand("delwarp")).setTabCompleter(new WarpName());
        Objects.requireNonNull(this.getCommand("home")).setExecutor(new Home());
        Objects.requireNonNull(this.getCommand("home")).setTabCompleter(new HomeName());
        Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHome());
        Objects.requireNonNull(this.getCommand("sethome")).setTabCompleter(new HomeName());
        Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DelHome());
        Objects.requireNonNull(this.getCommand("delhome")).setTabCompleter(new HomeName());
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new Spawn());
        Objects.requireNonNull(this.getCommand("spawn")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawn());
        Objects.requireNonNull(this.getCommand("setspawn")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("delspawn")).setExecutor(new DelSpawn());
        Objects.requireNonNull(this.getCommand("delspawn")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("back")).setExecutor(new Back());
        Objects.requireNonNull(this.getCommand("back")).setTabCompleter(new NullList());
    }

    // 注册事件
    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportEvent(), this);
    }
}
