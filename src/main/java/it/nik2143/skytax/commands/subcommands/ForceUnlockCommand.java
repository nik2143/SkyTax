package it.nik2143.skytax.commands.subcommands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.commands.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ForceUnlockCommand extends Subcommand {

    private final String prefix;

    public ForceUnlockCommand(){
        super(1);
        prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID teamLeader = SkyTax.getSkyTax().getIslandsMethods().getTeamLeader(Bukkit.getOfflinePlayer(args[1]));
        if (teamLeader==null || TaxUser.getUser(teamLeader)==null){
            sender.sendMessage(Utils.color(prefix+"Can't find island".replace("%player%",args[1])));
            return;
        }
        if (TaxUser.getUser(teamLeader.toString()).lockdown){
            TaxUser user = TaxUser.getUser(teamLeader.toString());
            user.lastPayement = java.time.Instant.now().getEpochSecond();
            user.taxnotpayed = 0;
            user.lockdown = false;
            sender.sendMessage(Utils.color(prefix+SkyTax.getSkyTax().getLanguage().getString("island-forceunlocked").replace("%player%",args[1])));
        } else {
            sender.sendMessage(Utils.color(prefix+SkyTax.getSkyTax().getLanguage().getString("island-notlocked").replace("%player%",args[1])));
        }
    }

    @Override
    public String getPermission() {
        return "skytax.forceunlock";
    }

    @Override
    public List<String> tabAutoComplete(CommandSender sender, String[] args){
        List<String> onlineplayers = new ArrayList<>();
        List<String> completions = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(p->onlineplayers.add(p.getName()));
        StringUtil.copyPartialMatches(args[1], onlineplayers, completions);
        Collections.sort(completions);
        return completions;
    }

    @Override
    public String getHelpMessage() {
        return Utils.color("&d/skytax forceunlock [name] &8- &fUnlock the island without paying");
    }

}
