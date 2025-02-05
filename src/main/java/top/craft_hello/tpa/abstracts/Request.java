package top.craft_hello.tpa.abstracts;

import cn.handyplus.lib.adapter.HandyRunnable;
import cn.handyplus.lib.adapter.HandySchedulerUtil;
import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.interfaces.RequestInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;

public abstract class Request implements RequestInterface {
    protected static Random random = new Random();
    protected HandyRunnable timer;
    protected HandyRunnable countdownMessageTimer;
    protected HandyRunnable checkMoveTimer;
    protected HandyRunnable useCommandTimer;
    protected long delay;
    protected Location location;
    protected final static Map<Player, Request> REQUEST_QUEUE = new HashMap<>();
    protected final static Map<Player, String> COMMAND_DELAY_QUEUE = new HashMap<>();


    protected void setCountdownMessageTimer(@NotNull Player player, @NotNull String target){
        HandyRunnable countdownMessageTimer = new HandyRunnable() {
            long sec = delay;
            @Override
            public void run() {
                try {
                    if (sec > 0){
                        SendMessageUtil.teleportCountdown(player, target, String.valueOf(sec));
                    }
                    if (getConfig().isEnableTitleMessage()){
                        SendMessageUtil.titleCountdownMessage(player, target, String.valueOf(sec));
                        if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                    if (--sec < 0){
                        SendMessageUtil.titleCountdownOverMessage(player, target);
                        this.cancel();
                    }
                } catch (Exception ignored){
                    this.cancel();
                }
            }
        };
        this.countdownMessageTimer = countdownMessageTimer;
        HandySchedulerUtil.runTaskTimerAsynchronously(countdownMessageTimer, 0, 20);
    }

    protected void setCommandTimer(Player player, long commandDelay){
        if (commandDelay == 0L) return;
        HandyRunnable useCommandTimer = new HandyRunnable() {
            long sec = commandDelay;
            @Override
            public void run() {
                try {
                    COMMAND_DELAY_QUEUE.put(player, String.valueOf(--sec));
                    if (sec < 0){
                        COMMAND_DELAY_QUEUE.remove(player);
                        this.cancel();
                    }
                } catch (Exception ignored){
                    COMMAND_DELAY_QUEUE.remove(player);
                    this.cancel();
                }
            }
        };
        this.useCommandTimer = useCommandTimer;
        HandySchedulerUtil.runTaskTimerAsynchronously(useCommandTimer, 0, 20);
    }

    public static void teleport(Player player, Location location) {
        PlayerSchedulerUtil.syncTeleport(player, location);
    }

    public static Map<Player, Request> getRequestQueue() {
        return REQUEST_QUEUE;
    }

    public static void clearRequestQueue() {
        Set<Player> players = REQUEST_QUEUE.keySet();
        for (Player player : players) REQUEST_QUEUE.remove(player);
    }

    public static void clearCommandDelayQueue() {
        Set<Player> players = COMMAND_DELAY_QUEUE.keySet();
        for (Player player : players) COMMAND_DELAY_QUEUE.remove(player);
    }
}
