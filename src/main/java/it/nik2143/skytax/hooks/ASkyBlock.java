package it.nik2143.skytax.hooks;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;
import com.wasteofplastic.askyblock.events.IslandPreTeleportEvent;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.math.BigInteger;
import java.util.UUID;


public class ASkyBlock extends IslandsMethods {

    @Override
    public UUID getTeamLeader(OfflinePlayer player) {
        if (ASkyBlockAPI.getInstance().inTeam(player.getUniqueId())){
            return ASkyBlockAPI.getInstance().getTeamLeader(player.getUniqueId());
        }else {
            return player.getUniqueId();
        }
    }

    @Override
    public BigInteger getIslandLevel(OfflinePlayer player) {
        return BigInteger.valueOf(ASkyBlockAPI.getInstance().getLongIslandLevel(player.getUniqueId()));
    }

    @Override
    public void deleteIsland(UUID playerUUID) {
        com.wasteofplastic.askyblock.ASkyBlock.getPlugin().deletePlayerIsland(playerUUID, true);
    }

    @Override
    public void lockdownAction(OfflinePlayer islandOwner) {
        for (Player target : SkyTax.getSkyTax().getServer().getOnlinePlayers()) {
            if (target == null || target.hasMetadata("NPC") || ASkyBlockAPI.getInstance().getIslandAt(target.getLocation())==null) continue;
            if(ASkyBlockAPI.getInstance().getIslandAt(target.getLocation()).getOwner().equals(islandOwner.getUniqueId())) {
                Bukkit.getScheduler().runTask(SkyTax.getSkyTax(),()->Utils.teleportToSpawn(target));
            }
        }
    }

    @Override
    public boolean hasIsland(OfflinePlayer player) {
        return ASkyBlockAPI.getInstance().hasIsland(player.getUniqueId());
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Island island = ASkyBlockAPI.getInstance().getIslandAt(event.getPlayer().getLocation());
        if (island != null){
            TaxUser ownerTaxUser = TaxUser.getUser(island.getOwner());
            if (ownerTaxUser != null && ownerTaxUser.lockdown){
                Utils.teleportToSpawn(event.getPlayer());
            }
        }
    }

    @EventHandler
    private void onIslandEnter(IslandEnterEvent e){
        UUID uuidPlayer = e.getPlayer();
        TaxUser user = TaxUser.getUser(uuidPlayer);
        Player player = Bukkit.getPlayer(uuidPlayer);
        boolean titlesEnabled = SkyTax.getSkyTax().getConfiguration().getBoolean("send-titles");
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle = SkyTax.getSkyTax().getLanguage().getString("prefix-title");
        if (TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner().toString())!= null &&
                TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner().toString()).lockdown){
            UUID uuidLeader = ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner();
            if(uuidPlayer.equals(uuidLeader))
            {
                player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader").replace("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                if(titlesEnabled) {
                    player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader-title").replace("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                }
            }else if (ASkyBlockAPI.getInstance().getTeamMembers(uuidLeader).contains(e.getPlayer())){
                player.sendMessage(Utils.color(prefix +  SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if(titlesEnabled) {
                    player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member-title").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            } else {
                player.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if (titlesEnabled) {
                    player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember-title").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            }
            Utils.teleportToSpawn(player);
        }
    }

    @EventHandler
    private void onIslandPreTelepor(IslandPreTeleportEvent e) {
        UUID uuidPlayer = e.getPlayer().getUniqueId();
        TaxUser user = TaxUser.getUser(e.getPlayer());
        boolean titlesEnabled = SkyTax.getSkyTax().getConfiguration().getBoolean("send-titles");
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle = SkyTax.getSkyTax().getLanguage().getString("prefix-title");
        if (TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner().toString())!= null &&
                TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner().toString()).lockdown){
            UUID uuidLeader = ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner();
            if(uuidPlayer.equals(uuidLeader))
            {
                e.getPlayer().sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader").replace("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                if(titlesEnabled) {
                    e.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader-title").replace("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                }
            }else if (ASkyBlockAPI.getInstance().getTeamMembers(uuidLeader).contains(e.getPlayer().getUniqueId())){
                e.getPlayer().sendMessage(Utils.color(prefix +  SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if(titlesEnabled) {
                    e.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member-title").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            } else {
                e.getPlayer().sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if (titlesEnabled) {
                    e.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember-title").replace("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            }
            e.setCancelled(true);
        }
    }

}
