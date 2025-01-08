package top.craft_hello.tpa.utils;

import cn.handyplus.lib.adapter.HandyRunnable;
import cn.handyplus.lib.adapter.HandySchedulerUtil;
import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.enums.TimerType;
import top.craft_hello.tpa.objects.Request;

import java.util.*;

public class TeleportUtil {
    // 请求队列
    private static final Map<Player, Request> REQUEST_QUEUE = new HashMap<>();

    // 获取请求队列
    public static Map<Player, Request> getREQUEST_QUEUE(){
        return REQUEST_QUEUE;
    }

    // 传送方法
    private static void tp(@NotNull Player executor, @NotNull Location location) {
        PlayerSchedulerUtil.syncTeleport(executor, location);
    }

    // 检测玩家是否移动，如果移动将取消传送请求
    public static void isMove(@NotNull Location lastLocation, @NotNull Player executor, @NotNull Player target) {
        if (executor.getLocation().getX() != lastLocation.getX() || executor.getLocation().getY() != lastLocation.getY() || executor.getLocation().getZ() != lastLocation.getZ()){
            HandySchedulerUtil.cancelTask();
            Messages.move(executor, target);
            REQUEST_QUEUE.remove(executor);
            REQUEST_QUEUE.remove(target);
        }
    }

    // 计时器
    private static HandyRunnable setTimer(@NotNull CommandSender executor, long delay, TimerType TIMER_TYPE) {
        HandyRunnable timer = new HandyRunnable() {
            @Override
            public void run() {
                try {
                    // 执行逻辑
                    switch (TIMER_TYPE){
                        case TELEPORT:
                            tpAccept(executor, this);
                            break;
                        case WARP_TELEPORT:
                            warp(executor, this);
                            break;
                        case HOME_TELEPORT:
                            home(executor, this);
                            break;
                        case SPAWN_TELEPORT:
                            spawn(executor, this);
                            break;
                        case BACK_TELEPORT:
                            back(executor, this);
                            break;
                        case DENY:
                            tpDeny(executor);
                            break;
                        default:
                            break;
                    }
                } catch (Exception ignored) {
                    this.cancel();
                }
            }
        };
        HandySchedulerUtil.runTaskLaterAsynchronously(timer, delay / 50L);
        return timer;
    }

    // 接受传送
    private static void tpAccept(@NotNull CommandSender executor, @NotNull HandyRunnable timer){
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        timer.cancel();
        Player player1;
        Player player2;
        switch (request.getREQUEST_TYPE()){
            case TPA:
                player1 = request.getRequestPlayer();
                player2 = (Player) executor;
                break;
            case TPHERE:
                player1 = (Player) executor;
                player2 = request.getRequestPlayer();
                break;
            default:
                Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                return;
        }
        REQUEST_QUEUE.remove(executor);
        tp(player1, player2.getLocation());
    }

    // 拒绝传送
    private static void tpDeny(@NotNull CommandSender executor) {
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        Player player1;
        Player player2;
        switch (request.getREQUEST_TYPE()){
            case TPA:
                player1 = request.getRequestPlayer();
                player2 = (Player) executor;
                break;
            case TPHERE:
                player1 = (Player) executor;
                player2 = request.getRequestPlayer();
                break;
            default:
                Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                return;
        }
        REQUEST_QUEUE.remove(executor);
        Messages.timeOverDeny(player1, player2);
    }

    // 传送到传送点
    private static void warp(@NotNull CommandSender executor, @NotNull HandyRunnable timer){
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        timer.cancel();
        String warpName = request.getTarget();
        FileConfiguration warp = LoadingConfigFileUtil.getWarp();
        Location location = warp.getLocation(warpName);
        REQUEST_QUEUE.remove(executor);
        tp((Player) executor, location);
        Messages.tpToWarpMessage(executor, warpName);
    }

    // 传送到家
    private static void home(@NotNull CommandSender executor, @NotNull HandyRunnable timer){
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        timer.cancel();
        String homeName = request.getTarget();
        FileConfiguration home = LoadingConfigFileUtil.getHome();
        Location location = home.getLocation(executor.getName() + "." + homeName);
        REQUEST_QUEUE.remove(executor);
        tp((Player) executor, location);
        Messages.tpToHomeMessage(executor, homeName);
    }

    // 传送到主城
    private static void spawn(@NotNull CommandSender executor, @NotNull HandyRunnable timer){
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        timer.cancel();
        FileConfiguration spawn = LoadingConfigFileUtil.getSpawn();
        Location location = spawn.getLocation("spawn");
        REQUEST_QUEUE.remove(executor);
        tp(((Player) executor), location);
        Messages.backSpawnSuccessMessage(executor, "spawn_point");
    }

