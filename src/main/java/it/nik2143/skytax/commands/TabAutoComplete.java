package it.nik2143.skytax.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabAutoComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        switch (args.length){
            case 1:
                List<String> subCommands = new ArrayList<>();
                subCommands.add("isunlock");
                if (sender.hasPermission("skytax.about")){
                    subCommands.add("about");
                }
                if (sender.hasPermission("skytax.reload")){
                    subCommands.add("reload");
                }
                if (sender.hasPermission("skytax.update")) {
                    subCommands.add("update");
                }
                if (sender.hasPermission("skytax.forceunlock")) {
                    subCommands.add("forceunlock");
                }
                final List<String> completions = new ArrayList<>();
                StringUtil.copyPartialMatches(args[0], subCommands, completions);
                Collections.sort(completions);
                return completions;
            case 2:
                if (args[0].equalsIgnoreCase("forceunlock")){
                    List<String> onlineplayers = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()){
                        onlineplayers.add(player.getName());
                    }
                    return onlineplayers;
                }
        }
        return Collections.emptyList();
    }
}
