package top.craft_hello.tpa.abstracts;


import cn.handyplus.lib.adapter.HandyRunnable;
import cn.handyplus.lib.adapter.HandySchedulerUtil;
import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import cn.handyplus.lib.adapter.WorldSchedulerUtil;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.utils.SendMessageUtil;
import top.craft_hello.tpa.enums.CommandType;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.exceptions.*;
import top.craft_hello.tpa.objects.Config;
import top.craft_hello.tpa.objects.LanguageConfig;
import top.craft_hello.tpa.objects.PlayerDataConfig;
import top.craft_hello.tpa.utils.LoadingConfigUtil;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;


public abstract class PlayerToLocationRequest extends Request {
    protected Player requestPlayer;
    protected String requestPlayerName;
    protected String targetName;
    protected CommandType commandType;


    public PlayerToLocationRequest(CommandSender requestObject, String[] args, CommandType commandType)  {
        this.commandType = commandType;
        checkError(requestObject, args);
        if (!getConfig().isEnableTeleportDelay(requestPlayer)) {
            teleport();
            return;
        }
        setCheckMoveTimer(requestPlayer.getLocation());
        setCountdownMessageTimer(requestPlayer, targetName);
        setTimer((delay < 0L ? 3000L : delay * 1000L));
        REQUEST_QUEUE.put(requestPlayer, this);
    }

    protected void checkError() {
        if (isNull(requestPlayer) || !requestPlayer.isOnline()) throw new ErrorTargetOfflineException(requestPlayer, "null");
    }

