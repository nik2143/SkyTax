package it.nik2143.skytax.listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandEnterEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import de.leonhard.storage.Yaml;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IslandTpEventSuperior implements Listener {

    @EventHandler
    public void onIslandEnter(IslandEnterEvent e) {
        Island island = e.getIsland();
        if (island == null) return;
        boolean titlesEnabled = SkyTax.getSkyTax().getConfiguration().getBoolean("send-titles");
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle = SkyTax.getSkyTax().getLanguage().getString("prefix-title");
        SuperiorPlayer Leader = island.getOwner();
        if (Leader == null) return;
            if (TaxUser.getUser(Leader.getUniqueId().toString()).lockdown) {
                TaxUser taxUserLeader = TaxUser.getUser(Leader.getUniqueId().toString());
                if (Leader.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                    e.getPlayer().asPlayer().sendMessage(Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader").replaceAll("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                    if (titlesEnabled) {
                        e.getPlayer().asPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader-title").replaceAll("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                    }
                }
                else if (island.isMember(e.getPlayer())){
                    e.getPlayer().asPlayer().sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member").replaceAll("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                    if (titlesEnabled) {
                        e.getPlayer().asPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member-title").replaceAll("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                    }
                }
                else {
                    e.getPlayer().asPlayer().sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember").replaceAll("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                    if (titlesEnabled) {
                        e.getPlayer().asPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember-title").replaceAll("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                    }
                }
                island.setPlayerInside(e.getPlayer(),false);
                e.setCancelled(true);
            }
    }
}
