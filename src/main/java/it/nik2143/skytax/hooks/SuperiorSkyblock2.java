package it.nik2143.skytax.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandEnterEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.math.BigInteger;
import java.util.UUID;

public class SuperiorSkyblock2 extends IslandsMethods {

    @Override
    public UUID getTeamLeader(OfflinePlayer player) {
        return SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland().getOwner().getUniqueId();
    }

    @Override
    public boolean hasIsland(OfflinePlayer player) {
        return SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland() != null;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Island island = SuperiorSkyblockAPI.getIslandAt(event.getPlayer().getLocation());
        if (island != null && !island.isSpawn()){
            TaxUser ownerTaxUser = TaxUser.getUser(island.getOwner().getUniqueId());
            if (ownerTaxUser != null && ownerTaxUser.lockdown){
                SuperiorSkyblockAPI.getPlayer(event.getPlayer()).teleport(SuperiorSkyblockAPI.getSpawnIsland());
                island.setPlayerInside(SuperiorSkyblockAPI.getPlayer(event.getPlayer()), false);
            }
        }
    }

    @Override
    public BigInteger getIslandLevel(OfflinePlayer player) {
        return SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland().getIslandLevel().toBigInteger();
    }

    @Override
    public void deleteIsland(UUID playerUUID) {
        Bukkit.getScheduler().runTask(SkyTax.getSkyTax(),()->SuperiorSkyblockAPI.deleteIsland(SuperiorSkyblockAPI.getPlayer(playerUUID).getIsland()));
    }

    @Override
    public void lockdownAction(OfflinePlayer islandOwner){
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(islandOwner.getUniqueId());
        if (superiorPlayer.getIsland() == null) return;
        Island island = superiorPlayer.getIsland();
        if (superiorPlayer.isOnline() && superiorPlayer.isInsideIsland()){
            Bukkit.getScheduler().runTask(SkyTax.getSkyTax(),()->{
                superiorPlayer.teleport(SuperiorSkyblockAPI.getSpawnIsland());
                superiorPlayer.getIsland().setPlayerInside(superiorPlayer, false);
            });
        }
        for (SuperiorPlayer superiorPlayerinisland : island.getAllPlayersInside()){
            Bukkit.getScheduler().runTask(SkyTax.getSkyTax(),()->superiorPlayerinisland.teleport(SuperiorSkyblockAPI.getSpawnIsland()));
        }
    }

    @EventHandler
    private void onIslandEnter(IslandEnterEvent e) {
        Island island = e.getIsland();
        if (island == null) return;
        boolean titlesEnabled = SkyTax.getSkyTax().getConfiguration().getBoolean("send-titles");
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle = SkyTax.getSkyTax().getLanguage().getString("prefix-title");
        SuperiorPlayer leader = island.getOwner();
        if (leader == null) return;
        TaxUser taxUserLeader = TaxUser.getUser(leader.getUniqueId());
        if (taxUserLeader==null) return;
        if (taxUserLeader.lockdown) {
            if (leader.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                e.getPlayer().asPlayer().sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader").replace("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                if (titlesEnabled) {
                    e.getPlayer().asPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader-title").replace("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                }
            }
            else if (island.isMember(e.getPlayer())){
                e.getPlayer().asPlayer().sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member").replace("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                if (titlesEnabled) {
                    e.getPlayer().asPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member-title").replace("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                }
            }
            else {
                e.getPlayer().asPlayer().sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember").replace("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                if (titlesEnabled) {
                    e.getPlayer().asPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember-title").replace("%TaxNumber%", String.valueOf(taxUserLeader.taxnotpayed))));
                }
            }
            island.setPlayerInside(e.getPlayer(),false);
            e.setCancelled(true);
        }
    }
}
