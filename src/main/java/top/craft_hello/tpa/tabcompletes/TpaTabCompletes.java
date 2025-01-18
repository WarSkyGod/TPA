package top.craft_hello.tpa.tabcompletes;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.craft_hello.tpa.utils.LoadingConfigFileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TpaTabCompletes implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        FileConfiguration lang = LoadingConfigFileUtil.getLang(sender);
        FileConfiguration config = LoadingConfigFileUtil.getConfig();
        boolean isEnableSetLangCommand = config.getBoolean("enable_command.setlang");
        boolean isEnableSetLangPermission = config.getBoolean("enable_permission.setlang");
        if (args.length == 0 || args.length == 1 && "spawn".equals(args[args.length - 1])){
            return list;
        }


        if (args.length == 1){
            if (sender.hasPermission("tpa.reload") || sender.hasPermission("tpa.admin")){
                list.add("reload");
            }

            if (sender instanceof Player){
                if (isEnableSetLangCommand && (!isEnableSetLangPermission || sender.hasPermission("tpa.setlang") || sender.hasPermission("tpa.admin"))){
                    list.add("setlang");
                }
                List<String> playerList = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!sender.equals(player)){
                        list.add(player.getName());
                        playerList.add(player.getName());
                    }
                }

                if (playerList.isEmpty()) list.add(lang.getString("not_online_player"));
            }
            return list;
        }

        if (isEnableSetLangCommand && (!isEnableSetLangPermission || sender.hasPermission("tpa.setlang") || sender.hasPermission("tpa.admin")) && args.length == 2 && args[args.length - 2].equals("setlang")){
            Map<String, FileConfiguration> langs = LoadingConfigFileUtil.getLangs();
            for (Map.Entry<String, FileConfiguration> langMap : langs.entrySet()){
                list.add(langMap.getKey());
            }
        }
        return list;
    }
}
