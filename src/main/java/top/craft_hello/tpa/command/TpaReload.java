package top.craft_hello.tpa.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.craft_hello.tpa.Messages;
import top.craft_hello.tpa.TPA;

public class TpaReload implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("tpa.reload")){
            Messages.notPermission(sender);
            return true;
        }
        TPA.getPlugin(TPA.class).reloadAllConfig(sender);
        return true;
    }
}
