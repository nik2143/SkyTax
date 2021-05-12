package it.nik2143.skytax.commands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.hooks.IslandsMethods;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IsUnlockCommand {

    private final CommandSender sender;
    String prefix;
    private final IslandsMethods islandsMethods;

    public IsUnlockCommand(CommandSender sender,IslandsMethods islandsMethods) {
        this.sender = sender;
        prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        this.islandsMethods = islandsMethods;
    }

    public void execute() {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[SkyTax] Command Executable only from a player");
            return;
        }
        Player player = (Player) sender;
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
            player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("pay-message-error").replaceAll("%tax%", String.valueOf(tax))));
            if (titlesEnabled) {
                player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("pay-message-error-title")).replaceAll("%tax%", String.valueOf(tax)));
            }
            return;
        }
        SkyTax.getSkyTax().getEcon().withdrawPlayer(player, tax);
        player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("pay-message").replaceAll("%tax%", String.valueOf(tax))));
        if (titlesEnabled) {
            player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("pay-message-title")).replaceAll("%tax%", String.valueOf(tax)));
        }
        taxleader.lastPayement = java.time.Instant.now().getEpochSecond();
        taxleader.taxnotpayed = 0;
        taxleader.lockdown = false;
    }

}
