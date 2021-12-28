package it.nik2143.skytax.commands.subcommands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.commands.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends Subcommand {

    String prefix;

    public ReloadCommand(){
        super(0);
        prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
    }

    @Override
    public String getPermission(){
        return "skytax.reload";
    }

    @Override
    public String getHelpMessage() {
        return Utils.color("&d/skytax reload &8- &fReload All Config and Languages");
    }

    @Override
    public void execute(CommandSender sender,String[] args) {
        SkyTax.getSkyTax().getConfiguration().forceReload();
        SkyTax.getSkyTax().getLanguage().forceReload();
        sender.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("plugin-reloaded")));
    }

    @Override
    public void execute(Player player,String[] args){
        SkyTax.getSkyTax().getConfiguration().forceReload();
        SkyTax.getSkyTax().getLanguage().forceReload();
        player.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("plugin-reloaded")));
        player.sendTitle(Utils.color(prefix), Utils.color(SkyTax.getSkyTax().getLanguage().getString("plugin-reloaded")));
    }

}
