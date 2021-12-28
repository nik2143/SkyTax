package it.nik2143.skytax.tasks;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.hooks.IslandsMethods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class PlayersCheckTask extends BukkitRunnable {

    private final IslandsMethods islandsMethods;

    public PlayersCheckTask() {
        this.islandsMethods = SkyTax.getSkyTax().getIslandsMethods();
    }

    @Override
    public void run() {
        String prefix =  SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle =  SkyTax.getSkyTax().getLanguage().getString("prefix-title");
        boolean titlesEnabled =  SkyTax.getSkyTax().getLanguage().getBoolean("send-titles");
        for (String username : SkyTax.getSkyTax().getUsers().keySet()) {
            try {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(username));
                UUID playerUUID = player.getUniqueId();
                if (!SkyTax.getSkyTax().getEcon().hasAccount(player)) continue;
                TaxUser user = TaxUser.getUser(player);
                double playerBalance = SkyTax.getSkyTax().getEcon().getBalance(player);
                long epochSeconds = java.time.Instant.now().getEpochSecond();

                if (!islandsMethods.shouldPayTax(user)) continue;

                double tax = islandsMethods.calculateTax(islandsMethods.getIslandLevel(player));
                if (playerBalance < tax) {
                    user.lastPayement = epochSeconds;
                    user.taxnotpayed += 1;
                    if (user.taxnotpayed >= SkyTax.getSkyTax().getConfiguration().getInt("tax-request-before")) {
                        switch (SkyTax.getSkyTax().getConfiguration().getString("tax-expired-action")) {
                            case "is-delete":
                                islandsMethods.deleteIsland(playerUUID);
                                if (player.getPlayer() != null) {
                                    player.getPlayer().sendMessage(Utils.color(prefix +  SkyTax.getSkyTax().getLanguage().getString("island-deleted")).replace("%TaxNumber%", String.valueOf(user.taxnotpayed)));
                                    if (titlesEnabled) {
                                        player.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color( SkyTax.getSkyTax().getLanguage().getString("island-deleted-title")));
                                    }
                                    user.taxnotpayed = 0;
                                } else {
                                    user.islandRemoved = true;
                                }
                                break;
                            case "is-lockdown":
                                islandsMethods.lockdownAction(player);
                                if (player.getPlayer() != null) {
                                    player.getPlayer().sendMessage(Utils.color(prefix +  SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader")).replace("%TaxNumber%", String.valueOf(user.taxnotpayed)));
                                    if (titlesEnabled) {
                                        player.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color( SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader-title")));
                                    }
                                }
                                user.lockdown = true;
                                break;
                            default:
                                Bukkit.getOperators().stream().filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer).filter(Objects::nonNull)
                                        .forEach(opPlayer -> opPlayer.sendMessage(Utils.color("&6&lSkyTax: &cConfiguration Error Occurred on tax-expired-action")));
                        }
                    }
                } else {
                    SkyTax.getSkyTax().getEcon().withdrawPlayer(player, tax);
                    if (player.getPlayer() != null) {
                        player.getPlayer().sendMessage(Utils.color(prefix +  SkyTax.getSkyTax().getLanguage().getString("pay-message").replace("%tax%", String.valueOf(tax))));
                        if (titlesEnabled) {
                            player.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color( SkyTax.getSkyTax().getLanguage().getString("pay-message-title")).replace("%tax%", String.valueOf(tax)));
                        }
                    } else {
                        user.taxpayedoffline += tax;
                    }
                    user.lastPayement = epochSeconds;
                    user.taxnotpayed = 0;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
