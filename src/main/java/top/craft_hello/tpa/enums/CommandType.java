package top.craft_hello.tpa.enums;

public enum CommandType {
    TPA, TP_HERE, RTP, TP_ALL, TP_LOGOUT, TP_ACCEPT, TP_DENY, DENYS, WARP, SET_WARP, DEL_WARP, HOME, HOMES, SET_HOME, SET_DEFAULT_HOME, DEL_HOME, SPAWN, SET_SPAWN, DEL_SPAWN, BACK, RELOAD, SET_LANG, VERSION;

    public static CommandType getCommandType(String command){
        CommandType commandType;
        switch (command.toLowerCase()){
            case "tpa":
                commandType = CommandType.TPA;
                break;
            case "tphere":
                commandType = CommandType.TP_HERE;
                break;
            case "rtp":
                commandType = CommandType.RTP;
                break;
            case "tpall":
                commandType = CommandType.TP_ALL;
                break;
            case "tplogout":
                commandType = CommandType.TP_LOGOUT;
                break;
            case "tpaccept":
                commandType = CommandType.TP_ACCEPT;
                break;
            case "tpdeny":
                commandType = CommandType.TP_DENY;
                break;
            case "denys":
                commandType = CommandType.DENYS;
                break;
            case "warp":
                commandType = CommandType.WARP;
                break;
            case "setwarp":
                commandType = CommandType.SET_WARP;
                break;
            case "delwarp":
                commandType = CommandType.DEL_WARP;
                break;
            case "home":
                commandType = CommandType.HOME;
                break;
            case "homes":
                commandType = CommandType.HOMES;
                break;
            case "sethome":
                commandType = CommandType.SET_HOME;
                break;
            case "setdefaulthome":
                commandType = CommandType.SET_DEFAULT_HOME;
                break;
            case "delhome":
                commandType = CommandType.DEL_HOME;
                break;
            case "spawn":
                commandType = CommandType.SPAWN;
                break;
            case "setspawn":
                commandType = CommandType.SET_SPAWN;
                break;
            case "delspawn":
                commandType = CommandType.DEL_SPAWN;
                break;
            case "back":
                commandType = CommandType.BACK;
                break;
            case "reload":
                commandType = CommandType.RELOAD;
                break;
            case "setlang":
                commandType = CommandType.SET_LANG;
                break;
            case "version":
                commandType = CommandType.VERSION;
                break;
            default:
                commandType = null;
        }
        return commandType;
    }
}
