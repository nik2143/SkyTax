package it.nik2143.skytax.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    private final CommandSender sender;

    public HelpCommand(CommandSender sender){
        this.sender = sender;
    }

    public void execute(){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&' ,"&6&lSky&e&lTax"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m&l-*-------------------*-"));
        if(sender.hasPermission("skytax.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d/skytax reload &8- &fReload All Config and Languages"));
        }
        if(sender.hasPermission("skytax.update")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d/skytax update &8- &fCheck if there is an update"));
        }
        if(sender.hasPermission("skytax.forceunlock")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d/skytax forceunlock [name] &8- &fUnlock the island without paying"));
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d/skytax isunlock &8- &fUnlock the island when he is locked by tax"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d/skytax about &8- &fShows you the plugin information"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m&l-*-------------------*-"));
    }

}
