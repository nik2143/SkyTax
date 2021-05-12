package it.nik2143.skytax.listeners;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;
import com.wasteofplastic.askyblock.events.IslandPreTeleportEvent;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class IslandTpEventASky implements Listener {

    @EventHandler
    public void onIslandEnter(IslandEnterEvent e){
        UUID uuidPlayer = e.getPlayer();
        TaxUser user = TaxUser.getUser(e.getPlayer());
        Player player = Bukkit.getPlayer(e.getPlayer());
        boolean titlesEnabled = SkyTax.getSkyTax().getConfiguration().getBoolean("send-titles");
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle = SkyTax.getSkyTax().getLanguage().getString("prefix-title");
        if (TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner().toString())!= null &&
                TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner().toString()).lockdown){
            UUID uuidLeader = ASkyBlockAPI.getInstance().getIslandAt(e.getLocation()).getOwner();
            if(uuidPlayer.equals(uuidLeader))
            {
                player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader").replaceAll("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                if(titlesEnabled) {
                    player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader-title").replaceAll("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                }
            }else if (ASkyBlockAPI.getInstance().getTeamMembers(uuidLeader).contains(e.getPlayer())){
                player.sendMessage(Utils.color(prefix +  SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if(titlesEnabled) {
                    player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member-title").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            } else {
                player.sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if (titlesEnabled) {
                    player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember-title").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            }
            Utils.teleportToSpawn(player);
        }
    }

    @EventHandler
    public void onIslandPreTelepor(IslandPreTeleportEvent e) {
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
                e.getPlayer().sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader").replaceAll("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                if(titlesEnabled) {
                    e.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-leader-title").replaceAll("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                }
            }else if (ASkyBlockAPI.getInstance().getTeamMembers(uuidLeader).contains(e.getPlayer().getUniqueId())){
                e.getPlayer().sendMessage(Utils.color(prefix +  SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if(titlesEnabled) {
                    e.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-member-title").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            } else {
                e.getPlayer().sendMessage(Utils.color( prefix + SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                if (titlesEnabled) {
                    e.getPlayer().sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("tax-notpayed-nomember-title").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                }
            }
            e.setCancelled(true);
        }
    }
}