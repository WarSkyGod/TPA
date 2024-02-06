package top.craft_hello.tpa;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Request {
    private static Map<Player, List<Player>> tpa = new HashMap<>();
    private Player executor;
    private Player target;
    private boolean commandError;
    private String playerName;
    private String resTpName;
    private String label;
    private boolean offlineOrNull;

    private boolean youToYou;
    private static boolean isTphere;

    private Plugin plugin;

    private long delay;

    public static AsyncScheduler asyncScheduler;

    public static BukkitScheduler scheduler;

    public static File resLocFile = new File(TPA.getPlugin(TPA.class).getDataFolder(), "res_loc.yml");
    public static FileConfiguration resLocConfig = YamlConfiguration.loadConfiguration(resLocFile);

    public Request(CommandSender sender, String label, String[] args) {
        this(sender, label, args, 30000L);
    }
    public Request(CommandSender sender, String label, String[] args, long delay) {
        if (delay <= 0L) {
            delay = 30000L;
        }
        this.executor = (Player) sender;
        if (args != null){
            this.commandError = args.length != 1;

            if (!this.commandError) {
                this.resTpName = args[args.length - 1];
                this.playerName = args[(args.length - 1)];
                this.target = this.executor.getServer().getPlayerExact(this.playerName);
                this.offlineOrNull = this.target == null;
                this.youToYou = this.executor == this.target;
            }
        }
        plugin = TPA.getPlugin(TPA.class);
        this.label = label;
        this.delay = delay;
    }

    private boolean errorCheck() {
        if (TPA.isPaperServers){
            asyncScheduler = this.plugin.getServer().getAsyncScheduler();
        } else {
            scheduler = this.plugin.getServer().getScheduler();
        }

        if (this.label.equals("tpa") || this.label.equals("tpa:tpa") || this.label.equals("tphere") || this.label.equals("tpa:tpahere")) {
            if (this.commandError) {
                Messages.commandError(this.executor, this.label);
                return true;
            }

            if (this.offlineOrNull) {
                Messages.offlineOrNull(this.executor, this.playerName);
                return true;
            }

            if (this.youToYou) {
                Messages.requestYou(this.executor);
                return true;
            }

            List<Player> targetList = tpa.get(this.executor);

            if (tpa.containsKey(this.executor) || tpa.containsKey(this.target)) {
                Messages.requestLock(this.executor);
                return true;
            }

            if (targetList !=  null) {
                Player target = targetList.get(targetList.size() - 1);
                if (tpa.containsKey(target))
                    Messages.requestLock(this.executor);
                return true;
            }
            return false;
        }

        if (this.label.equals("tpaccept") || this.label.equals("tpa:tpaccept")) {
            boolean flag = tpa.containsKey(this.executor);
            List<Player> targetList = tpa.get(this.executor);

            if (!flag) {
                Messages.noRequestAccept(this.executor);
                return true;
            }

            if (targetList == null) {
                Messages.noRequestAccept(this.executor);
                return true;
            }

            Player target = targetList.get(targetList.size() - 1);
            if (!target.isOnline()) {
                Messages.offlineOrNull(this.executor, target.getName());
                tpa.remove(this.executor);
                if (TPA.isPaperServers){
                    asyncScheduler.cancelTasks(this.plugin);
                    return true;
                }
                scheduler.cancelTasks(this.plugin);
                return true;
            }
            this.target = target;
            return false;
        }


        if (this.label.equals("tpdeny") || this.label.equals("tpa:tpdeny")) {
            boolean flag = tpa.containsKey(this.executor);
            List<Player> targetList = tpa.get(this.executor);

            if (!flag) {
                Messages.noRequestDeny(this.executor);
                return true;
            }

            if (targetList == null) {
                Messages.noRequestDeny(this.executor);
                return true;
            }

            Player target = targetList.get(targetList.size() - 1);
            if (!target.isOnline()) {
                Messages.offlineOrNull(this.executor, target.getName());
                tpa.remove(this.executor);
                if (TPA.isPaperServers){
                    asyncScheduler.cancelTasks(this.plugin);
                    return true;
                }
                scheduler.cancelTasks(this.plugin);
                return true;
            }
            this.target = target;
        }

        if (this.label.equals("restp") || this.label.equals("tpa:restp") || this.label.equals("restpset") || this.label.equals("tpa:restpset")) {
            if (this.commandError){
                Messages.resTpCommandError(this.executor, label);
                return true;
            }
            try {
                resLocConfig.load(resLocFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
            if (this.label.equals("restp") | this.label.equals("tpa:restp") && resLocConfig.getLocation(this.resTpName) == null){
                Messages.resNull(this.executor, this.resTpName);
                return true;
            }
        }
        return false;
    }


    private void setTimer(long delay, String TimerName) {
        delay = delay <= 0L ? 3000L : delay;

        if (TPA.isPaperServers){
            asyncScheduler = this.plugin.getServer().getAsyncScheduler();
            asyncScheduler.runDelayed(this.plugin, scheduledTask -> {
                switch (TimerName){
                    case "timeOverResTp":
                        restp(true);
                        break;
                    case "timeOverTp":
                        tpaccept(true);
                        break;
                    case "timeOverDeny":
                        tpdeny(true);
                        break;
                    default:
                        break;
                }
            },delay, TimeUnit.MILLISECONDS);
            return;
        }

        scheduler = this.plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this.plugin, () ->{
            switch (TimerName){
                case "timeOverResTp":
                    restp(true);
                    break;
                case "timeOverTp":
                    tpaccept(true);
                    break;
                case "timeOverDeny":
                    tpdeny(true);
                    break;
                default:
                    break;
            }
        }, delay / 50L, delay / 50L);
    }

    public void tpa() {
        if (this.errorCheck()) {
            return;
        }
        List<Player> executorList = new ArrayList<>();
        executorList.add(this.executor);
        tpa.put(this.target, executorList);
        isTphere = false;
        this.delay = this.delay <= 0L ? 30000L : this.delay;
        Messages.targetToYou(this.executor, this.target, delay / 1000L);
        setTimer(delay, "timeOverDeny");
    }


    public void tphere() {
        if (this.errorCheck()) {
            return;
        }
        List<Player> executorList = new ArrayList<>();
        executorList.add(this.executor);
        tpa.put(this.target, executorList);
        isTphere = true;
        delay = delay <= 0L ? 30000L : delay;
        Messages.youToTarget(this.executor, this.target, delay / 1000L);
        setTimer(delay, "timeOverDeny");
    }

    public void tpaccept(boolean isTimeOverTp) {

        if (this.errorCheck()) {
            return;
        }

        if (TPA.isPaperServers){
            asyncScheduler = this.plugin.getServer().getAsyncScheduler();
            if (!isTimeOverTp) {
                asyncScheduler.cancelTasks(this.plugin);
                long delay = 3000L;
                Messages.acceptMessage(this.executor, this.target, delay / 1000L, isTphere);

                if (isTphere) {
                    Location location = this.executor.getLocation();
                    asyncScheduler.runAtFixedRate(this.plugin, scheduledTask ->
                            isMove(location, this.executor, this.target),1L, 1L, TimeUnit.MICROSECONDS);
                    setTimer(delay, "timeOverTp");
                    return;
                }
                Location location = this.target.getLocation();
                asyncScheduler.runAtFixedRate(this.plugin, scheduledTask ->
                        isMove(location, this.target, this.executor),1L, 1L, TimeUnit.MICROSECONDS);
                setTimer(delay, "timeOverTp");
                return;
            }
            asyncScheduler.cancelTasks(this.plugin);
            tpa.remove(this.executor);
            tpa.remove(this.target);
            this.tpToPlayer(this.target, this.executor, isTphere);
            return;
        }

        if (!isTimeOverTp) {
            scheduler.cancelTasks(this.plugin);
            long delay = 3000L;
            Messages.acceptMessage(this.executor, this.target, delay / 1000L, isTphere);

            if (isTphere) {
                Location location = this.executor.getLocation();
                scheduler.scheduleSyncRepeatingTask(this.plugin, () ->
                        isMove(location, this.executor, this.target), 1L, 1L);
                setTimer(delay, "timeOverTp");
                return;
            }
            Location location = this.target.getLocation();
            scheduler.scheduleSyncRepeatingTask(this.plugin, () ->
                    isMove(location, this.target, this.executor), 1L, 1L);
            setTimer(delay, "timeOverTp");
            return;
        }
        scheduler.cancelTasks(this.plugin);
        tpa.remove(this.executor);
        tpa.remove(this.target);
        this.tpToPlayer(this.target, this.executor, isTphere);
    }

    public void tpdeny(Boolean isTimeOverDeny) {
        if (TPA.isPaperServers){
            asyncScheduler = this.plugin.getServer().getAsyncScheduler();
            if (isTimeOverDeny) {
                Messages.timeOverDeny(this.executor, this.target);
                tpa.remove(this.executor);
                tpa.remove(this.target);
                asyncScheduler.cancelTasks(this.plugin);
                return;
            }

            if (this.errorCheck()) {
                return;
            }

            Messages.deny(this.executor, this.target);
            tpa.remove(this.executor);
            tpa.remove(this.target);
            asyncScheduler.cancelTasks(this.plugin);
            return;
        }


        scheduler = this.plugin.getServer().getScheduler();
        if (isTimeOverDeny) {
            Messages.timeOverDeny(this.executor, this.target);
            tpa.remove(this.executor);
            tpa.remove(this.target);
            scheduler.cancelTasks(this.plugin);
            return;
        }

        if (this.errorCheck()) {
            return;
        }

        Messages.deny(this.executor, this.target);
        tpa.remove(this.executor);
        tpa.remove(this.target);
        scheduler.cancelTasks(this.plugin);
    }

    public void restp(boolean isTimeOverTp) {
        if (this.errorCheck()) {
            return;
        }
        if (TPA.isPaperServers){
            if(!isTimeOverTp){
                Long delay = 3000L;
                Messages.tpTimeMessage(this.executor, this.resTpName, delay / 1000L);

                Location location = this.executor.getLocation();
                asyncScheduler = this.plugin.getServer().getAsyncScheduler();
                asyncScheduler.runAtFixedRate(this.plugin, scheduledTask ->
                       isMove(location, this.executor, this.executor), 1L, 1L, TimeUnit.MILLISECONDS);
                setTimer(delay, "timeOverResTp");
                return;
            }

            asyncScheduler.cancelTasks(this.plugin);
            Location location = resLocConfig.getLocation(this.resTpName);
            this.tp(this.executor, location);
            Messages.resTpMessage(this.executor, this.resTpName);
            return;
        }

        if(!isTimeOverTp){
            Long delay = 3000L;
            Messages.tpTimeMessage(this.executor, this.resTpName, delay / 1000L);

            Location location = this.executor.getLocation();
            scheduler = this.plugin.getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(this.plugin, () ->
                    isMove(location, this.executor, this.executor), 1L, 1L);
            setTimer(delay, "timeOverResTp");
            return;
        }

        scheduler.cancelTasks(this.plugin);
        try {
            resLocConfig.load(resLocFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        Location location = resLocConfig.getLocation(this.resTpName);
        this.tp(this.executor, location);
        Messages.resTpMessage(this.executor, this.resTpName);
    }

    public void restpset(){
        if (this.errorCheck()) {
            return;
        }
        Location loc = this.executor.getLocation();
        resLocConfig.set(this.resTpName, loc);
        try {
            resLocConfig.save(resLocFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Messages.resTpSet(this.executor, this.resTpName);
    }

    public void isMove(Location location, Player executor, Player target) {
        if(TPA.isPaperServers){
            if (executor.getX() != location.getX() || executor.getY() != location.getY() || executor.getZ() != location.getZ()){
                asyncScheduler = this.plugin.getServer().getAsyncScheduler();
                asyncScheduler.cancelTasks(this.plugin);
                Messages.move(executor, target);
                if (executor != target){
                    tpa.remove(this.executor);
                    tpa.remove(this.target);
                    return;
                }
                return;
            }
        }

        if (executor.getLocation().getX() != location.getX() || executor.getLocation().getY() != location.getY() || executor.getLocation().getZ() != location.getZ()){
            scheduler = this.plugin.getServer().getScheduler();
            scheduler.cancelTasks(this.plugin);
            Messages.move(executor, target);
            if (executor != target){
                tpa.remove(this.executor);
                tpa.remove(this.target);
            }
        }
    }

    private void tpToPlayer(Player executor, Player target, boolean tphere){
        if (tphere) {
            this.tp(target, executor.getLocation());
            return;
        }
        this.tp(executor, target.getLocation());
    }

    private void tp(Player executor, Location location) {
        if (TPA.isPaperServers){
            executor.teleportAsync(location);
            return;
        }
        executor.teleport(location);
    }
}
