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
    public static void tp(@NotNull Player executor, @NotNull Location location) {
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
        Location location = LoadingConfigFileUtil.getLocation(RequestType.WARP,null , warpName);
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
        Location location = LoadingConfigFileUtil.getLocation(RequestType.HOME, executor.getName(), "homes." + homeName);
        REQUEST_QUEUE.remove(executor);
        tp((Player) executor, location);
        Messages.tpToHomeMessage(executor, homeName);
    }

    // 传送到主城
    private static void spawn(@NotNull CommandSender executor, @NotNull HandyRunnable timer){
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        timer.cancel();
        Location location = LoadingConfigFileUtil.getLocation(RequestType.SPAWN, null, "spawn");
        REQUEST_QUEUE.remove(executor);
        tp(((Player) executor), location);
        Messages.backSpawnSuccessMessage(executor, "spawn_point");
    }

    // 传送到上一次的位置
    private static void back(@NotNull CommandSender executor, @NotNull HandyRunnable timer){
        Request request = REQUEST_QUEUE.get(executor);
        request.getTimer().cancel();
        timer.cancel();
        Location location = LoadingConfigFileUtil.getLocation(RequestType.BACK, executor.getName(), "last_location");
        REQUEST_QUEUE.remove(executor);
        tp(((Player) executor), location);
        Messages.backLastLocationSuccessMessage(executor, "last_location");
    }

    // 添加一条新的传送请求
    public static void addRequest(@NotNull CommandSender executor, @NotNull String[] args, RequestType REQUEST_TYPE){
        switch (REQUEST_TYPE){
            case TPA:
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
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
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
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
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
                    Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    if (args.length == 0){
                        onlinePlayers.remove(executor);
                        for (Player onlinePlayer : onlinePlayers) {
                            tp(onlinePlayer, ((Player) executor).getLocation());
                            Messages.adminTpYouToMessage(onlinePlayer, executor.getName());
                        }
                        Messages.tpAllCommandSuccess(executor, executor.getName());
                    }

                    if (args.length == 1 && args[args.length - 1].equals("spawn")){
                        Location location = LoadingConfigFileUtil.getLocation(RequestType.SPAWN, null, "spawn");
                        for (Player onlinePlayer : onlinePlayers) {
                            tp(onlinePlayer, location);
                            Messages.adminTpYouToMessage(onlinePlayer, Objects.requireNonNull(LoadingConfigFileUtil.getLang(onlinePlayer).getString("spawn_point")));
                        }
                        Messages.tpAllCommandSuccess(executor, Objects.requireNonNull(LoadingConfigFileUtil.getLang(executor).getString("spawn_point")));
                        return;
                    }

                    if (args.length == 2){
                        switch (args[args.length - 2]){
                            case "player":
                                Player target = Bukkit.getPlayer(args[args.length - 1]);
                                String targetName = target.getName();
                                Location location = target.getLocation();
                                onlinePlayers.remove(target);
                                for (Player onlinePlayer : onlinePlayers) {
                                    tp(onlinePlayer, location);
                                    Messages.adminTpYouToMessage(onlinePlayer, targetName);
                                }
                                Messages.tpAllCommandSuccess(executor, targetName);
                                Messages.adminTpAllPlayerToYouMessage(target);
                                return;
                            case "warp":
                                location = LoadingConfigFileUtil.getLocation(RequestType.WARP, null, args[args.length - 1]);
                                for (Player onlinePlayer : onlinePlayers) {
                                    tp(onlinePlayer, location);
                                    Messages.adminTpYouToMessage(onlinePlayer, args[args.length - 1]);
                                }
                                Messages.tpAllCommandSuccess(executor, args[args.length - 1]);
                                return;
                            default:
                                Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                                return;
                        }
                    }
                }
                return;
            case TPLOGOUT:
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
                    Location location = LoadingConfigFileUtil.getLocation(REQUEST_TYPE, args[args.length - 1], "logout_location");
                    tp((Player) executor, location);
                    Messages.tpLogoutCommandSuccess(executor, args[args.length - 1]);
                }
                return;
            case TPACCEPT:
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
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
                        long sec = teleportDelay * 20;
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, player1, player2);
                                if (--sec < 0){
                                    this.cancel();
                                }
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 0, 1);
                    request.setTimer(timer);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.TELEPORT);
                    Messages.acceptMessage(player1, player2, (teleportDelay < 0L ? 3L : teleportDelay), isTphere);
                }

                return;
            case TPDENY:
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
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
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
                    if (args.length == 0){
                        FileConfiguration warp = LoadingConfigFileUtil.getWarp();
                        List<String> list = new ArrayList<>(warp.getKeys(false));
                        Messages.warpListMessage(executor, list);
                        return;
                    }

                    Player requestPlayer = (Player) executor;
                    String warpName = args[args.length - 1];
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        long sec = teleportDelay * 20;
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                                if (--sec < 0){
                                    this.cancel();
                                }
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 0, 1);
                    Request request = new Request(REQUEST_TYPE, requestPlayer, warpName, timer);
                    REQUEST_QUEUE.put((Player) executor, request);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.WARP_TELEPORT);
                    Messages.teleportCountdown(executor, warpName, (teleportDelay < 0L ? 3L : teleportDelay));
                }
                return;
            case HOME:
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
                    Player requestPlayer = (Player) executor;
                    String homeName;
                    if (args.length == 0){
                        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
                        String defaultHome = playerData.getString("default_home");
                        homeName = defaultHome;
                    } else {
                        homeName = args[args.length - 1];
                    }

                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        @Override
                        public void run() {
                            long sec = teleportDelay * 20;
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                                if (--sec < 0){
                                    this.cancel();
                                }
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 0, 1);
                    Request request = new Request(REQUEST_TYPE, requestPlayer, homeName, timer);
                    REQUEST_QUEUE.put((Player) executor, request);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.HOME_TELEPORT);
                    Messages.teleportCountdown(executor, homeName, (teleportDelay < 0L ? 3L : teleportDelay));
                }
                return;
            case SPAWN:
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    Player requestPlayer = (Player) executor;
                    String spawnPoint = "spawn_point";
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();

                    HandyRunnable timer = new HandyRunnable() {
                        long sec = teleportDelay * 20;
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                                if (--sec < 0){
                                    this.cancel();
                                }
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 0, 1);
                    Request request = new Request(REQUEST_TYPE, requestPlayer, spawnPoint, timer);
                    REQUEST_QUEUE.put((Player) executor, request);
                    setTimer(executor, (teleportDelay < 0L ? 3000L : teleportDelay * 1000L), TimerType.SPAWN_TELEPORT);
                    Messages.teleportCountdown(executor, spawnPoint, (teleportDelay < 0L ? 3L : teleportDelay));
                }
                return;
            case BACK:
                if (ErrorCheckUtil.check(executor, args, REQUEST_TYPE)){
                    FileConfiguration config = LoadingConfigFileUtil.getConfig();
                    Player requestPlayer = (Player) executor;
                    String lastLocation = "last_location";
                    long teleportDelay = config.getLong("teleport_delay");
                    Location location = requestPlayer.getLocation();
                    HandyRunnable timer = new HandyRunnable() {
                        long sec = teleportDelay * 20;
                        @Override
                        public void run() {
                            try {
                                // 执行逻辑
                                isMove(location, requestPlayer, requestPlayer);
                                if (--sec < 0){
                                    this.cancel();
                                }
                            } catch (Exception ignored) {
                                this.cancel();
                            }
                        }
                    };
                    HandySchedulerUtil.runTaskTimerAsynchronously(timer, 0, 1);
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
