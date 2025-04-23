package top.craft_hello.tpa.utils;

import cn.handyplus.lib.adapter.PlayerSchedulerUtil;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.craft_hello.tpa.abstracts.Request;
import top.craft_hello.tpa.enums.CommandType;
import top.craft_hello.tpa.enums.PermissionType;
import top.craft_hello.tpa.exceptions.*;
import top.craft_hello.tpa.objects.*;

import java.util.*;

import static java.util.Objects.isNull;
import static top.craft_hello.tpa.abstracts.Request.teleport;
import static top.craft_hello.tpa.objects.LanguageConfig.getLanguage;
import static top.craft_hello.tpa.utils.LoadingConfigUtil.getConfig;


public class ErrorCheckUtil{
    public static void executeCommand(CommandSender sender, String[] args, String command) {
        final Map<Player, Request> REQUEST_QUEUE = Request.getRequestQueue();
        CommandType commandType = CommandType.getCommandType(command);
        Config config = LoadingConfigUtil.getConfig();
        Player executorPlayer;
        String executorPlayerName;
        Player targetPlayer;
        String targetPlayerName;
        String targetName;
        Request request;
        PlayerDataConfig playerDataConfig;
        Location location;
        try {
            switch (commandType){
                case TPA:
                    new TpaRequest(sender, args);
                    break;
                case TP_HERE:
                    new TphereRequest(sender, args);
                    break;
                case RTP:
                    new RtpRequest(sender, args);
                    break;
                case TP_ALL:
                    if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(sender);
                    if (!config.hasPermission(sender, PermissionType.TP_ALL)) throw new ErrorPermissionDeniedException(sender);
                    Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                    if (args.length == 0){
                        if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                        executorPlayer = (Player) sender;
                        executorPlayerName = executorPlayer.getName();
                        onlinePlayers.remove(executorPlayer);
                        if (onlinePlayers.isEmpty()) throw new ErrorNoOnlinePlayersException(executorPlayer);
                        location = executorPlayer.getLocation();
                        for (Player onlinePlayer : onlinePlayers) {
                            teleport(onlinePlayer, location);
                            if (getConfig().isEnableTitleMessage()) {
                                SendMessageUtil.titleCountdownOverMessage(onlinePlayer, executorPlayerName);
                                if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(onlinePlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                            }
                            SendMessageUtil.adminTpYouToMessage(onlinePlayer, executorPlayerName);
                        }
                        SendMessageUtil.tpAllCommandSuccess(executorPlayer, executorPlayerName);
                        return;
                    }

                    if (args.length == 1 && args[args.length - 1].equals("spawn")){
                        location = LoadingConfigUtil.getSpawnConfig().getSpawnLocation(sender);
                        for (Player onlinePlayer : onlinePlayers) {
                            teleport(onlinePlayer, location);
                            if (getConfig().isEnableTitleMessage()) {
                                SendMessageUtil.titleCountdownOverMessage(onlinePlayer, "spawn");
                                if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(onlinePlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                            }
                            SendMessageUtil.adminTpYouToMessage(onlinePlayer, getLanguage(onlinePlayer).getMessage("spawn"));
                        }
                        SendMessageUtil.tpAllCommandSuccess(sender, getLanguage(sender).getMessage("spawn"));
                        return;
                    }

                    if (args.length == 2 && !isNull(args[args.length - 2])){
                        switch (args[args.length - 2].toLowerCase()){
                            case "player":
                                targetPlayerName = args[args.length - 1];
                                targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
                                if (isNull(targetPlayer) || !targetPlayer.isOnline()) throw new ErrorTargetOfflineException(sender, targetPlayerName);
                                location = targetPlayer.getLocation();
                                onlinePlayers.remove(targetPlayer);
                                for (Player onlinePlayer : onlinePlayers) {
                                    teleport(onlinePlayer, location);
                                    if (getConfig().isEnableTitleMessage()) {
                                        SendMessageUtil.titleCountdownOverMessage(onlinePlayer, targetPlayerName);
                                        if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(onlinePlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    }
                                    SendMessageUtil.adminTpYouToMessage(onlinePlayer, targetPlayerName);
                                }
                                SendMessageUtil.tpAllCommandSuccess(sender, targetPlayerName);
                                SendMessageUtil.adminTpAllPlayerToYouMessage(targetPlayer);
                                return;
                            case "warp":
                                targetName = args[args.length - 1];
                                location = LoadingConfigUtil.getWarpConfig().getWarpLocation(sender, targetName);
                                for (Player onlinePlayer : onlinePlayers) {
                                    teleport(onlinePlayer, location);
                                    if (getConfig().isEnableTitleMessage()) {
                                        SendMessageUtil.titleCountdownOverMessage(onlinePlayer, targetName);
                                        if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(onlinePlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                    }
                                    SendMessageUtil.adminTpYouToMessage(onlinePlayer, targetName);
                                }
                                SendMessageUtil.tpAllCommandSuccess(sender, targetName);
                                return;
                        }
                    }

                    throw new ErrorSyntaxTpAllException(sender, command);
                case TP_LOGOUT:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.TP_LOGOUT)) throw new ErrorPermissionDeniedException(executorPlayer);
                    if (args.length != 1) throw new ErrorSyntaxTpaException(executorPlayer, command);
                    targetPlayerName = args[args.length - 1];
                    location = PlayerDataConfig.getPlayerData(Bukkit.getOfflinePlayer(targetPlayerName)).getLogoutLocation(executorPlayer);
                    teleport(executorPlayer, location);
                    if (getConfig().isEnableTitleMessage()) {
                        SendMessageUtil.titleCountdownOverMessage(executorPlayer, targetPlayerName);
                        if (getConfig().isEnableSound()) PlayerSchedulerUtil.playSound(executorPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                    SendMessageUtil.tpLogoutCommandSuccess(executorPlayer, targetPlayerName);
                    break;
                case TP_ACCEPT:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.TPA, CommandType.TP_HERE)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!REQUEST_QUEUE.containsKey(executorPlayer)) throw new ErrorNoPendingRequestException(executorPlayer);
                    request = REQUEST_QUEUE.get(executorPlayer);
                    request.tpaccept();
                    break;
                case TP_DENY:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.TPA, CommandType.TP_HERE)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!REQUEST_QUEUE.containsKey(executorPlayer)) throw new ErrorNoPendingRequestException(executorPlayer);
                    request = REQUEST_QUEUE.get(executorPlayer);
                    request.tpdeny();
                    break;
                case DENYS:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.TPA, CommandType.TP_HERE)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.DENYS)) throw new ErrorPermissionDeniedException(executorPlayer);
                    playerDataConfig = PlayerDataConfig.getPlayerData(executorPlayer);
                    if (args.length == 0) {
                        SendMessageUtil.denysMessage(executorPlayer, playerDataConfig.getDenyList(executorPlayer));
                        return;
                    }

