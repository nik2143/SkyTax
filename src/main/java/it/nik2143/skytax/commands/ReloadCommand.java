package it.nik2143.skytax.commands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand {

    private final CommandSender sender;
    String prefix;

    public ReloadCommand(CommandSender sender){
        this.sender = sender;
        prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
    }

    public void execute(){
        if (sender.hasPermission("skytax.reload")) {
            try {
                SkyTax.getSkyTax().getConfiguration().forceReload();
                SkyTax.getSkyTax().getLanguage().forceReload();
                sender.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("plugin-reloaded")));
                if (sender instanceof Player){
                    Player utente = (Player)sender;
                    utente.sendTitle(Utils.color( "&6&lSky&e&lTax"), Utils.color(SkyTax.getSkyTax().getLanguage().getString("plugin-reloaded")));
                }
            } catch (Exception e) {
                sender.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("error")));
                if (sender instanceof Player) {
                    Player utente = (Player)sender;
                    utente.sendTitle(Utils.color("&6&lSky&e&lTax"), Utils.color(SkyTax.getSkyTax().getLanguage().getString("error")));
                }
            }
        } else {
            sender.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("permission-error")));
        }
    }

}