    protected void checkError(CommandSender requestObject, String[] args)  {
        Config config = LoadingConfigUtil.getConfig();
        String command;
        PlayerDataConfig playerDataConfig;
        switch (commandType) {
            case WARP:
                command = "warp";
                if (!(requestObject instanceof Player)) throw new ErrorConsoleRestrictedException(requestObject);
                requestPlayer = (Player) requestObject;
                requestPlayerName = requestPlayer.getName();
                this.delay = LoadingConfigUtil.getConfig().getTeleportDelay(requestPlayer);
                if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(requestPlayer);
                if (!config.hasPermission(requestPlayer, PermissionType.WARP)) throw new ErrorPermissionDeniedException(requestPlayer);
                if (COMMAND_DELAY_QUEUE.containsKey(requestPlayer)) throw new ErrorCommandCooldownException(requestPlayer, COMMAND_DELAY_QUEUE.get(requestPlayer));
                if (REQUEST_QUEUE.containsKey(requestPlayer)) throw new ErrorRequestPendingException(requestPlayer);
                if (args.length > 1) throw new ErrorSyntaxWarpException(requestPlayer, command);
                targetName = args[args.length - 1];
                if (!LoadingConfigUtil.getWarpConfig().containsWarpLocation(targetName)) throw new ErrorWarpNotFoundException(requestPlayer, targetName);
                location = LoadingConfigUtil.getWarpConfig().getWarpLocation(requestPlayer, targetName);
                break;
            case HOME:
                command = "home";
                if (!(requestObject instanceof Player)) throw new ErrorConsoleRestrictedException(requestObject);
                requestPlayer = (Player) requestObject;
                requestPlayerName = requestPlayer.getName();
                this.delay = LoadingConfigUtil.getConfig().getTeleportDelay(requestPlayer);
                if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(requestPlayer);
                if (!config.hasPermission(requestPlayer, PermissionType.HOME)) throw new ErrorPermissionDeniedException(requestPlayer);
                if (COMMAND_DELAY_QUEUE.containsKey(requestPlayer)) throw new ErrorCommandCooldownException(requestPlayer, COMMAND_DELAY_QUEUE.get(requestPlayer));
                if (REQUEST_QUEUE.containsKey(requestPlayer)) throw new ErrorRequestPendingException(requestPlayer);
                if (args.length > 1) throw new ErrorSyntaxHomeException(requestPlayer, command);
                playerDataConfig = PlayerDataConfig.getPlayerData(requestPlayer);
                if (args.length == 0){
                    location = playerDataConfig.getHomeLocation();
                    targetName = playerDataConfig.getDefaultHomeName();
                    break;
                }
                targetName = args[args.length - 1];
                location = PlayerDataConfig.getPlayerData(requestPlayer).getHomeLocation(targetName);
                break;
            case SPAWN:
                if (!(requestObject instanceof Player)) throw new ErrorConsoleRestrictedException(requestObject);
                requestPlayer = (Player) requestObject;
                requestPlayerName = requestPlayer.getName();
                this.delay = LoadingConfigUtil.getConfig().getTeleportDelay(requestPlayer);
                if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(requestPlayer);
                if (!config.hasPermission(requestPlayer, PermissionType.SPAWN)) throw new ErrorPermissionDeniedException(requestPlayer);
                if (COMMAND_DELAY_QUEUE.containsKey(requestPlayer)) throw new ErrorCommandCooldownException(requestPlayer, COMMAND_DELAY_QUEUE.get(requestPlayer));
                if (REQUEST_QUEUE.containsKey(requestPlayer)) throw new ErrorRequestPendingException(requestPlayer);
                if (!LoadingConfigUtil.getSpawnConfig().containsSpawnLocation()) throw new ErrorSpawnNotSetException(requestPlayer);
                location = LoadingConfigUtil.getSpawnConfig().getSpawnLocation(requestPlayer);
                targetName = "spawn_name";
                break;
            case BACK:
                if (!(requestObject instanceof Player)) throw new ErrorConsoleRestrictedException(requestObject);
                requestPlayer = (Player) requestObject;
                requestPlayerName = requestPlayer.getName();
                this.delay = LoadingConfigUtil.getConfig().getTeleportDelay(requestPlayer);
                if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(requestPlayer);
                if (!config.hasPermission(requestPlayer, PermissionType.BACK)) throw new ErrorPermissionDeniedException(requestPlayer);
                if (COMMAND_DELAY_QUEUE.containsKey(requestPlayer)) throw new ErrorCommandCooldownException(requestPlayer, COMMAND_DELAY_QUEUE.get(requestPlayer));
                if (REQUEST_QUEUE.containsKey(requestPlayer)) throw new ErrorRequestPendingException(requestPlayer);
                location = PlayerDataConfig.getPlayerData(requestPlayer).getLastLocation();
                targetName = "last_location";
                break;
            case RTP:
                random.setSeed(System.currentTimeMillis());
                if (!(requestObject instanceof Player)) throw new ErrorConsoleRestrictedException(requestObject);
                requestPlayer = ((Player) requestObject);
                requestPlayerName = requestPlayer.getName();
                this.delay = LoadingConfigUtil.getConfig().getTeleportDelay(requestPlayer);
                if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(requestPlayer);
                if (!config.hasPermission(requestPlayer, PermissionType.RTP)) throw new ErrorPermissionDeniedException(requestPlayer);
                if (COMMAND_DELAY_QUEUE.containsKey(requestPlayer)) throw new ErrorCommandCooldownException(requestPlayer, COMMAND_DELAY_QUEUE.get(requestPlayer));
                if (REQUEST_QUEUE.containsKey(requestPlayer)) throw new ErrorRequestPendingException(requestPlayer);
                targetName = "rtp_name";
                World world = requestPlayer.getWorld();
                if (config.isRtpDisableWorld(world)) throw new ErrorWorldDisabledException(requestPlayer);
                SendMessageUtil.generateRandomLocationMessage(requestPlayer);
                if (config.isEnableTitleMessage()) {
                    SendMessageUtil.titleGenerateRandomLocationMessage(requestPlayer);
                    if (config.isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                location = requestPlayer.getLocation();
                int limitX = config.getRtpLimitX();
                int limitZ = config.getRtpLimitZ();
                double x = random.nextDouble(location.getX() - limitX,location.getX() + limitX);
                double z = random.nextDouble(location.getZ() - limitZ, location.getZ() + limitZ);
                location.setX(x);
                location.setZ(z);
                WorldSchedulerUtil.getChunkAtAsync(location);
                int y = world.getHighestBlockYAt((int) location.getX(), (int) location.getZ(), HeightMap.WORLD_SURFACE);
                location.setY(y);
                break;
            default:
                throw new ErrorRuntimeException(requestObject, "在 objects.PlayerToLocationRequest : 35行，请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
        }
    }

    protected void setTimer(long delay){
        HandyRunnable timer = new HandyRunnable() {
            @Override
            public void run() {
                try {
                    // 执行逻辑
                    teleport();
                } catch (Exception ignored){
                    REQUEST_QUEUE.remove(requestPlayer);
                    this.cancel();
                }
            }
        };
        this.timer = timer;
        HandySchedulerUtil.runTaskLaterAsynchronously(timer, delay / 50L);
    }

    protected void isMove(@NotNull Location lastLocation){
        if (requestPlayer.getLocation().getX() != lastLocation.getX() || requestPlayer.getLocation().getY() != lastLocation.getY() || requestPlayer.getLocation().getZ() != lastLocation.getZ()){
            REQUEST_QUEUE.remove(requestPlayer);
            timer.cancel();
            checkMoveTimer.cancel();
            countdownMessageTimer.cancel();
            if (LoadingConfigUtil.getConfig().isEnableTitleMessage()){
                LanguageConfig language = LanguageConfig.getLanguage(requestPlayer);
                String title = language.getFormatMessage("teleport.canceled.self");
                if (LoadingConfigUtil.getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                requestPlayer.sendTitle(title, "");
            }
            SendMessageUtil.move(requestPlayer, requestPlayer);
        }
    }

    protected void setCheckMoveTimer(@NotNull Location lastLocation){
        HandyRunnable checkMoveTimer = new HandyRunnable() {
            long sec = delay * 20;
            @Override
            public void run() {
                try {
                    // 执行逻辑
                    isMove(lastLocation);
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
        if (getConfig().isEnableTeleportDelay(requestPlayer)) {
            REQUEST_QUEUE.remove(requestPlayer);
            checkMoveTimer.cancel();
        }
        checkError();
        switch (commandType){
            case WARP:
                if (getConfig().isEnableTitleMessage()) {
                    SendMessageUtil.titleCountdownOverMessage(requestPlayer, targetName);
                    if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                SendMessageUtil.tpToWarpMessage(requestPlayer, targetName);
                if (!getConfig().isNonTpaOrTphereDisableCheck() && getConfig().isEnableCommandDelay(requestPlayer))
                    setCommandTimer(requestPlayer, getConfig().getCommandDelay(requestPlayer));
                break;
            case HOME:
                if (getConfig().isEnableTitleMessage()) {
                    SendMessageUtil.titleCountdownOverMessage(requestPlayer, targetName);
                    if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                SendMessageUtil.tpToHomeMessage(requestPlayer, targetName);
                if (!getConfig().isNonTpaOrTphereDisableCheck() && getConfig().isEnableCommandDelay(requestPlayer))
                    setCommandTimer(requestPlayer, getConfig().getCommandDelay(requestPlayer));
                break;
            case SPAWN:
                if (getConfig().isEnableTitleMessage()) {
                    Bukkit.getConsoleSender().sendMessage(targetName);
                    SendMessageUtil.titleCountdownOverMessage(requestPlayer, targetName);
                    if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                SendMessageUtil.backSpawnSuccessMessage(requestPlayer);
                if (!getConfig().isNonTpaOrTphereDisableCheck() && getConfig().isEnableCommandDelay(requestPlayer))
                    setCommandTimer(requestPlayer, getConfig().getCommandDelay(requestPlayer));
                break;
            case BACK:
                if (getConfig().isEnableTitleMessage()) {
                    SendMessageUtil.titleCountdownOverMessage(requestPlayer, targetName);
                    if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                SendMessageUtil.backLastLocationSuccessMessage(requestPlayer);
                if (!getConfig().isNonTpaOrTphereDisableCheck() && getConfig().isEnableCommandDelay(requestPlayer))
                    setCommandTimer(requestPlayer, getConfig().getCommandDelay(requestPlayer));
                break;
            case RTP:
                HandyRunnable rtpTimer = new HandyRunnable() {
                    long sec = 200;
                    @Override
                    public void run() {
                        try {
                            if (!isNull(location)){
                                teleport(requestPlayer, location);
                                if (getConfig().isEnableTitleMessage()) {
                                    SendMessageUtil.titleCountdownOverMessage(requestPlayer, targetName);
                                    if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(requestPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                }
                                SendMessageUtil.rtpSuccessMessage(requestPlayer);
                                this.cancel();
                            }
                            if (--sec < 0) {
                                throw new RtpFailedException(requestPlayer);
                            }
                        } catch (Exception ignored) {
                            this.cancel();
                        }
                    }
                };
                HandySchedulerUtil.runTaskTimerAsynchronously(rtpTimer, 0, 1);
                if (!getConfig().isNonTpaOrTphereDisableCheck() && getConfig().isEnableCommandDelay(requestPlayer))
                    setCommandTimer(requestPlayer, getConfig().getCommandDelay(requestPlayer));
                return;
        }
        teleport(requestPlayer, location);
    }

    @Override
    public void tpaccept()  {
        throw new ErrorNoPendingRequestException(requestPlayer);
    }

    @Override
    public void tpdeny()  {
        throw new ErrorNoPendingRequestException(requestPlayer);
    }

}
