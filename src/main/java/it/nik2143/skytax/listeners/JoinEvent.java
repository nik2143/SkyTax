package it.nik2143.skytax.listeners;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import de.leonhard.storage.Yaml;
import it.nik2143.skytax.hooks.ASkyBlock;
import it.nik2143.skytax.hooks.SuperiorSkyblock2;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.hooks.IslandsMethods;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinEvent implements Listener {
    
    private final IslandsMethods islandsMethods;

    public JoinEvent(IslandsMethods islandsMethods) {
        this.islandsMethods = islandsMethods;
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        String prefixtitle = SkyTax.getSkyTax().getLanguage().getString("prefix-title");

        if (SkyTax.getSkyTax().outdated && SkyTax.getSkyTax().getConfiguration().getBoolean("get-updates")) {
            if (e.getPlayer().hasPermission("skytax.update")) {
                TextComponent tc = new TextComponent(Utils.color(prefix + "&7There is a new update available (" + SkyTax.getSkyTax().getDescription().getVersion() + ")"));
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/81159"));
                e.getPlayer().spigot().sendMessage(tc);
            }
        }
        Player player = e.getPlayer();
        TaxUser user = TaxUser.getUser(e.getPlayer());
        UUID playerUUID = e.getPlayer().getUniqueId();
        if (user.taxpayedoffline > 0) {
            player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("pay-message-tax-offline")).replaceAll("%TaxOffline%", String.valueOf(user.taxpayedoffline)));
            user.taxpayedoffline = 0;
        }
        if (user.islandRemoved && SkyTax.getSkyTax().getConfiguration().getString("tax-expired-action").equalsIgnoreCase("is-delete")) {
            player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("island-deleted")).replaceAll("%TaxNumber%", String.valueOf(user.taxnotpayed)));
            user.islandRemoved = false;
            user.taxnotpayed = 0;
        }
        boolean titlesEnabled = SkyTax.getSkyTax().getConfiguration().getBoolean("send-titles");
        if (islandsMethods.hasIsland(e.getPlayer())){
            UUID uuidLeader = islandsMethods.getTeamLeader(player);
            if (TaxUser.getUser(uuidLeader.toString()).lockdown) {
                if (playerUUID.equals(uuidLeader)) {
                    Bukkit.getPlayer(uuidLeader).sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("leader-join-message").replaceAll("%TaxNumber%", String.valueOf(user.taxnotpayed))));
                    if (titlesEnabled) {
                        Bukkit.getPlayer(uuidLeader).sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("leader-join-message-title")));
                    }
                } else {
                    player.sendMessage(Utils.color(prefix + SkyTax.getSkyTax().getLanguage().getString("member-join-message").replaceAll("%TaxNumber%", String.valueOf(TaxUser.getUser(uuidLeader.toString()).taxnotpayed))));
                    if (titlesEnabled) {
                        player.sendTitle(Utils.color(prefixtitle), Utils.color(SkyTax.getSkyTax().getLanguage().getString("member-join-message-title")));
                    }
                }
            }
            if(islandsMethods instanceof ASkyBlock){
                if (ASkyBlockAPI.getInstance().getIslandAt(e.getPlayer().getLocation())!=null && TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getPlayer().getLocation()).getOwner().toString()) != null && TaxUser.getUser(ASkyBlockAPI.getInstance().getIslandAt(e.getPlayer().getLocation()).getOwner().toString()).lockdown){
                    Utils.teleportToSpawn(e.getPlayer());
                }
            } else if (islandsMethods instanceof SuperiorSkyblock2){
                if (SuperiorSkyblockAPI.getIslandAt(e.getPlayer().getLocation())!=null && !SuperiorSkyblockAPI.getIslandAt(e.getPlayer().getLocation()).isSpawn()){
                    if (TaxUser.getUser(SuperiorSkyblockAPI.getIslandAt(e.getPlayer().getLocation()).getOwner().getUniqueId().toString()).lockdown){
                        SuperiorSkyblockAPI.getPlayer(e.getPlayer()).teleport(SuperiorSkyblockAPI.getSpawnIsland());
                        SuperiorSkyblockAPI.getIslandAt(e.getPlayer().getLocation()).setPlayerInside(SuperiorSkyblockAPI.getPlayer(e.getPlayer()),false);
                    }
                }
            }
        }
    }
}
