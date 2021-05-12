package it.nik2143.skytax.hooks;

import it.nik2143.skytax.TaxUser;
import org.bukkit.OfflinePlayer;
import java.math.BigInteger;
import java.util.UUID;

public interface IslandsMethods {
    boolean hasPayedTax(TaxUser user);
    boolean shouldPayTax(TaxUser user);
    double calculateTax(long islandLevel);
    double calculateTax(BigInteger islandLevel);
    UUID getTeamLeader(OfflinePlayer player);
    BigInteger getIslandLevel(OfflinePlayer player);
    void deleteIsland(UUID playerUUID);
    void lockdownAction(OfflinePlayer islandOwner);
    boolean hasIsland(OfflinePlayer player);


}
