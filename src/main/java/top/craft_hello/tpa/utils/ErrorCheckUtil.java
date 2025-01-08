package top.craft_hello.tpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.objects.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class ErrorCheckUtil {

    // tpa命令错误检查
    public static boolean tpa(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return consoleUseError(executor)
                && disableCommandError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && offlineOrNullError(executor, REQUEST_TYPE, args[args.length - 1])
                && notPermissionError(executor, REQUEST_TYPE)
                && selfRequestedError(executor, args)
                && requestLockError(executor, args);
    }

    // tphere命令错误检查
    public static boolean tpHere(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return tpa(executor, args, REQUEST_TYPE);
    }

    // tpall命令错误检查
    public static boolean tpAll(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return consoleUseError(executor)
                && disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && notOnlinePlayerError(executor);
    }

    // tpaccept命令错误检查
    public static boolean tpAccept(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return consoleUseError(executor)
                && disableCommandError(executor, REQUEST_TYPE)
                && notRequestAccept(executor)
                && offlineOrNullError(executor, REQUEST_TYPE, "");
    }

    // tpdeny命令错误检查
    public static boolean tpDeny(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return consoleUseError(executor)
                && disableCommandError(executor, REQUEST_TYPE)
                && notRequestDeny(executor)
                && offlineOrNullError(executor, REQUEST_TYPE, "");
    }

    // warp命令错误检查
    public static boolean warp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && requestLockError(executor, new String[]{executor.getName()})
                && notWarpError(executor, args);
    }

    // setwarp命令错误检查
    public static boolean setWarp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor)
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
                && consoleUseError(executor)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && notUseKeyWordError(executor, args)
                && requestLockError(executor, new String[]{executor.getName()})
                && notHomeError(executor, args);
    }

    // sethome命令错误检查
    public static boolean setHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor)
                && notPermissionError(executor, REQUEST_TYPE)
                && homeAmountMaxError(executor)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && notUseKeyWordError(executor, args);
    }

    // delhome命令错误检查
    public static boolean delHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor)
                && notPermissionError(executor, REQUEST_TYPE)
                && commandError(executor, REQUEST_TYPE, args, 1)
                && notUseKeyWordError(executor, args)
                && notHomeError(executor, args);
    }

    // spawn命令错误检查
    public static boolean spawn(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor)
                && notPermissionError(executor, REQUEST_TYPE)
                && requestLockError(executor, new String[]{executor.getName()})
                && notSetSpawnError(executor);
    }

    // setspawn命令错误检查
    public static boolean setSpawn(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor)
                && notPermissionError(executor, REQUEST_TYPE);
    }

    // delspawn命令错误检查
    public static boolean delSpawn(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && notPermissionError(executor, REQUEST_TYPE)
                && notSetSpawnError(executor);
    }

    // back命令错误检查
    public static boolean back(@NotNull CommandSender executor, RequestType REQUEST_TYPE){
        return disableCommandError(executor, REQUEST_TYPE)
                && consoleUseError(executor)
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
            case TPACCEPT:
            case TPDENY:
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
            case SETHOME:
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
            case SETHOME:
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
    public static boolean consoleUseError(@NotNull CommandSender executor){
        if (!(executor instanceof Player)) {
            Messages.consoleUseError(executor);
            return false;
        }
        return true;
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
                case TPACCEPT:
                    label = "tpaccept";
                    break;
                case TPDENY:
                    label = "tpdeny";
                    break;
                case WARP:
                    label = "warp";
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
                    Messages.homeCommandError(executor, label);
                    return false;
                case SETHOME:
                    label = "sethome";
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
                case RELOAD:
                    label = "reload";
                    break;
                default:
                    label = "";
            }
            Messages.commandError(executor, label);
            return false;
        }
        return true;
    }

    // 无在线玩家错误
    public static boolean notOnlinePlayerError(@NotNull CommandSender executor){
        Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(executor);
        if (onlinePlayers.isEmpty()){
            Messages.notOnlinePlayerError(executor);
            return false;
        }
        return true;
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
        FileConfiguration warp = LoadingConfigFileUtil.getWarp();
        String warpName = args[args.length - 1];
        if (warp.getLocation(warpName) == null){
            Messages.notWarpError(executor, warpName);
            return false;
        }
        return true;
    }

    // 找不到家错误
    public static boolean notHomeError(@NotNull CommandSender executor, @NotNull String[] args){
        FileConfiguration home = LoadingConfigFileUtil.getHome();
        String homeName = args[args.length - 1];
        if (home.getLocation(executor.getName() + "." + homeName) == null){
            Messages.notHomeError(executor, homeName);
            return false;
        }
        return true;
    }

    // 可拥有的家的数量已达上限错误
    public static boolean homeAmountMaxError(@NotNull CommandSender executor){
        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        FileConfiguration home = LoadingConfigFileUtil.getHome();
        int homeAmount = home.getInt(executor.getName() + "." + "home_amount");
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

    // 不能使用关键字错误
    public static boolean notUseKeyWordError(@NotNull CommandSender executor, @NotNull String[] args){
        String arg = args[args.length - 1];
        if (arg.equalsIgnoreCase("home_amount")){
            Messages.notUseKeyWordError(executor);
            return false;
        }
        return true;
    }

    // 找不到上一次的位置错误
    public static boolean notLastLocationError(@NotNull CommandSender executor){
        FileConfiguration lastLocation = LoadingConfigFileUtil.getLastLocation();
        if (lastLocation.getLocation(executor.getName()) == null){
            Messages.notLastLocationError(executor);
            return false;
        }
        return true;
    }

    // 未设置主城错误
    public static boolean notSetSpawnError(@NotNull CommandSender executor){
        FileConfiguration spawn = LoadingConfigFileUtil.getSpawn();
        if (spawn.getLocation("spawn") == null){
            Messages.notSetSpawnError(executor);
            return false;
        }
        return true;
    }
}
