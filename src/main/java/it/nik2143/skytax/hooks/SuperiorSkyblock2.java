package it.nik2143.skytax.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.SkyTax;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigInteger;
import java.util.UUID;

public class SuperiorSkyblock2 implements IslandsMethods{

    @Override
    public boolean shouldPayTax (TaxUser user){
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(user.getOfflinePlayer().getUniqueId());
        if (superiorPlayer.getIsland() == null) return false;
        BigInteger islandLevel = superiorPlayer.getIsland().getIslandLevel().toBigInteger();
        return  !user.lockdown &&
                hasPayedTax(user) && islandLevel.compareTo(BigInteger.ZERO) != 0 &&
                islandLevel.compareTo(BigInteger.valueOf(SkyTax.getSkyTax().getConfiguration().getLong("start-level"))) >= 0 &&
                (!SkyTax.getSkyTax().getConfiguration().getBoolean("TaxBypass") || !SkyTax.getSkyTax().getPerms().playerHas(null, user.getOfflinePlayer(), "SkyTax.bypass"));
    }

    @Override
    public double calculateTax(long islandLevel) {
        return calculateTax(BigInteger.valueOf(islandLevel));
    }

    @Override
    public double calculateTax(BigInteger islandLevel) {
        BigInteger livelloiniziale = BigInteger.valueOf(SkyTax.getSkyTax().getConfiguration().getLong("start-level"));
        double tax = SkyTax.getSkyTax().getConfiguration().getDouble("Tax");
        double multiplier = SkyTax.getSkyTax().getConfiguration().getDouble("Multiplier");
        int increaseLevel = SkyTax.getSkyTax().getConfiguration().getInt("IncreaseLevel");
        BigInteger taxedLevel = islandLevel.subtract(livelloiniziale);
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

    @Override
    public UUID getTeamLeader(OfflinePlayer player) {
        return SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland().getOwner().getUniqueId();
    }

    @Override
    public boolean hasIsland(OfflinePlayer player) {
        return SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland() != null;
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

    @Override
    public boolean hasPayedTax(TaxUser User) {
        int timeToPay = SkyTax.getSkyTax().getConfiguration().getInt("TimeToPay");
        long epochSeconds = java.time.Instant.now().getEpochSecond();
        long newPayement = epochSeconds - User.lastPayement;
        return newPayement > timeToPay;
    }
}
