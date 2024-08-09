package top.craft_hello.tpa.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.Request;
import top.craft_hello.tpa.TPA;


public class Tpa implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1 || args[0].equals("reload")){
            if (!sender.hasPermission("tpa.reload")){
                Messages.notPermission(sender);
                return true;
            }
            TPA.getPlugin(TPA.class).reloadAllConfig(sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            Messages.consoleUseError(sender);
            return true;
        }
        Request request = new Request(sender, label, args);
        request.tpa();
        return true;
    }
}
