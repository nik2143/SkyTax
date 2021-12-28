package it.nik2143.skytax.hooks;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.math.BigInteger;
import java.util.UUID;

public abstract class IslandsMethods implements Listener {

    protected boolean hasPayedTax(TaxUser user){
        int timeToPay = SkyTax.getSkyTax().getConfiguration().getInt("TimeToPay");
        long epochSeconds = java.time.Instant.now().getEpochSecond();
        long newPayement = epochSeconds - user.lastPayement;
        return newPayement > timeToPay;
    }

    public boolean shouldPayTax (TaxUser user){
        if (!hasIsland(user.getOfflinePlayer())) return false;
        BigInteger islandLevel = getIslandLevel(user.getOfflinePlayer());
        return  !user.lockdown &&
                hasPayedTax(user) && islandLevel.compareTo(BigInteger.ZERO) != 0 &&
                islandLevel.compareTo(BigInteger.valueOf(SkyTax.getSkyTax().getConfiguration().getLong("start-level"))) >= 0 &&
                (!SkyTax.getSkyTax().getConfiguration().getBoolean("TaxBypass") || !SkyTax.getSkyTax().getPerms().playerHas(null, user.getOfflinePlayer(), "SkyTax.bypass"));
    }

    public double calculateTax(BigInteger islandLevel) {
        BigInteger startLevel = BigInteger.valueOf(SkyTax.getSkyTax().getConfiguration().getLong("start-level"));
        double tax = SkyTax.getSkyTax().getConfiguration().getDouble("Tax");
        double multiplier = SkyTax.getSkyTax().getConfiguration().getDouble("Multiplier");
        int increaseLevel = SkyTax.getSkyTax().getConfiguration().getInt("IncreaseLevel");
        BigInteger taxedLevel = islandLevel.subtract(startLevel);
        long increaseTime = taxedLevel.divide(BigInteger.valueOf(increaseLevel)).longValue();
        multiplier = multiplier * tax;
        if (increaseTime==0){
            return tax;
        }
        for (int i = 0;i<increaseTime;i++){
            tax += multiplier;
        }
        return tax;
    }

    public void registerEvents(){
        Bukkit.getPluginManager().registerEvents(this,SkyTax.getSkyTax());
    }

    public abstract UUID getTeamLeader(OfflinePlayer player);
    public abstract BigInteger getIslandLevel(OfflinePlayer player);
    public abstract void deleteIsland(UUID playerUUID);
    public abstract void lockdownAction(OfflinePlayer islandOwner);
    public abstract boolean hasIsland(OfflinePlayer player);
    public abstract void onPlayerJoin(PlayerJoinEvent event);
}
