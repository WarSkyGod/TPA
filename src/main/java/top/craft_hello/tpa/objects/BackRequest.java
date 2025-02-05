package top.craft_hello.tpa.objects;

import org.bukkit.command.CommandSender;
import top.craft_hello.tpa.abstracts.PlayerToLocationRequest;
import top.craft_hello.tpa.enums.CommandType;


public class BackRequest extends PlayerToLocationRequest {
    public BackRequest(CommandSender requestObject, String[] args)  {
        super(requestObject, args, CommandType.BACK);
    }
}