                    if (args.length == 2) {
                        targetPlayerName = args[args.length - 1];
                        OfflinePlayer target = Bukkit.getOfflinePlayer(targetPlayerName);
                        String targetUUID = target.getUniqueId().toString();
                        switch (args[args.length - 2].toLowerCase()) {
                            case "add":
                                if (executorPlayer.getUniqueId().equals(target.getUniqueId())) throw new ErrorSelfOperationException(executorPlayer);
                                if (playerDataConfig.isDeny(targetUUID)) throw new ErrorAlreadyBlacklistedException(executorPlayer);
                                if (REQUEST_QUEUE.containsKey(executorPlayer)) PlayerSchedulerUtil.performCommand(executorPlayer, "tpdeny");
                                playerDataConfig.addDeny(targetUUID);
                                SendMessageUtil.addDenysSuccess(executorPlayer, targetPlayerName);
                                return;
                            case "remove":
                                playerDataConfig.checkIsNoDeny(targetUUID, executorPlayer);
                                playerDataConfig.delDeny(targetUUID);
                                SendMessageUtil.removeDenySuccess(executorPlayer, targetPlayerName);
                                return;
                        }
                    }
                    throw new ErrorSyntaxGenericException(executorPlayer, command);
                case WARP:
                    if (args.length == 0){
                        List<String> warpNameList = LoadingConfigUtil.getWarpConfig().getWarpNameList();
                        SendMessageUtil.warpListMessage(sender, warpNameList);
                        return;
                    }
                    new WarpRequest(sender, args);
                    break;
                case SET_WARP:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.WARP)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.SET_WARP)) throw new ErrorPermissionDeniedException(executorPlayer);
                    location = executorPlayer.getLocation();
                    if (args.length != 1) throw new ErrorSyntaxWarpException(executorPlayer, command);
                    targetName = args[args.length - 1];
                    LoadingConfigUtil.getWarpConfig().setWarpLocation(targetName, location);
                    SendMessageUtil.setWarpSuccess(executorPlayer, targetName);
                    break;
                case DEL_WARP:
                    if (!config.isEnableCommand(CommandType.WARP)) throw new ErrorCommandDisabledException(sender);
                    if (!config.hasPermission(sender, PermissionType.DEL_WARP)) throw new ErrorPermissionDeniedException(sender);
                    if (args.length != 1) throw new ErrorSyntaxWarpException(sender, command);
                    WarpConfig warp = LoadingConfigUtil.getWarpConfig();
                    targetName = args[args.length - 1];
                    if (!warp.containsWarpLocation(targetName)) throw new ErrorWarpNotFoundException(sender, targetName);
                    warp.delWarpLocation(targetName);
                    SendMessageUtil.delWarpSuccess(sender, targetName);
                    break;
                case HOME:
                    new HomeRequest(sender, args);
                    break;
                case HOMES:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.HOME)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.HOME)) throw new ErrorPermissionDeniedException(executorPlayer);
                    playerDataConfig = PlayerDataConfig.getPlayerData(executorPlayer);
                    List<String> homeNameList = playerDataConfig.getHomeNameList();
                    SendMessageUtil.homeListMessage(executorPlayer, homeNameList);
                    break;
                case SET_HOME:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.HOME)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.HOME)) throw new ErrorPermissionDeniedException(executorPlayer);
                    if (args.length > 1) throw new ErrorSyntaxHomeException(executorPlayer, command);
                    location = executorPlayer.getLocation();
                    playerDataConfig = PlayerDataConfig.getPlayerData(executorPlayer);
                    if (args.length == 0){
                        playerDataConfig.setHomeLocation(location);
                        return;
                    }

                    targetName = args[args.length - 1];
                    playerDataConfig.setHomeLocation(targetName, location);
                    break;
                case SET_DEFAULT_HOME:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.HOME)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.HOME)) throw new ErrorPermissionDeniedException(executorPlayer);
                    if (args.length != 1) throw new ErrorSyntaxHomeException(executorPlayer, command);
                    targetName = args[args.length - 1];
                    playerDataConfig = PlayerDataConfig.getPlayerData(executorPlayer);
                    playerDataConfig.setDefaultHomeName(targetName, false);
                    break;
                case DEL_HOME:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.HOME)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.HOME)) throw new ErrorPermissionDeniedException(executorPlayer);
                    if (args.length != 1) throw new ErrorSyntaxHomeException(executorPlayer, command);
                    targetName = args[args.length - 1];
                    playerDataConfig = PlayerDataConfig.getPlayerData(executorPlayer);
                    playerDataConfig.delHomeLocation(targetName);
                    break;
                case SPAWN:
                    new SpawnRequest(sender, args);
                    break;
                case SET_SPAWN:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    if (!config.isEnableCommand(CommandType.SPAWN)) throw new ErrorCommandDisabledException(executorPlayer);
                    if (!config.hasPermission(executorPlayer, PermissionType.SET_SPAWN)) throw new ErrorPermissionDeniedException(executorPlayer);
                    location = executorPlayer.getLocation();
                    LoadingConfigUtil.getSpawnConfig().setSpawnLocation(location);
                    World world = executorPlayer.getWorld();
                    world.setSpawnLocation((int) location.getX(), location.getBlockY(), location.getBlockZ());
                    Bukkit.setSpawnRadius(0);
                    SendMessageUtil.setSpawnSuccess(executorPlayer);
                    break;
                case DEL_SPAWN:
                    if (!config.isEnableCommand(CommandType.SPAWN)) throw new ErrorCommandDisabledException(sender);
                    if (!config.hasPermission(sender, PermissionType.DEL_SPAWN)) throw new ErrorPermissionDeniedException(sender);
                    LoadingConfigUtil.getSpawnConfig().delSpawnLocation();
                    SendMessageUtil.delSpawnSuccess(sender);
                    break;
                case BACK:
                    new BackRequest(sender, args);
                    break;
                case SET_LANG:
                    if (!(sender instanceof Player)) throw new ErrorConsoleRestrictedException(sender);
                    executorPlayer = (Player) sender;
                    String languageStr = args[args.length - 1];
                    playerDataConfig = PlayerDataConfig.getPlayerData(executorPlayer);
                    if ("clear".equalsIgnoreCase(languageStr)){
                        playerDataConfig.setSetlang(false);
                        playerDataConfig.setLanguage(getConfig().isOldServer() ? getConfig().getDefaultLanguageStr() : executorPlayer.getLocale());
                        return;
                    }

                    playerDataConfig.setLanguage(languageStr);
                    playerDataConfig.setSetlang(true);
                    break;
                case VERSION:
                    if (!config.isEnableCommand(commandType)) throw new ErrorCommandDisabledException(sender);
                    if (!config.hasPermission(sender, PermissionType.VERSION)) throw new ErrorPermissionDeniedException(sender);
                    VersionUtil.updateCheck(sender);
                    break;
                case RELOAD:
                    if (!config.hasPermission(sender, PermissionType.RELOAD)) throw new ErrorPermissionDeniedException(sender);
                    LoadingConfigUtil.reloadALLConfig(sender);
                    break;
                default:
                    throw new ErrorRuntimeException(sender, "在 utils.ErrorCheckUtil 46 行，请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
            }
        } catch (Exception exception){
            if (config.isDebug()) exception.printStackTrace();
        }
    }
}
