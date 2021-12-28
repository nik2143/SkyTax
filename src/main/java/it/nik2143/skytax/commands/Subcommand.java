package it.nik2143.skytax.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Collections;
import java.util.List;

public abstract class Subcommand {

    private final int minargs;

    protected Subcommand(int minargs) {
        this.minargs = minargs;
    }

    public void execute(CommandSender sender,String[] args){
        sender.sendMessage("[SkyTax] Command Executable only from a player");
    }

    public void execute(Player player,String[] args){execute((CommandSender)player,args);}

    public int getMinArgs(){return minargs;}

    public String getPermission(){
        return null;
    }

    public List<String> tabAutoComplete(CommandSender sender,String[] args){
        return Collections.emptyList();
    }

    public abstract String getHelpMessage();

}
