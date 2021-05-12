package it.nik2143.skytax.commands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import org.bukkit.command.CommandSender;

public class AboutCommand {

    private final CommandSender sender;

    public AboutCommand(CommandSender sender){
        this.sender = sender;
    }

    public void execute(){
        sender.sendMessage(Utils.color(SkyTax.getSkyTax().getLanguage().getString("prefix") + "&7This server is running SkyTax v" + SkyTax.getSkyTax().getDescription().getVersion() + " by "+SkyTax.getSkyTax().getDescription().getAuthors()));
    }

}