    // 传送到上一次的位置
    private static void back(@NotNull CommandSender executor, @NotNull HandyRunnable timer){
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        timer.cancel();
        FileConfiguration lastLocation = LoadingConfigFileUtil.getLastLocation();
        Location location = lastLocation.getLocation(executor.getName());
        REQUEST_QUEUE.remove(executor);
        tp(((Player) executor), location);
        Messages.backLastLocationSuccessMessage(executor, "last_location");
    }

    // 添加一条新的传送请求
    public static void addRequest(@NotNull CommandSender executor, @NotNull String[] args, RequestType REQUEST_TYPE){
        switch (REQUEST_TYPE){
            case TPA:
                if (ErrorCheckUtil.tpa(executor, args, REQUEST_TYPE)){
                    Player requestPlayer = (Player) executor;
                    String target = args[args.length - 1];
                    Player targetPlayer = Bukkit.getPlayerExact(target);
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    long acceptDelay = config.getLong("accept_delay");
                    HandyRunnable timer = setTimer(targetPlayer, (acceptDelay < 0L ? 30000L : acceptDelay * 1000L), TimerType.DENY);
                    Request request = new Request(REQUEST_TYPE, requestPlayer, target, timer);
                    REQUEST_QUEUE.put(targetPlayer, request);
                    Messages.requestTeleportToTarget(executor, targetPlayer, (acceptDelay < 0L ? 30L : acceptDelay));
                }
                return;
            case TPHERE:
                if (ErrorCheckUtil.tpHere(executor, args, REQUEST_TYPE)){
                    Player requestPlayer = (Player) executor;
                    String target = args[args.length - 1];
                    Player targetPlayer = Bukkit.getPlayerExact(target);
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    long acceptDelay = config.getLong("accept_delay");
                    HandyRunnable timer = setTimer(targetPlayer, (acceptDelay < 0L ? 30000L : acceptDelay * 1000L), TimerType.DENY);
                    Request request = new Request(REQUEST_TYPE, requestPlayer, target, timer);
                    REQUEST_QUEUE.put(targetPlayer, request);
                    Messages.requestTargetTeleportToHere(executor, targetPlayer, (acceptDelay < 0L ? 30L : acceptDelay));
                }
                return;
            case TPALL:
                if (ErrorCheckUtil.tpAll(executor, REQUEST_TYPE)){
                    Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    onlinePlayers.remove(executor);
                    for (Player onlinePlayer : onlinePlayers) {
                        tp(onlinePlayer, ((Player) executor).getLocation());
                        Messages.adminTpYouToMessage(onlinePlayer, executor.getName());
                    }
                    Messages.tpAllCommandSuccess(executor, executor.getName());
                }
                return;
            case TPACCEPT:
                if (ErrorCheckUtil.tpAccept(executor, REQUEST_TYPE)){
                    Request request = REQUEST_QUEUE.get(executor);
                    request.getTimer().cancel();
                    Player player1;
                    Player player2;
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    long teleportDelay = config.getLong("teleport_delay");
                    boolean isTphere;
                    switch (request.getREQUEST_TYPE()){
                        case TPA:
                            player1 = request.getRequestPlayer();
                            player2 = (Player) executor;
                            isTphere = false;
                            break;
                        case TPHERE:
                            player1 = (Player) executor;
                            player2 = request.getRequestPlayer();
                            isTphere = true;
                            break;
                        default:
                            Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                            return;
                    }
                    Location location = player1.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, player1, player2);
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 3, (teleportDelay < 0L ? 3L : teleportDelay));
                    request.setTimer(timer);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.TELEPORT);
                    Messages.acceptMessage(player1, player2, (teleportDelay < 0L ? 3L : teleportDelay), isTphere);
                }

                return;
            case TPDENY:
                if (ErrorCheckUtil.tpDeny(executor, REQUEST_TYPE)){
                    Request request = REQUEST_QUEUE.get(executor);
                    request.getTimer().cancel();
                    Player player1;
                    Player player2;
                    boolean isTphere;
                    switch (request.getREQUEST_TYPE()){
                        case TPA:
                            player1 = request.getRequestPlayer();
                            player2 = (Player) executor;
                            isTphere = false;
                            break;
                        case TPHERE:
                            player1 = (Player) executor;
                            player2 = request.getRequestPlayer();
                            isTphere = true;
                            break;
                        default:
                            Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                            return;
                    }
                    REQUEST_QUEUE.remove(executor);
                    Messages.denyMessage(player1, player2, isTphere);
                }

                return;
            case WARP:
                if (ErrorCheckUtil.warp(executor, args, REQUEST_TYPE)){
                    Player requestPlayer = (Player) executor;
                    String warpName = args[args.length - 1];
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 3, (teleportDelay < 0L ? 3L : teleportDelay));
                    Request request = new Request(REQUEST_TYPE, requestPlayer, warpName, timer);
                    REQUEST_QUEUE.put((Player) executor, request);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.WARP_TELEPORT);
                    Messages.teleportCountdown(executor, warpName, (teleportDelay < 0L ? 3L : teleportDelay));
                }
                return;
            case SETWARP:
                if (ErrorCheckUtil.setWarp(executor, args, REQUEST_TYPE)){
                    Location location = ((Player) executor).getLocation();
                    String warpName = args[args.length - 1];
                    LoadingConfigFileUtil.setWarp(executor, warpName, location);
                    Messages.setWarpSuccess(executor, warpName);
                }
                return;
            case DELWARP:
                if (ErrorCheckUtil.delWarp(executor, args, REQUEST_TYPE)){
                    String warpName = args[args.length - 1];
                    LoadingConfigFileUtil.delWarp(executor, warpName);
                    Messages.delWarpSuccess(executor, warpName);
                }
                return;
            case HOME:
                if (ErrorCheckUtil.home(executor, args, REQUEST_TYPE)){
                    Player requestPlayer = (Player) executor;
                    String homeName = args[args.length - 1];
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 3, (teleportDelay < 0L ? 3L : teleportDelay));
                    Request request = new Request(REQUEST_TYPE, requestPlayer, homeName, timer);
                    REQUEST_QUEUE.put((Player) executor, request);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.HOME_TELEPORT);
                    Messages.teleportCountdown(executor, homeName, (teleportDelay < 0L ? 3L : teleportDelay));
                }
                return;
            case SETHOME:
                if (ErrorCheckUtil.setHome(executor, args, REQUEST_TYPE)){
                    Location location = ((Player) executor).getLocation();
                    String homeName = args[args.length - 1];
                    LoadingConfigFileUtil.setHome(executor, homeName, location);
                    Messages.setHomeSuccess(executor, homeName);
                }
                return;
            case DELHOME:
                if (ErrorCheckUtil.delHome(executor, args, REQUEST_TYPE)){
                    String homeName = args[args.length - 1];
                    LoadingConfigFileUtil.delHome(executor, homeName);
                    Messages.delHomeSuccess(executor, homeName);
                }
                return;
            case SPAWN:
                if (ErrorCheckUtil.spawn(executor, REQUEST_TYPE)){
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    Player requestPlayer = (Player) executor;
                    String spawnPoint = "spawn_point";
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 3, (teleportDelay < 0L ? 3L : teleportDelay));
                    Request request = new Request(REQUEST_TYPE, requestPlayer, spawnPoint, timer);
                    REQUEST_QUEUE.put((Player) executor, request);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.SPAWN_TELEPORT);
                    Messages.teleportCountdown(executor, spawnPoint, (teleportDelay < 0L ? 3L : teleportDelay));
                }
                return;
            case SETSPAWN:
                if (ErrorCheckUtil.setSpawn(executor, REQUEST_TYPE)){
                    Location location = ((Player) executor).getLocation();
                    LoadingConfigFileUtil.setSpawn(executor, location);
                    Messages.setSpawnSuccess(executor);
                }
                return;
            case DELSPAWN:
                if (ErrorCheckUtil.delSpawn(executor, REQUEST_TYPE)){
                    LoadingConfigFileUtil.delSpawn(executor);
                    Messages.delSpawnSuccess(executor);
                }
                return;
            case BACK:
                if (ErrorCheckUtil.back(executor, REQUEST_TYPE)){
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    Player requestPlayer = (Player) executor;
                    String lastLocation = "last_location";
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 3, (teleportDelay < 0L ? 3L : teleportDelay));
                    Request request = new Request(REQUEST_TYPE, requestPlayer, lastLocation, timer);
                    REQUEST_QUEUE.put((Player) executor, request);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.BACK_TELEPORT);
                    Messages.teleportCountdown(executor, lastLocation, (teleportDelay < 0L ? 3L : teleportDelay));
                }
                return;
            default:
                Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
        }
    }
}
