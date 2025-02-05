package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.craft_hello.tpa.commands.*;
import top.craft_hello.tpa.events.*;
import top.craft_hello.tpa.tabcompleters.*;
import top.craft_hello.tpa.utils.ErrorCheckUtil;
import top.craft_hello.tpa.utils.LoadingConfigUtil;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.utils.VersionUtil;

import java.util.Objects;

import static top.craft_hello.tpa.utils.VersionUtil.getPluginVersion;


public final class TPA extends JavaPlugin {
    private final CommandSender CONSOLE = getServer().getConsoleSender();
    private final PluginManager PLUGIN_MANAGER = getServer().getPluginManager();
    
    @Override
    public void onEnable() {
        HandySchedulerUtil.init(this);
        HandySchedulerUtil.runTaskAsynchronously(() -> {
            // 插件加载时执行
            LoadingConfigUtil.init(this);
            VersionUtil.init(this);
            registerCommands();
            registerEvents();
            SendMessageUtil.pluginLoaded(CONSOLE, getPluginVersion());
            if (LoadingConfigUtil.getConfig().isUpdateCheck()) ErrorCheckUtil.executeCommand(CONSOLE, null, "version");
        });
    }


    @Override
    public void onDisable() {
        // 插件卸载时执行
        SendMessageUtil.pluginUnLoaded(CONSOLE);
    }

    // 注册命令
    public void registerCommands(){
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new Tpa());
        Objects.requireNonNull(this.getCommand("tphere")).setExecutor(new TpHere());
        Objects.requireNonNull(this.getCommand("tpall")).setExecutor(new TpAll());
        Objects.requireNonNull(this.getCommand("rtp")).setExecutor(new Rtp());
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

        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new TpaTabCompleter());
        Objects.requireNonNull(this.getCommand("tphere")).setTabCompleter(new TpHereTabCompleter());
        Objects.requireNonNull(this.getCommand("tpall")).setTabCompleter(new TpAllTabCompleter());
        Objects.requireNonNull(this.getCommand("rtp")).setTabCompleter(new EmptyListTabCompleter());
        Objects.requireNonNull(this.getCommand("tplogout")).setTabCompleter(new TpLogoutTabCompleter());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new EmptyListTabCompleter());
        Objects.requireNonNull(this.getCommand("tpdeny")).setTabCompleter(new EmptyListTabCompleter());
        Objects.requireNonNull(this.getCommand("denys")).setTabCompleter(new DenysTabCompleter());
        Objects.requireNonNull(this.getCommand("warp")).setTabCompleter(new WarpTabCompleter());
        Objects.requireNonNull(this.getCommand("setwarp")).setTabCompleter(new WarpTabCompleter());
        Objects.requireNonNull(this.getCommand("delwarp")).setTabCompleter(new WarpTabCompleter());
        Objects.requireNonNull(this.getCommand("home")).setTabCompleter(new HomeTabCompleter());
        Objects.requireNonNull(this.getCommand("homes")).setTabCompleter(new EmptyListTabCompleter());
        Objects.requireNonNull(this.getCommand("sethome")).setTabCompleter(new HomeTabCompleter());
        Objects.requireNonNull(this.getCommand("setdefaulthome")).setTabCompleter(new SetDefaultHomeTabCompleter());
        Objects.requireNonNull(this.getCommand("delhome")).setTabCompleter(new HomeTabCompleter());
        Objects.requireNonNull(this.getCommand("spawn")).setTabCompleter(new EmptyListTabCompleter());
        Objects.requireNonNull(this.getCommand("setspawn")).setTabCompleter(new EmptyListTabCompleter());
        Objects.requireNonNull(this.getCommand("delspawn")).setTabCompleter(new EmptyListTabCompleter());
        Objects.requireNonNull(this.getCommand("back")).setTabCompleter(new EmptyListTabCompleter());
    }

    // 注册事件
    public void registerEvents(){
        PLUGIN_MANAGER.registerEvents(new TPAPlayerJoinEvent(), this);
        PLUGIN_MANAGER.registerEvents(new TPAPlayerQuitEvent(), this);
        PLUGIN_MANAGER.registerEvents(new TPAPlayerDeathEvent(), this);
        PLUGIN_MANAGER.registerEvents(new TPAPlayerRespawnEvent(), this);
        PLUGIN_MANAGER.registerEvents(new TPAPlayerTeleportEvent(), this);
        if (!LoadingConfigUtil.getConfig().isOldServer()) PLUGIN_MANAGER.registerEvents(new TPAPlayerLocaleChangeEvent(), this);
    }
}
