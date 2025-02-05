package top.craft_hello.tpa.objects;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.PlayerToLocationRequest;
import top.craft_hello.tpa.enums.CommandType;


public class SpawnRequest extends PlayerToLocationRequest {
    public SpawnRequest(CommandSender requestObject, String[] args)  {
        super(requestObject, args, CommandType.SPAWN);
    }
}
