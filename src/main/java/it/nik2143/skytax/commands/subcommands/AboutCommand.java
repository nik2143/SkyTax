package it.nik2143.skytax.commands.subcommands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.commands.Subcommand;
import org.bukkit.command.CommandSender;

public class AboutCommand extends Subcommand {
    public AboutCommand() {
        super(0);
    }

    @Override
    public void execute(CommandSender sender,String[] args) {
        sender.sendMessage(Utils.color(SkyTax.getSkyTax().getLanguage().getString("prefix") + "&7This server is running SkyTax v" + SkyTax.getSkyTax().getDescription().getVersion() + " by "+SkyTax.getSkyTax().getDescription().getAuthors()));
    }

    @Override
    public String getHelpMessage() {
        return Utils.color("&d/skytax about &8- &fShows you the plugin information");
    }
}
