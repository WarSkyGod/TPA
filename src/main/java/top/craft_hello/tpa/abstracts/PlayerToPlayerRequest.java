package top.craft_hello.tpa.abstracts;


import cn.handyplus.lib.adapter.HandyRunnable;
import cn.handyplus.lib.adapter.HandySchedulerUtil;
import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.enums.CommandType;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.enums.TimerType;
import top.craft_hello.tpa.exceptions.*;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.objects.PlayerDataConfig;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;


public abstract class PlayerToPlayerRequest extends Request {
    protected Player requestPlayer;
    protected String requestPlayerName;
    protected Player targetPlayer;
    protected String targetPlayerName;

    public PlayerToPlayerRequest(CommandSender requestObject, String[] args, CommandType commandType)  {
        this.delay = getConfig().getAcceptDelay();
        checkError(requestObject, args, commandType);
        this.requestPlayer = (Player) requestObject;
        this.targetPlayer = Bukkit.getPlayerExact(args[args.length - 1]);
    }

    protected void isMove(@NotNull Location lastLocation, @NotNull Player executor, @NotNull Player target){
        if (executor.getLocation().getX() != lastLocation.getX() || executor.getLocation().getY() != lastLocation.getY() || executor.getLocation().getZ() != lastLocation.getZ()){
            REQUEST_QUEUE.remove(executor);
            REQUEST_QUEUE.remove(target);
            timer.cancel();
            checkMoveTimer.cancel();
            countdownMessageTimer.cancel();
            if (getConfig().isEnableTitleMessage()){
                LanguageConfig language = getLanguage(requestPlayer);
                String title = language.getFormatMessage("teleport.canceled.self");
                if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                requestPlayer.sendTitle(title, "");
            }
            SendMessageUtil.move(executor, target);
        }
    }

    protected void setCheckMoveTimer(@NotNull Location lastLocation, @NotNull Player executor, @NotNull Player target){
        HandyRunnable checkMoveTimer = new HandyRunnable() {
            long sec = (delay < 0L ? 60L : delay * 20);
            @Override
            public void run() {
                try {
                    // 执行逻辑
                    isMove(lastLocation, executor, target);
                    if (--sec < 0){
                        this.cancel();
                    }
                } catch (Exception ignored) {
                    this.cancel();
                }
            }
        };
        this.checkMoveTimer = checkMoveTimer;
        HandySchedulerUtil.runTaskTimerAsynchronously(checkMoveTimer, 0, 1);
    }

    protected void teleport()  {
        REQUEST_QUEUE.remove(targetPlayer);
        if (getConfig().isEnableTeleportDelay(requestPlayer)) checkMoveTimer.cancel();
    }

    protected void checkError(CommandSender requestObject, String[] args, CommandType commandType)   {
        String command = commandType == CommandType.TPA ? "tpa" : "tphere";
        PermissionType permissionType = commandType == CommandType.TPA ? PermissionType.TPA : PermissionType.TP_HERE;
        if (!(requestObject instanceof Player)) throw new ErrorConsoleRestrictedException(requestObject);
        requestPlayer = ((Player) requestObject);
        requestPlayerName = requestPlayer.getName();
        if (!getConfig().isEnableCommand(commandType)) throw new ErrorCommandDisabledException(requestPlayer);
        if (!getConfig().hasPermission(requestPlayer, permissionType)) throw new ErrorPermissionDeniedException(requestPlayer);
        if (args.length != 1) throw new ErrorSyntaxTpaException(requestPlayer, command);
        targetPlayerName = args[args.length - 1];
        targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
        if (COMMAND_DELAY_QUEUE.containsKey(requestPlayer)) throw new ErrorCommandCooldownException(requestPlayer, COMMAND_DELAY_QUEUE.get(requestPlayer));
        if (REQUEST_QUEUE.containsKey(requestPlayer) || REQUEST_QUEUE.containsKey(targetPlayer)) throw new ErrorRequestPendingException(requestPlayer);
        if (requestPlayer.equals(targetPlayer)) throw new ErrorSelfOperationException(requestPlayer);
        if (isNull(targetPlayer) || !targetPlayer.isOnline()) throw new ErrorTargetOfflineException(requestPlayer, targetPlayerName);
        if (PlayerDataConfig.getPlayerData(targetPlayer).isDeny(requestPlayer.getUniqueId().toString())) throw new ErrorBlockedByTargetException(requestPlayer);
    }

    protected void setTimer(long delay, TimerType timerType){
        HandyRunnable timer = new HandyRunnable() {
            @Override
            public void run() {
                try {
                    // 执行逻辑
                    switch (timerType){
                        case TELEPORT:
                            teleport();
                            break;
                        case DENY:
                            timeOverDeny();
                            break;
                    }
                } catch (Exception ignored){
                    REQUEST_QUEUE.remove(targetPlayer);
                    this.cancel();
                }
            }
        };
        this.timer = timer;
        HandySchedulerUtil.runTaskLaterAsynchronously(timer, delay / 50L);
    }

    @Override
    public void tpaccept()  {
        REQUEST_QUEUE.remove(targetPlayer);
        timer.cancel();
        delay = getConfig().getTeleportDelay(requestPlayer);
    }

    protected void deny(){
        REQUEST_QUEUE.remove(targetPlayer);
        timer.cancel();
    }

    @Override
    public void tpdeny(){
        deny();
    }

    protected void timeOverDeny(){
        deny();
    }
}
