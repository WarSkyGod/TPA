package top.craft_hello.tpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.enums.RequestType;
import top.craft_hello.tpa.exception.*;
import top.craft_hello.tpa.objects.Request;

import java.util.*;


public class ErrorCheckUtil{

    public static <T> boolean isNull(T object) {
        return object == null;
    }

    public static boolean check(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE){
        try {
            switch (REQUEST_TYPE){
                case TPA:
                    tpa(executor, args, REQUEST_TYPE);
                    break;
                case TPHERE:
                    tpHere(executor, args, REQUEST_TYPE);
                    break;
                case TPALL:
                    tpAll(executor, args, REQUEST_TYPE);
                    break;
                case TPLOGOUT:
                    tpLogout(executor, args, REQUEST_TYPE);
                    break;
                case TPACCEPT:
                    tpAccept(executor, args, REQUEST_TYPE);
                    break;
                case TPDENY:
                    tpDeny(executor, args, REQUEST_TYPE);
                    break;
                case DENYS:
                    denys(executor, args, REQUEST_TYPE);
                    break;
                case WARP:
                    warp(executor, args, REQUEST_TYPE);
                    break;
                case SETWARP:
                    setWarp(executor, args, REQUEST_TYPE);
                    break;
                case DELWARP:
                    delWarp(executor, args, REQUEST_TYPE);
                    break;
                case HOME:
                    home(executor, args, REQUEST_TYPE);
                    break;
                case HOMES:
                    homes(executor, args, REQUEST_TYPE);
                    break;
                case SETHOME:
                    setHome(executor, args, REQUEST_TYPE);
                    break;
                case SETDEFAULTHOME:
                    setDefaultHome(executor, args, REQUEST_TYPE);
                    break;
                case DELHOME:
                    delHome(executor, args, REQUEST_TYPE);
                    break;
                case SPAWN:
                    spawn(executor, args, REQUEST_TYPE);
                    break;
                case SETSPAWN:
                    setSpawn(executor, args, REQUEST_TYPE);
                    break;
                case DELSPAWN:
                    delSpawn(executor, REQUEST_TYPE);
                    break;
                case BACK:
                    back(executor, args, REQUEST_TYPE);
                    break;
                case SETLANG:
                    setLang(executor, args, REQUEST_TYPE);
                    break;
                case VERSION:
                    version(executor, REQUEST_TYPE);
                    break;
                case RELOAD:
                    reload(executor, REQUEST_TYPE);
            }
            return true;
        } catch (Exception exception){

            if (exception instanceof CommandErrorException){
                ((CommandErrorException) exception).sendMessage();
            }

            else if (exception instanceof ConsoleUseErrorException){
                ((ConsoleUseErrorException) exception).sendMessage();
            }

            else if (exception instanceof DefaultHomeErrorException){
                ((DefaultHomeErrorException) exception).sendMessage();
            }

            else if (exception instanceof DisableCommandErrorException){
                ((DisableCommandErrorException) exception).sendMessage();
            }

            else if (exception instanceof HomeAmountMaxErrorException){
                ((HomeAmountMaxErrorException) exception).sendMessage();
            }

            else if (exception instanceof HomeCommandErrorException){
                ((HomeCommandErrorException) exception).sendMessage();
            }

            else if (exception instanceof IsDenysErrorException){
                ((IsDenysErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotDefaultHomeErrorException){
                ((NotDefaultHomeErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotHomeErrorException){
                ((NotHomeErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotLastLocationErrorException){
                ((NotLastLocationErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotLogoutLocationErrorException){
                ((NotLogoutLocationErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotOnlinePlayerErrorException){
                ((NotOnlinePlayerErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotPermissionErrorException){
                ((NotPermissionErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotRequestAcceptException){
                ((NotRequestAcceptException) exception).sendMessage();
            }

            else if (exception instanceof NotRequestDenyException){
                ((NotRequestDenyException) exception).sendMessage();
            }

            else if (exception instanceof NotSetSpawnErrorException){
                ((NotSetSpawnErrorException) exception).sendMessage();
            }

            else if (exception instanceof NotWarpErrorException){
                ((NotWarpErrorException) exception).sendMessage();
            }

            else if (exception instanceof OfflineOrNullErrorException){
                ((OfflineOrNullErrorException) exception).sendMessage();
            }

            else if (exception instanceof PluginErrorException){
                ((PluginErrorException) exception).sendMessage();
            }

            else if (exception instanceof RequestLockErrorException){
                ((RequestLockErrorException) exception).sendMessage();
            }

            else if (exception instanceof SelfRequestedErrorException){
                ((SelfRequestedErrorException) exception).sendMessage();
            }

            else if (exception instanceof SetLangCommandErrorException){
                ((SetLangCommandErrorException) exception).sendMessage();
            }

            else if (exception instanceof TargetIsDenysErrorException){
                ((TargetIsDenysErrorException) exception).sendMessage();
            }

            else if (exception instanceof TargetIsNoDenysErrorException){
                ((TargetIsNoDenysErrorException) exception).sendMessage();
            }

            else if (exception instanceof TpaCommandErrorException){
                ((TpaCommandErrorException) exception).sendMessage();
            }

            else if (exception instanceof TpAllCommandErrorException){
                ((TpAllCommandErrorException) exception).sendMessage();
            }

            else if (exception instanceof WarpCommandErrorException){
                ((WarpCommandErrorException) exception).sendMessage();
            }

            else if (exception instanceof YouDenysYouErrorException){
                ((YouDenysYouErrorException) exception).sendMessage();
            }
            return false;
        }
    }


    // tpa命令错误检查
    public static void tpa(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
            consoleUseError(executor, args, REQUEST_TYPE);
            disableCommandError(executor, REQUEST_TYPE);
            commandError(executor, REQUEST_TYPE, args, 1);
            offlineOrNullError(executor, REQUEST_TYPE, args[args.length - 1]);
            notPermissionError(executor, REQUEST_TYPE);
            notPermissionError(executor, REQUEST_TYPE);
            selfRequestedError(executor, args);
            requestLockError(executor, args);
            playerIsDeny(executor, args, REQUEST_TYPE);
    }

    // tphere命令错误检查
    public static void tpHere(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        tpa(executor, args, REQUEST_TYPE);
    }

    // tpall命令错误检查
    public static void tpAll(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        consoleUseError(executor, args, REQUEST_TYPE);
        disableCommandError(executor, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 0);
        notPlayerOrWarpOrSpawnError(executor, args);
        notOnlinePlayerError(executor, args);
    }

    // tplogout命令错误检查
    public static void tpLogout(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        consoleUseError(executor, args, REQUEST_TYPE);
        disableCommandError(executor, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 1);
        notLogoutLocationError(executor, args[args.length - 1]);
    }

    // tpaccept命令错误检查
    public static void tpAccept(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        consoleUseError(executor, args, REQUEST_TYPE);
        disableCommandError(executor, REQUEST_TYPE);
        notRequestAccept(executor);
        offlineOrNullError(executor, REQUEST_TYPE, "");
    }

    // tpdeny命令错误检查
    public static void tpDeny(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        consoleUseError(executor, args, REQUEST_TYPE);
        disableCommandError(executor, REQUEST_TYPE);
        notRequestDeny(executor);
        offlineOrNullError(executor, REQUEST_TYPE, "");
    }

    // denys 命令错误检查
    public static void denys(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        consoleUseError(executor, args, REQUEST_TYPE);
        disableCommandError(executor, REQUEST_TYPE);
        commandError(executor,REQUEST_TYPE, args, 0);
        notPermissionError(executor, REQUEST_TYPE);
        youDenyYou(executor, args);
        playerIsDeny(executor, args, REQUEST_TYPE);
    }

    // warp命令错误检查
    public static void warp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 0);
        requestLockError(executor, new String[]{executor.getName()});
        notWarpError(executor, args);
    }

    // setwarp命令错误检查
    public static void setWarp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 1);
    }

    // delwarp命令错误检查
    public static void delWarp(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 1);
        notWarpError(executor, args);
    }

    // home命令错误检查
    public static void home(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 1);
        requestLockError(executor, new String[]{executor.getName()});
        notDefaultHomeError(executor, args);
        notHomeError(executor, args);
    }

    // homes命令错误检查
    public static void homes(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
    }

    // sethome命令错误检查
    public static void setHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 1);
        homeAmountMaxError(executor, args);
    }

    // setDefaultHome命令错误检查
    public static void setDefaultHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 1);
        notHomeError(executor, args);
        defaultHomeError(executor, args);
    }

    // delhome命令错误检查
    public static void delHome(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 1);
        notHomeError(executor, args);
    }

    // spawn命令错误检查
    public static void spawn(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        requestLockError(executor, new String[]{executor.getName()});
        notSetSpawnError(executor);
    }

    // setspawn命令错误检查
    public static void setSpawn(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
    }

    // delspawn命令错误检查
    public static void delSpawn(@NotNull CommandSender executor, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        notSetSpawnError(executor);
    }

    // back命令错误检查
    public static void back(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        consoleUseError(executor, args, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        requestLockError(executor, new String[]{executor.getName()});
        notLastLocationError(executor);
    }

    // setlang命令错误检查
    public static void setLang(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        consoleUseError(executor, args, REQUEST_TYPE);
        disableCommandError(executor, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
        commandError(executor, REQUEST_TYPE, args, 2);
    }

    // 是否检测版本更新错误检查
    public static void version(@NotNull CommandSender executor, RequestType REQUEST_TYPE) throws Exception{
        disableCommandError(executor, REQUEST_TYPE);
        notPermissionError(executor, REQUEST_TYPE);
    }

    // reload命令错误检查
    public static void reload(@NotNull CommandSender executor, RequestType REQUEST_TYPE) throws Exception{
        notPermissionError(executor, REQUEST_TYPE);
    }



    // 服务器未启用此命令错误
    public static void disableCommandError(@NotNull CommandSender executor, RequestType REQUEST_TYPE) throws Exception{
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
                if (!config.getBoolean("enable_command.tpa") || !config.getBoolean("enable_command.tphere")) throw new OfflineOrNullErrorException(executor);
                return;
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
                if (!config.getBoolean("update_check")) throw new Exception();
                return;
            default:
                throw new PluginErrorException(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
        }
        if (!LoadingConfigFileUtil.getConfig().getBoolean(enableCommand)) throw new DisableCommandErrorException(executor);
    }

    // 没有权限错误
    public static void notPermissionError(@NotNull CommandSender executor, RequestType REQUEST_TYPE) throws Exception{
        String permission;
        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        switch (REQUEST_TYPE){
            case TPA:
                if (!config.getBoolean("enable_permission.tpa")) return;
                permission = "tpa.tpa";
                break;
            case TPHERE:
                if (!config.getBoolean("enable_permission.tphere")) return;
                permission = "tpa.tphere";
                break;
            case TPALL:
                permission = "tpa.tpall";
                break;
            case DENYS:
                permission = "tpa.denys";
                if (!config.getBoolean("enable_permission.denys")) return;
                break;
            case TPLOGOUT:
                permission = "tpa.tplogout";
                break;
            case WARP:
                if (!config.getBoolean("enable_permission.warp")) return;
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
                if (!config.getBoolean("enable_permission.home")) return;
                permission = "tpa.home";
                break;
            case SPAWN:
                if (!config.getBoolean("enable_permission.spawn")) return;
                permission = "tpa.spawn";
                break;
            case SETSPAWN:
                permission = "tpa.setspawn";
                break;
            case DELSPAWN:
                permission = "tpa.delspawn";
                break;
            case BACK:
                if (!config.getBoolean("enable_permission.back")) return;
                permission = "tpa.back";
                break;
            case RELOAD:
                permission = "tpa.reload";
                break;
            case SETLANG:
                if (!config.getBoolean("enable_permission.setlang")) return;
                permission = "tpa.setlang";
                break;
            case VERSION:
                permission = "tpa.version";
                if (!executor.hasPermission(permission) || executor.hasPermission("tpa.admin")) throw new Exception();
                return;
            default:
                throw new PluginErrorException(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
        }
        if (!(executor.hasPermission(permission) || executor.hasPermission("tpa.admin"))) throw new NotPermissionErrorException(executor);
    }

    // 该命令不能由控制台执行错误
    public static void consoleUseError(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        boolean isConsole = !(executor instanceof Player);
        switch (REQUEST_TYPE){
            case TPALL:
                if (args.length == 0 && isConsole) throw new TpAllCommandErrorException(executor, "tpall");
                break;
            case WARP:
                if (args.length == 0 && isConsole) return;
            default:
                if (isConsole) throw new ConsoleUseErrorException(executor);
        }
    }

    // 命令使用错误
    public static void commandError(@NotNull CommandSender executor, RequestType REQUEST_TYPE, @NotNull String[] args, int number) throws Exception{
        if (args.length != number){
            String label;
            switch (REQUEST_TYPE){
                case TPA:
                    label = "tpa";
                    throw new TpaCommandErrorException(executor, label);
                case TPHERE:
                    label = "tphere";
                    throw new TpaCommandErrorException(executor, label);
                case TPALL:
                    label = "tpall";
                    if ((args.length == 1 && args[args.length - 1].equals("spawn")) || args.length == 2 && (args[args.length - 2].equals("player") || args[args.length - 2].equals("warp"))) return;
                    if (args.length >= 1) throw new TpAllCommandErrorException(executor, label);
                    break;
                case TPLOGOUT:
                    label = "tplogout";
                    throw new TpAllCommandErrorException(executor, label);
                case TPACCEPT:
                    label = "tpaccept";
                    break;
                case TPDENY:
                    label = "tpdeny";
                    break;
                case DENYS:
                    label = "denys";
                    if (args.length == 2 && (args[args.length - 2].equals("add") || args[args.length - 2].equals("remove"))) return;
                    break;
                case WARP:
                    label = "warp";
                    if (args.length == 1) return;
                    throw new WarpCommandErrorException(executor, label);
                case SETWARP:
                    label = "setwarp";
                    throw new WarpCommandErrorException(executor, label);
                case DELWARP:
                    label = "delwarp";
                    throw new WarpCommandErrorException(executor, label);
                case HOME:
                    label = "home";
                    if (args.length == 0) return;
                    throw new HomeCommandErrorException(executor, label);
                case SETHOME:
                    label = "sethome";
                    if (args.length == 0) return;
                    throw new HomeCommandErrorException(executor, label);
                case SETDEFAULTHOME:
                    label = "setdefaulthome";
                    throw new HomeCommandErrorException(executor, label);
                case DELHOME:
                    label = "delhome";
                    throw new HomeCommandErrorException(executor, label);
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
                    throw new SetLangCommandErrorException(executor, label);
                case RELOAD:
                    label = "tpa reload";
                    break;
                default:
                    label = "";
            }
            throw new CommandErrorException(executor, label);
        }
    }

    // 玩家或传送点或主城不存在错误
    public static void notPlayerOrWarpOrSpawnError(@NotNull CommandSender executor, String[] args) throws Exception{
        if (args.length ==  0) return;

        if (args.length == 1 && args[args.length - 1].equals("spawn")) {
            notSetSpawnError(executor);
            return;
        }

        if (args.length == 2){
            switch (args[args.length - 2]){
                case "player":
                    offlineOrNullError(executor, RequestType.TPA, args[args.length - 1]);
                    break;
                case "warp":
                    String[] args2 = {args[args.length - 2], args[args.length - 1]};
                    notWarpError(executor, args2);
                    break;
                default:
                    throw new PluginErrorException(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
            }
        }
    }

    // 无在线玩家错误
    public static void notOnlinePlayerError(@NotNull CommandSender executor, String[] args) throws Exception{
        if (args.length == 0){
            Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            onlinePlayers.remove((Player) executor);
            if (onlinePlayers.isEmpty()) throw new NotOnlinePlayerErrorException(executor);
            return;
        }

        if (args.length == 1 && args[args.length - 1].equals("spawn")){
            Collection<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            if (onlinePlayers.isEmpty()) throw new NotOnlinePlayerErrorException(executor);
            return;
        }

        if (args.length == 2){
            Collection<? extends Player> onlinePlayers;
            switch (args[args.length - 2]){
                case "player":
                    onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    onlinePlayers.remove(Bukkit.getPlayer(args[args.length - 1]));
                    if (onlinePlayers.isEmpty()) throw new NotOnlinePlayerErrorException(executor);
                    break;
                case "warp":
                    onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    if (onlinePlayers.isEmpty()) throw new NotOnlinePlayerErrorException(executor);
                    break;
                default:
                    throw new PluginErrorException(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
            }
        }
    }

    // 您不能添加自己
    public static void youDenyYou(@NotNull CommandSender executor, String[] args) throws Exception{
        if (args.length == 0) return;

        if (args.length == 2){
            String targetName = args[args.length - 1];
            Player target = Bukkit.getPlayer(targetName);
            if (executor.equals(target)) throw new YouDenysYouErrorException(executor);
        }
    }

    // 目标是否在拒绝请求的名单中
    public static void playerIsDeny(@NotNull CommandSender executor, String[] args, RequestType REQUEST_TYPE) throws Exception{
        switch (REQUEST_TYPE){
            case DENYS:
                if (args.length == 0) return;

                if (args.length == 2){
                    String targetName = args[args.length - 1];
                    OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                    List<String> denys = LoadingConfigFileUtil.getDenysList(targetName);
                    if (args[args.length - 2].equals("add") && denys.contains(target.getUniqueId().toString())) throw new TargetIsDenysErrorException(executor);
                    if (args[args.length - 2].equals("remove") && !denys.contains(target.getUniqueId().toString())) throw new TargetIsNoDenysErrorException(executor);
                }
                break;
            case TPA:
            case TPHERE:
                String targetName = args[args.length - 1];
                List<String> denys = LoadingConfigFileUtil.getDenysList(targetName);
                if (denys.contains(((Player) executor).getUniqueId().toString())) throw new IsDenysErrorException(executor);
        }
    }


    // 目标不在线或不存在错误
    public static void offlineOrNullError(@NotNull CommandSender executor, @NotNull RequestType REQUEST_TYPE , @NotNull String targetName) throws Exception{
        switch (REQUEST_TYPE){
            case TPA:
            case TPHERE:
                Player target = Bukkit.getPlayerExact(targetName);
                if (isNull(target) || !target.isOnline()) throw new OfflineOrNullErrorException(executor);
                break;
            case TPACCEPT:
            case TPDENY:
                Map<Player, Request> REQUEST_QUEUE = TeleportUtil.getREQUEST_QUEUE();
                Request request = REQUEST_QUEUE.get((Player) executor);
                if (isNull(request.getRequestPlayer()) || !request.getRequestPlayer().isOnline()) {
                    request.getTimer().cancel();
                    REQUEST_QUEUE.remove(executor);
                    throw new OfflineOrNullErrorException(executor);
                }
                break;
            default:
                throw new PluginErrorException(executor, "请联系开发者（https://github.com/WarSkyGod/TPA/issues）");
        }
    }

    // 不能自己请求自己错误
    public static void selfRequestedError(@NotNull CommandSender executor, @NotNull String[] args) throws Exception{
        String target = args[args.length - 1];
        if (executor.equals(Bukkit.getPlayer(target))) throw new SelfRequestedErrorException(executor);
    }

    // 自己或对方有尚未结束的请求错误
    public static void requestLockError(@NotNull CommandSender executor, @NotNull String[] args) throws Exception{
        String target = args[args.length - 1];
        if (TeleportUtil.getREQUEST_QUEUE().containsKey((Player) executor) || TeleportUtil.getREQUEST_QUEUE().containsKey(Bukkit.getPlayer(target))) throw new RequestLockErrorException(executor);
    }

    // 没有请求
    public static boolean notRequest(@NotNull CommandSender executor){
        return !TeleportUtil.getREQUEST_QUEUE().containsKey((Player) executor) || isNull(TeleportUtil.getREQUEST_QUEUE().get(executor));
    }

    // 没有待接受的请求
    public static void notRequestAccept(@NotNull CommandSender executor) throws Exception{
        if (notRequest(executor)) throw new NotRequestAcceptException(executor);
    }

    // 没有待拒绝的请求
    public static void notRequestDeny(@NotNull CommandSender executor) throws Exception{
        if (notRequest(executor)) throw new NotRequestDenyException(executor);
    }

    // 找不到传送点错误
    public static void notWarpError(@NotNull CommandSender executor, @NotNull String[] args) throws Exception{
        if (args.length == 0) return;

        FileConfiguration warp = LoadingConfigFileUtil.getWarp();
        String warpName = args[args.length - 1];
        if (isNull(warp.get(warpName))) throw new NotWarpErrorException(executor, warpName);
    }

    // 找不到家错误
    public static void notHomeError(@NotNull CommandSender executor, @NotNull String[] args) throws Exception{
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        String defaultHome = playerData.getString("default_home");
        String homeName;
        if (args.length == 0){
            homeName = defaultHome;
        } else {
            homeName = args[args.length - 1];
        }

        if (isNull(playerData.get("homes." + homeName))) throw new NotHomeErrorException(executor, homeName == null ? "" : homeName);
    }

    // 已是默认的家错误
    public static void defaultHomeError(@NotNull CommandSender executor, @NotNull String[] args) throws Exception{
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        String homeName = playerData.getString("default_home");
        if (!isNull(homeName) && homeName.equals(args[args.length - 1])) throw new DefaultHomeErrorException(executor, homeName);
    }

    // 没有默认的家错误
    public static void notDefaultHomeError(@NotNull CommandSender executor, @NotNull String[] args) throws Exception{
        if (args.length == 0){
            FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
            String homeName = playerData.getString("default_home");
            if (isNull(homeName)) throw new NotDefaultHomeErrorException(executor);
        }
    }

    // 可拥有的家的数量已达上限错误
    public static void homeAmountMaxError(@NotNull CommandSender executor, String[] args) throws Exception{
        String homeName;
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        String defaultHome = playerData.getString("default_home");
        if (args.length == 0){
            if (isNull(defaultHome)){
                homeName = "default";
            } else {
                homeName = defaultHome;
            }
        } else {
            homeName = args[args.length - 1];
        }

        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        if (!isNull(playerData.get("homes." + homeName))) return;
        int homeAmount = playerData.getInt("home_amount");
        int defaultHomeAmount = config.getInt("home_amount.default");
        int vipHomeAmount = config.getInt("home_amount.vip");
        int svipHomeAmount = config.getInt("home_amount.svip");
        int adminHomeAmount = config.getInt("home_amount.admin");

        if (executor.hasPermission("tpa.admin")){
            if (!(adminHomeAmount == -1 || homeAmount < adminHomeAmount)) throw new HomeAmountMaxErrorException(executor, adminHomeAmount);
            return;
        }
        if (executor.hasPermission("tpa.svip")){
            if (!(svipHomeAmount == -1 || homeAmount < svipHomeAmount)) throw new HomeAmountMaxErrorException(executor, svipHomeAmount);
            return;
        }
        if (executor.hasPermission("tpa.vip")) {
            if (!(vipHomeAmount == -1 || homeAmount < vipHomeAmount)) throw new HomeAmountMaxErrorException(executor, vipHomeAmount);
            return;
        }
        if (!(defaultHomeAmount == -1 || homeAmount < defaultHomeAmount)) throw new HomeAmountMaxErrorException(executor, defaultHomeAmount);
    }

    // 找不到上一次的位置错误
    public static void notLastLocationError(@NotNull CommandSender executor) throws Exception{
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(executor.getName());
        if (isNull(playerData.get("last_location"))) throw new NotLastLocationErrorException(executor);
    }

    // 找不到最近一次下线的的位置错误
    public static void notLogoutLocationError(@NotNull CommandSender executor, String target) throws Exception{
        FileConfiguration playerData = LoadingConfigFileUtil.getPlayerData(target);
        if (isNull(playerData.get("logout_location"))) throw new NotLogoutLocationErrorException(executor);
    }

    // 未设置主城错误
    public static void notSetSpawnError(@NotNull CommandSender executor) throws Exception{
        FileConfiguration spawn = LoadingConfigFileUtil.getSpawn();
        if (isNull(spawn.get("spawn"))) throw new NotSetSpawnErrorException(executor);
    }
}
