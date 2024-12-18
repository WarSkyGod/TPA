package top.craft_hello.tpa;

import cn.handyplus.lib.adapter.HandySchedulerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Event.PlayerDeathEvent;
import top.craft_hello.tpa.Event.PlayerTeleportEvent;
import top.craft_hello.tpa.command.*;
import top.craft_hello.tpa.tabcomplete.NullList;
import top.craft_hello.tpa.tabcomplete.OnlinePlayers;
import top.craft_hello.tpa.tabcomplete.warpName;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public final class TPA extends JavaPlugin {
    private FileConfiguration config = getConfig();
    private File langFile = new File(getDataFolder(), "lang/" + this.config.getString("lang") + ".yml");
    private File warpFile = new File(getDataFolder(), "warp.yml");
    private String lang = this.config.getString("lang") == null ? "zh_CN" : this.config.getString("lang");

    @Override
    public void onEnable() {
        // Plugin startup logic
        HandySchedulerUtil.init(this);
        loadAllConfig(this.getServer().getConsoleSender());
        registerCommands();
        registerEvents();
        Messages.pluginLoaded(getServer().getConsoleSender());
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Messages.pluginUnLoaded(getServer().getConsoleSender());
    }

    public void registerCommands(){
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new Tpa());
        Objects.requireNonNull(this.getCommand("tpa")).setTabCompleter(new OnlinePlayers());
        Objects.requireNonNull(this.getCommand("tphere")).setExecutor(new TpHere());
        Objects.requireNonNull(this.getCommand("tphere")).setTabCompleter(new OnlinePlayers());
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpAccept());
        Objects.requireNonNull(this.getCommand("tpaccept")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpDeny());
        Objects.requireNonNull(this.getCommand("tpdeny")).setTabCompleter(new NullList());
        Objects.requireNonNull(this.getCommand("warp")).setExecutor(new Warp());
        Objects.requireNonNull(this.getCommand("warp")).setTabCompleter(new warpName());
        Objects.requireNonNull(this.getCommand("setwarp")).setExecutor(new SetWarp());
        Objects.requireNonNull(this.getCommand("setwarp")).setTabCompleter(new warpName());
        Objects.requireNonNull(this.getCommand("back")).setExecutor(new Back());
    }

    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportEvent(), this);
    }

    public void loadAllConfig(@NotNull CommandSender sender){
        boolean isPlayer = sender instanceof Player;
        saveDefaultConfig();
        reloadConfig();
        loadLangConfig(this.lang);
        if (!this.warpFile.exists()) {
            saveResource("warp.yml", false);
        }
        try {
            getLangConfig().load(this.langFile);
            getWarpConfig().load(this.warpFile);
        } catch (IOException | InvalidConfigurationException e) {
            Messages.configNotFound(getServer().getConsoleSender());
            if (isPlayer) Messages.configNotFound(sender);
            loadAllConfig(sender);
        }
    }

    public FileConfiguration loadLangConfig(String lang){
        String lang2 = lang.substring(0, lang.length() - 2) + lang.substring(lang.length() - 2).toUpperCase();
        File langFile;

        langFile = new File(getDataFolder(), "lang/" + lang2 + ".yml");
        //this.lang = lang2;
        this.langFile = langFile;
        try {
            if (!langFile.exists()) {
                saveResource("lang/" + lang2 + ".yml", false);
            }
        } catch (IllegalArgumentException e){
            langFile =  new File(getDataFolder(), "lang/" + lang + ".yml");
            //this.lang = lang;
            this.langFile = langFile;
            try {
                if (!langFile.exists()) {
                    saveResource("lang/" + lang + ".yml", false);
                }
            } catch (IllegalArgumentException e2){
                langFile =  new File(getDataFolder(), "lang/" + this.lang + ".yml");
                this.langFile = langFile;
                try {
                    if (!langFile.exists()) {
                        saveResource("lang/" + this.lang + ".yml", false);
                    }
                } catch (IllegalArgumentException e3) {
                    Messages.pluginError(getServer().getConsoleSender(), "在尝试使用配置文件中lang设置的语言时出现错误，请检查您是否创建了此语言的yml，正在尝试为您使用插件默认语言（zh_CN）");
                    langFile =  new File(getDataFolder(), "lang/" + "zh_CN" + ".yml");
                    this.lang = "zh_CN";
                    this.langFile = langFile;
                    try {
                        if (!langFile.exists()) {
                            saveResource("lang/" + "zh_CN" + ".yml", false);
                        }
                    } catch (IllegalArgumentException e4) {
                        Messages.pluginError(getServer().getConsoleSender(), "在尝试使用插件默认语言时出现错误，请向在Github向作者反馈问题");
                    }
                }
            }
        }
        return YamlConfiguration.loadConfiguration(langFile);
    }

    public void reloadAllConfig(@NotNull CommandSender sender) {
        boolean isPlayer = sender instanceof Player;
        loadAllConfig(sender);
        registerCommands();
        Messages.configReloaded(getServer().getConsoleSender());

        if (isPlayer){
            Messages.configReloaded(sender);
        }
    }

    public File getLangFile() {
        return this.langFile;
    }

    public File getWarpFile() {
        return this.warpFile;
    }

    public String getLang(){
        return this.lang;
    }

    public FileConfiguration getLangConfig() {
        return YamlConfiguration.loadConfiguration(this.langFile);
    }

    public FileConfiguration getWarpConfig() {
        return YamlConfiguration.loadConfiguration(this.warpFile);
    }

}
