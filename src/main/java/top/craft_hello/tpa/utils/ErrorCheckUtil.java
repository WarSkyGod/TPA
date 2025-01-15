package top.craft_hello.tpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.objects.Request;

import java.util.*;


public class ErrorCheckUtil {

    // tpa命令错误检查
    public static boolean tpa(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor, args, REQUEST_TYPE)
                && disableCommandError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && offlineOrNullError(executor, REQUEST_TYPE, args[args.length - 1])
                && notPermissionError(executor, REQUEST_TYPE)
                && selfRequestedError(executor, args)
                && requestLockError(executor, args)
                && playerIsDeny(executor, args, REQUEST_TYPE);
    }

    // tphere命令错误检查
    public static boolean tpHere(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return tpa(executor, args, REQUEST_TYPE);
    }

    // tpall命令错误检查
    public static boolean tpAll(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor, args, REQUEST_TYPE)
                && disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 0)
                && notPlayerOrWarpOrSpawnError(executor, args)
                && notOnlinePlayerError(executor, args);
    }

    // tplogout命令错误检查
    public static boolean tpLogout(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor, args, REQUEST_TYPE)
                && disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && notLogoutLocationError(executor, args[args.length - 1]);
    }

    // tpaccept命令错误检查
    public static boolean tpAccept(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor, args, REQUEST_TYPE)
                && disableCommandError(executor, REQUEST_TYPE)
                && notRequestAccept(executor)
                && offlineOrNullError(executor, REQUEST_TYPE, "");
    }

    // tpdeny命令错误检查
    public static boolean tpDeny(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor, args, REQUEST_TYPE)
                && disableCommandError(executor, REQUEST_TYPE)
                && notRequestDeny(executor)
                && offlineOrNullError(executor, REQUEST_TYPE, "");
    }

    // denys 命令错误检查
    public static boolean denys(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor, args, REQUEST_TYPE)
                && disableCommandError(executor, REQUEST_TYPE)
                && commandError(executor,REQUEST_TYPE, args, 0)
                && notPermissionError(executor, REQUEST_TYPE)
                && youDenyYou(executor, args)
                && playerIsDeny(executor, args, REQUEST_TYPE);
    }

    // warp命令错误检查
    public static boolean warp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 0)
                && requestLockError(executor, new String[]{executor.getName()})
                && notWarpError(executor, args);
    }

    // setwarp命令错误检查
    public static boolean setWarp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1);
    }

    // delwarp命令错误检查
    public static boolean delWarp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && notWarpError(executor, args);
    }

    // home命令错误检查
    public static boolean home(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && requestLockError(executor, new String[]{executor.getName()})
                && notDefaultHomeError(executor, args)
                && notHomeError(executor, args);
    }

    // homes命令错误检查
    public static boolean homes(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE);
    }

    // sethome命令错误检查
    public static boolean setHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && homeAmountMaxError(executor, args);
    }

    // setDefaultHome命令错误检查
    public static boolean setDefaultHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && notHomeError(executor, args)
                && defaultHomeError(executor, args);

    }

    // delhome命令错误检查
    public static boolean delHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && notHomeError(executor, args);
    }

    // spawn命令错误检查
    public static boolean spawn(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && requestLockError(executor, new String[]{executor.getName()})
                && notSetSpawnError(executor);
    }

    // setspawn命令错误检查
    public static boolean setSpawn(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE);
    }

    // delspawn命令错误检查
    public static boolean delSpawn(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && notSetSpawnError(executor);
    }

    // back命令错误检查
    public static boolean back(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor, args, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && requestLockError(executor, new String[]{executor.getName()})
                && notLastLocationError(executor);
    }

    // 是否检测版本更新错误检查
    public static boolean version(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE);
    }

    // reload命令错误检查
    public static boolean reload(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return notPermissionError(executor, REQUEST_TYPE);
    }

    // setlang命令错误检查
    public static boolean setLang(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor, args, REQUEST_TYPE)
                && disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 2);
    }

    // 服务器未启用此命令错误
    public static boolean disableCommandError(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        String enableCommand;
        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        switch (REQUEST_TYPE){
            case TPA:
                enableCommand = "enable_command.tpa";
                break;
            case TPHERE:
                enableCommand = "enable_command.tphere";
                break;
            case TPALL:
                enableCommand = "enable_command.tpall";
                break;
            case TPLOGOUT:
                enableCommand = "enable_command.tplogout";
                break;
            case TPACCEPT:
            case TPDENY:
            case DENYS:
                if (!config.getBoolean("enable_command.tpa") || !config.getBoolean("enable_command.tphere")){
                    Messages.disableCommandError(executor);
                    return false;
                }
                return true;
            case WARP:
            case SETWARP:
            case DELWARP:
                enableCommand = "enable_command.warp";
                break;
            case HOME:
            case HOMES:
            case SETHOME:
            case SETDEFAULTHOME:
            case DELHOME:
                enableCommand = "enable_command.home";
                break;
            case SPAWN:
            case SETSPAWN:
            case DELSPAWN:
                enableCommand = "enable_command.spawn";
                break;
            case BACK:
                enableCommand = "enable_command.back";
                break;
            case SETLANG:
                enableCommand = "enable_command.setlang";
                break;
            case VERSION:
                return config.getBoolean("update_check");
            default:
                Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                return false;
        }
        if (!LoadingConfigFileUtil.getConfig().getBoolean(enableCommand)){
            Messages.disableCommandError(executor);
            return false;
        }
        return true;
    }

    // 没有权限错误
    public static boolean notPermissionError(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        String permission;
        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        switch (REQUEST_TYPE){
            case TPA:
                if (!config.getBoolean("enable_permission.tpa")) return true;
                permission = "tpa.tpa";
                break;
            case TPHERE:
                if (!config.getBoolean("enable_permission.tphere")) return true;
                permission = "tpa.tphere";
                break;
            case TPALL:
                permission = "tpa.tpall";
                break;
            case DENYS:
                permission = "tpa.denys";
                if (!config.getBoolean("enable_permission.denys")) return true;
                break;
            case TPLOGOUT:
                permission = "tpa.tplogout";
                break;
            case WARP:
                if (!config.getBoolean("enable_permission.warp")) return true;
                permission = "tpa.warp";
                break;
            case SETWARP:
                permission = "tpa.setwarp";
                break;
            case DELWARP:
                permission = "tpa.delwarp";
                break;
            case HOME:
            case HOMES:
            case SETHOME:
            case SETDEFAULTHOME:
            case DELHOME:
                if (!config.getBoolean("enable_permission.home")) return true;
                permission = "tpa.home";
                break;
            case SPAWN:
                if (!config.getBoolean("enable_permission.spawn")) return true;
                permission = "tpa.spawn";
                break;
            case SETSPAWN:
                permission = "tpa.setspawn";
                break;
            case DELSPAWN:
                permission = "tpa.delspawn";
                break;
            case BACK:
                if (!config.getBoolean("enable_permission.back")) return true;
                permission = "tpa.back";
                break;
            case RELOAD:
                permission = "tpa.reload";
                break;
            case SETLANG:
                if (!config.getBoolean("enable_permission.setlang")) return true;
                permission = "tpa.setlang";
                break;
            case VERSION:
                permission = "tpa.version";
                return executor.hasPermission(permission) || executor.hasPermission("tpa.admin");
            default:
                Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                return false;
        }
        if (!(executor.hasPermission(permission) || executor.hasPermission("tpa.admin"))){
            Messages.notPermissionError(executor);
            return false;
        }
        return true;
    }

    // 该命令不能由控制台执行错误
    public static boolean consoleUseError(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        boolean isConsole = !(executor instanceof Player);
        switch (REQUEST_TYPE){
            case TPALL:
                if (args.length == 0 && isConsole){
                    Messages.tpAllCommandError(executor, "tpall");
                    return false;
                }
                return true;
            case WARP:
                if (args.length == 0 && isConsole){
                    return true;
                }
            default:
                if (isConsole){
                    Messages.consoleUseError(executor);
                }
                return !isConsole;
        }
    }

    // 命令使用错误
    public static boolean commandError(@NotNull CommandSender executor, RequestType REQUEST_TYPE, @NotNull String[] args, int number){
        if (args.length != number){
            String label;
            switch (REQUEST_TYPE){
                case TPA:
                    label = "tpa";
                    Messages.tpaCommandError(executor, label);
                    return false;
                case TPHERE:
                    label = "tphere";
                    Messages.tpaCommandError(executor, label);
                    return false;
                case TPALL:
                    label = "tpall";
                    if ((args.length == 1 && args[args.length - 1].equals("spawn")) || args.length == 2 && (args[args.length - 2].equals("player") || args[args.length - 2].equals("warp"))) {
                        return true;
                    }
                    if (args.length >= 1){
                        Messages.tpAllCommandError(executor, label);
                        return false;
                    }
                    break;
                case TPLOGOUT:
                    label = "tplogout";
                    Messages.tpaCommandError(executor, label);
                    return false;
                case TPACCEPT:
                    label = "tpaccept";
                    break;
                case TPDENY:
                    label = "tpdeny";
                    break;
                case DENYS:
                    label = "denys";
                    if (args.length == 2 && (args[args.length - 2].equals("add") || args[args.length - 2].equals("remove"))) return true;
                    break;
                case WARP:
                    label = "warp";
                    if (args.length == 1) return true;
                    Messages.warpCommandError(executor, label);
                    return false;
                case SETWARP:
                    label = "setwarp";
                    Messages.warpCommandError(executor, label);
                    return false;
                case DELWARP:
                    label = "delwarp";
                    Messages.warpCommandError(executor, label);
                    return false;
                case HOME:
                    label = "home";
                    if (args.length == 0) return true;
                    Messages.homeCommandError(executor, label);
                    return false;
                case SETHOME:
                    label = "sethome";
                    if (args.length == 0) return true;
                    Messages.homeCommandError(executor, label);
                    return false;
                case SETDEFAULTHOME:
                    label = "setdefaulthome";
                    Messages.homeCommandError(executor, label);
                    return false;
                case DELHOME:
                    label = "delhome";
                    Messages.homeCommandError(executor, label);
                    return false;
                case SPAWN:
                    label = "spawn";
                    break;
                case DELSPAWN:
                    label = "delspawn";
                    break;
                case BACK:
                    label = "back";
                    break;
                case SETLANG:
                    label = "tpa setlang";
                    Messages.setLangCommandError(executor, label);
                    return false;
                case RELOAD:
                    label = "tpa reload";
                    break;
                default:
                    label = "";
            }
            Messages.commandError(executor, label);
            return false;
        }
        return true;
    }

    // 玩家或传送点或主城不存在错误
    public static boolean notPlayerOrWarpOrSpawnError(@NotNull CommandSender executor, String[] args){
        if (args.length ==  0){
            return true;
        }
        if (args.length == 1 && args[args.length - 1].equals("spawn")){
            return notSetSpawnError(executor);
        }

        if (args.length == 2){
            switch (args[args.length - 2]){
                case "player":
                    return offlineOrNullError(executor, RequestType.TPA, args[args.length - 1]);
                case "warp":
                    String[] args2 = {args[args.length - 2], args[args.length - 1]};
                    return notWarpError(executor, args2);
                default:
                    return false;
            }
        }
        return false;
    }

    // 无在线玩家错误
    public static boolean notOnlinePlayerError(@NotNull CommandSender executor, String[] args){
        if (args.length == 0){
            Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            onlinePlayers.remove(executor);
            if (onlinePlayers.isEmpty()){
                Messages.notOnlinePlayerError(executor);
                return false;
            }
            return true;
        }

        if (args.length == 1 && args[args.length - 1].equals("spawn")){
            Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            if (onlinePlayers.isEmpty()){
                Messages.notOnlinePlayerError(executor);
                return false;
            }
            return true;
        }

        if (args.length == 2){
            switch (args[args.length - 2]){
                case "player":
                    Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    onlinePlayers.remove(Bukkit.getPlayer(args[args.length - 1]));
                    if (onlinePlayers.isEmpty()){
                        Messages.notOnlinePlayerError(executor);
                        return false;
                    }
                    return true;
                case "warp":
                    onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    if (onlinePlayers.isEmpty()){
                        Messages.notOnlinePlayerError(executor);
                        return false;
                    }
                    return true;
                default:
                    Messages.pluginError(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
                    return false;
            }
        }
        return true;
    }

    // 您不能添加自己
    public static boolean youDenyYou(@NotNull CommandSender executor, String[] args){
        if (args.length == 0) return true;
        if (args.length == 2){
            String targetName = args[args.length - 1];
            Player target = Bukkit.getPlayer(targetName);
            if (executor.equals(target)){
                Messages.youDenyYouError(executor);
                return false;
            }
            return true;
        }
        return false;
    }

    // 目标是否在拒绝请求的名单中
    public static boolean playerIsDeny(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        switch (REQUEST_TYPE){
            case DENYS:
                if (args.length == 0) return true;
                if (args.length == 2){
                    String targetName = args[args.length - 1];
                    OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                    FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
                    Set<String> denySet = playerData.getKeys(true);
                    List<String> denys = new ArrayList<>();
                    for (String deny : denySet) {
                        if (deny.contains("denys.")) {
                            String deny2 = deny.substring(deny.indexOf(".") + 1);
                            if (!deny2.contains(".")) denys.add(deny2);
                        }
                    }
                    if (args[args.length - 2].equals("add") && denys.contains(target.getUniqueId().toString())){
                        // 对方已在您的拒绝请求列表中了
                        Messages.targetIsDenysError(executor);
                        return false;
                    }

                    if (args[args.length - 2].equals("add")) return true;

                    if (args[args.length - 2].equals("remove") && !denys.contains(target.getUniqueId().toString())){
                        // 对方不在您的拒绝请求列表中
                        Messages.targetIsNoDenysError(executor);
                        return false;
                    }

                    if (args[args.length - 2].equals("remove")) return true;
                }
                return false;
            case TPA:
            case TPHERE:
                String targetName = args[args.length - 1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(target.getName());
                Set<String> denySet = playerData.getKeys(true);
                List<String> denys = new ArrayList<>();
                for (String deny : denySet) {
                    if (deny.contains("denys.")) {
                        String deny2 = deny.substring(deny.indexOf(".") + 1);
                        if (!deny2.contains(".")) denys.add(deny2);
                    }
                }
                if (denys.contains(((Player) executor).getUniqueId().toString())){
                    // 对方已将您拉黑
                    Messages.isDenysError(executor);
                    return false;
                }
                return true;
        }
        return false;
    }


    // 目标不在线或不存在错误
    public static boolean offlineOrNullError(@NotNull CommandSender executor, @NotNull RequestType REQUEST_TYPE , @NotNull String targetName){
        switch (REQUEST_TYPE){
            case TPA:
            case TPHERE:
                Player target = Bukkit.getPlayerExact(targetName);
                if (target == null || !target.isOnline()){
                    Messages.offlineOrNullError(executor);
                    return false;
                }
                return true;
            case TPACCEPT:
            case TPDENY:
                Map<Player, Request> REQUEST_QUEUE = TeleportUtil.getREQUEST_QUEUE();
                Request request = REQUEST_QUEUE.get(executor);
                if (request.getRequestPlayer() == null || !request.getRequestPlayer().isOnline()) {
                    Messages.offlineOrNullError(executor);
                    request.getTimer().cancel();
                    REQUEST_QUEUE.remove(executor);
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    // 不能自己请求自己错误
    public static boolean selfRequestedError(@NotNull CommandSender executor, @NotNull String[] args){
        String target = args[args.length - 1];
        if (executor == executor.getServer().getPlayerExact(target)){
            Messages.selfRequestedError(executor);
            return false;
        }
        return true;
    }

    // 自己或对方有尚未结束的请求错误
    public static boolean requestLockError(@NotNull CommandSender executor, @NotNull String[] args){
        String target = args[args.length - 1];
        if (TeleportUtil.getREQUEST_QUEUE().containsKey(executor) || TeleportUtil.getREQUEST_QUEUE().containsKey(Bukkit.getPlayerExact(target))){
            Messages.requestLockError(executor);
            return false;
        }
        return true;
    }

    // 没有请求
    public static boolean notRequest(@NotNull CommandSender executor){
        return !TeleportUtil.getREQUEST_QUEUE().containsKey(executor) || TeleportUtil.getREQUEST_QUEUE().get(executor) == null;
    }

    // 没有待接受的请求
    public static boolean notRequestAccept(@NotNull CommandSender executor){
        if (notRequest(executor)){
            Messages.notRequestAccept(executor);
            return false;
        }
        return true;
    }

    // 没有待拒绝的请求
    public static boolean notRequestDeny(@NotNull CommandSender executor){
        if (notRequest(executor)){
            Messages.notRequestDeny(executor);
            return false;
        }
        return true;
    }

    // 找不到传送点错误
    public static boolean notWarpError(@NotNull CommandSender executor, @NotNull String[] args){
        if (args.length == 0) return true;

        FileConfiguration warp = LoadingConfigFileUtil.getWarp();
        String warpName = args[args.length - 1];
        if (warp.get(warpName) == null){
            Messages.notWarpError(executor, warpName);
            return false;
        }
        return true;
    }

    // 找不到家错误
    public static boolean notHomeError(@NotNull CommandSender executor, @NotNull String[] args){
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        String defaultHome = playerData.getString("default_home");
        String homeName;
        if (args.length == 0){
            homeName = defaultHome;
        } else {
            homeName = args[args.length - 1];
        }

        if (playerData.get("homes." + homeName) == null){
            Messages.notHomeError(executor, homeName);
            return false;
        }
        return true;
    }

    // 已是默认的家错误
    public static boolean defaultHomeError(@NotNull CommandSender executor, @NotNull String[] args){
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        String homeName = playerData.getString("default_home");
        if (homeName != null && homeName.equals(args[args.length - 1])){
            Messages.defaultHomeError(executor, homeName);
            return false;
        }
        return true;
    }

    // 没有默认的家错误
    public static boolean notDefaultHomeError(@NotNull CommandSender executor, @NotNull String[] args){
        if (args.length == 0){
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
            String homeName = playerData.getString("default_home");
            if (homeName == null){
                Messages.notDefaultHomeError(executor);
                return false;
            }
        }
        return true;
    }

    // 可拥有的家的数量已达上限错误
    public static boolean homeAmountMaxError(@NotNull CommandSender executor, String[] args){
        String homeName;
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        String defaultHome = playerData.getString("default_home");
        if (args.length == 0){
            if (defaultHome == null){
                homeName = "default";
            } else {
                homeName = defaultHome;
            }
        } else {
            homeName = args[args.length - 1];
        }

        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        if (playerData.get("homes." + homeName) != null) return true;
        int homeAmount = playerData.getInt("home_amount");
        int defaultHomeAmount = config.getInt("home_amount.default");
        int vipHomeAmount = config.getInt("home_amount.vip");
        int svipHomeAmount = config.getInt("home_amount.svip");
        int adminHomeAmount = config.getInt("home_amount.admin");
        boolean flag;

        if (executor.hasPermission("tpa.admin")){
            flag = (adminHomeAmount == -1 || homeAmount < adminHomeAmount);
            if (!flag) Messages.homeAmountMaxError(executor, adminHomeAmount);
            return (adminHomeAmount == -1 || homeAmount < adminHomeAmount);
        }
        if (executor.hasPermission("tpa.svip")){
            flag = (svipHomeAmount == -1 || homeAmount < svipHomeAmount);
            if (!flag) Messages.homeAmountMaxError(executor, svipHomeAmount);
            return flag;
        }
        if (executor.hasPermission("tpa.vip")) {
            flag = (vipHomeAmount == -1 || homeAmount < vipHomeAmount);
            if (!flag) Messages.homeAmountMaxError(executor, vipHomeAmount);
            return flag;
        }
        flag = (defaultHomeAmount == -1 || homeAmount < defaultHomeAmount);
        if (!flag) Messages.homeAmountMaxError(executor, defaultHomeAmount);
        return flag;
    }

    // 找不到上一次的位置错误
    public static boolean notLastLocationError(@NotNull CommandSender executor){
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        if (playerData.get("last_location") == null){
            Messages.notLastLocationError(executor);
            return false;
        }
        return true;
    }

    // 找不到最近一次下线的的位置错误
    public static boolean notLogoutLocationError(@NotNull CommandSender executor, String target){
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(target);
        if (playerData.get("logout_location") == null){
            Messages.notLogoutLocationError(executor);
            return false;
        }
        return true;
    }

    // 未设置主城错误
    public static boolean notSetSpawnError(@NotNull CommandSender executor){
        FileConfiguration spawn = LoadingConfigFileUtil.getSpawn();
        if (spawn.get("spawn") == null){
            Messages.notSetSpawnError(executor);
            return false;
        }
        return true;
    }
}
