package it.nik2143.skytax.commands.subcommands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.commands.Subcommand;
import it.nik2143.skytax.hooks.IslandsMethods;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IsUnlockCommand extends Subcommand {

    public IsUnlockCommand() {
        super(0);
    }

    @Override
    public void execute(Player player, String[] args) {
        IslandsMethods islandsMethods = SkyTax.getSkyTax().getIslandsMethods();
        double balance = SkyTax.getSkyTax().getEcon().getBalance(player);
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle = SkyTax.getSkyTax().getLanguage().getString("prefix-title");
        boolean titlesEnabled = SkyTax.getSkyTax().getConfiguration().getBoolean("send-titles");
        UUID leader = islandsMethods.getTeamLeader(player);
        TaxUser taxleader = TaxUser.getUser(leader);
        if (!taxleader.lockdown) {
            player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("pay-message-notax")));
            if (titlesEnabled) {
                player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("pay-message-notax-title")));
            }
            return;
        }
        double tax = islandsMethods.calculateTax(islandsMethods.getIslandLevel(player));
        tax += SkyTax.getSkyTax().getConfiguration().getOrDefault("forfeit", 0);
        if (balance < tax) {
            player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("pay-message-error").replace("%tax%", String.valueOf(tax))));
            if (titlesEnabled) {
                player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("pay-message-error-title")).replace("%tax%", String.valueOf(tax)));
            }
            return;
        }
        SkyTax.getSkyTax().getEcon().withdrawPlayer(player, tax);
        player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("pay-message").replace("%tax%", String.valueOf(tax))));
        if (titlesEnabled) {
            player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("pay-message-title")).replace("%tax%", String.valueOf(tax)));
        }
        taxleader.lastPayement = java.time.Instant.now().getEpochSecond();
        taxleader.taxnotpayed = 0;
        taxleader.lockdown = false;
    }

    @Override
    public String getHelpMessage() {
        return Utils.color("&d/skytax isunlock &8- &fUnlock the island when he is locked by tax");
    }

}