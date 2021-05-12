package it.nik2143.skytax.commands;

import de.leonhard.storage.Yaml;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.hooks.IslandsMethods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ForceUnlockCommand {

    private final CommandSender sender;
    private final String[] args;
    String prefix;
    private final IslandsMethods islandsMethods;


    public ForceUnlockCommand(CommandSender sender, String[] args, IslandsMethods islandsMethods){
        this.sender = sender;
        this.args = args;
        prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        this.islandsMethods = islandsMethods;
    }

    public void execete(){
        if (!sender.hasPermission("skytax.forceunlock")){
            sender.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("permission-error")));
        }
        if (args.length!=2){
            new HelpCommand(sender).execute();
        }
        try {
            UUID teamLeader = islandsMethods.getTeamLeader(Bukkit.getOfflinePlayer(args[1]));
            if (TaxUser.getUser(teamLeader.toString()).lockdown){
                TaxUser user = TaxUser.getUser(teamLeader.toString());
                user.lastPayement = java.time.Instant.now().getEpochSecond();
                user.taxnotpayed = 0;
                user.lockdown = false;
                sender.sendMessage(Utils.color(prefix+SkyTax.getSkyTax().getLanguage().getString("island-forceunlocked").replace("%player%",args[1])));
            } else {
                sender.sendMessage(Utils.color(prefix+SkyTax.getSkyTax().getLanguage().getString("island-notlocked").replace("%player%",args[1])));
            }
        } catch (NullPointerException ex){
            sender.sendMessage(Utils.color(prefix+"Can't find island".replace("%player%",args[1])));
        }
    }

}
