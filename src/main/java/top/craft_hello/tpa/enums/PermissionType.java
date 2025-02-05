package top.craft_hello.tpa.enums;

import org.bukkit.command.CommandSender;

import static java.util.Objects.isNull;

public enum PermissionType {
    DEFAULT, VIP, VIP_PLUS, MVP, MVP_PLUS, MVP_PLUS_PLUS, ADMIN, RELOAD, VERSION, WARP, SET_WARP, DEL_WARP, HOME, SPAWN, SET_SPAWN, DEL_SPAWN, TPA, TP_HERE, TP_ALL, TP_LOGOUT, RTP, DENYS, BACK;

    public static String getPermission(PermissionType permissionType) {
        String permission;
        switch (permissionType){
            case DEFAULT:
                permission = "tpa.default";
                break;
            case VIP:
                permission = "tpa.vip";
                break;
            case VIP_PLUS:
                permission = "tpa.vip+";
                break;
            case MVP:
                permission = "tpa.mvp";
                break;
            case MVP_PLUS:
                permission = "tpa.mvp+";
                break;
            case MVP_PLUS_PLUS:
                permission = "tpa.mvp++";
                break;
            case RELOAD:
                permission = "tpa.reload";
                break;
            case VERSION:
                permission = "tpa.version";
                break;
            case WARP:
                permission = "tpa.warp";
                break;
            case SET_WARP:
                permission = "tpa.setwarp";
                break;
            case DEL_WARP:
                permission = "tpa.delwarp";
                break;
            case HOME:
                permission = "tpa.home";
                break;
            case SPAWN:
                permission = "tpa.spawn";
                break;
            case SET_SPAWN:
                permission = "tpa.setspawn";
                break;
            case DEL_SPAWN:
                permission = "tpa.delspawn";
                break;
            case TPA:
                permission = "tpa.tpa";
                break;
            case TP_HERE:
                permission = "tpa.tphere";
                break;
            case TP_ALL:
                permission = "tpa.tpall";
                break;
            case TP_LOGOUT:
                permission = "tpa.tplogout";
                break;
            case RTP:
                permission = "tpa.rtp";
                break;
            case DENYS:
                permission = "tpa.denys";
                break;
            case BACK:
                permission = "tpa.back";
                break;
            default:
                permission = "tpa.admin";
        }
        return permission;
    }

    public static boolean hasPermission(CommandSender sender, PermissionType... permissionTypes) {
        if (!isNull(sender)) {
            for (PermissionType permissionType : permissionTypes) if (!sender.hasPermission(PermissionType.getPermission(permissionType))) return false;
            return true;
        }
        return false;
    }
}


