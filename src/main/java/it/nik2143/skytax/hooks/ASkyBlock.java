package it.nik2143.skytax.hooks;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.UUID;


public class ASkyBlock implements IslandsMethods {

    @Override
    public double calculateTax(long islandLevel) {
        long startlevel = SkyTax.getSkyTax().getConfiguration().getLong("start-level");
        double tax = SkyTax.getSkyTax().getConfiguration().getDouble("Tax");
        double multiplier = SkyTax.getSkyTax().getConfiguration().getDouble("Multiplier");
        int increaseLevel = SkyTax.getSkyTax().getConfiguration().getInt("IncreaseLevel");
        long taxedLevel = islandLevel - startlevel;
        long increaseTime = taxedLevel / increaseLevel;
        multiplier = multiplier * tax;
        if (increaseTime==0){
            return tax;
        }
        for (int i = 0;i<increaseTime;i++){
            tax += multiplier;
        }
        return tax;
    }

    @Override
    public double calculateTax(BigInteger islandLevel) {
        return calculateTax(islandLevel.longValue());
    }

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
    public boolean shouldPayTax(TaxUser user){
        long islandLevel = ASkyBlockAPI.getInstance().getLongIslandLevel(user.getOfflinePlayer().getUniqueId());
        return !user.lockdown &&
                hasPayedTax(user) &&
                islandLevel != 0 &&
                islandLevel >= SkyTax.getSkyTax().getConfiguration().getLong("start-level") &&
                (!SkyTax.getSkyTax().getConfiguration().getBoolean("TaxBypass") || !SkyTax.getSkyTax().getPerms().playerHas(null, user.getOfflinePlayer(), "SkyTax.bypass"));
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
    public boolean hasPayedTax(TaxUser user) {
        int timeToPay = SkyTax.getSkyTax().getConfiguration().getInt("TimeToPay");
        long epochSeconds = java.time.Instant.now().getEpochSecond();
        long newPayement = epochSeconds - user.lastPayement;
        return newPayement > timeToPay;
    }

}
