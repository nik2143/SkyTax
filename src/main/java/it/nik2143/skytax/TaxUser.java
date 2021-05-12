package it.nik2143.skytax;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.util.HashMap;
import java.util.UUID;

public class TaxUser {

    public String uuid;
    public String name;
    public long lastPayement;
    public int taxnotpayed;
    public long taxpayedoffline;
    public boolean lockdown;
    public boolean islandRemoved;


    public TaxUser (OfflinePlayer p){
        this.uuid = p.getUniqueId().toString();
        this.name = p.getName();
        this.lastPayement = 0;
        this.taxnotpayed = 0;
        this.taxpayedoffline = 0;
        this.islandRemoved = false;
        this.lockdown = false;
         SkyTax.getSkyTax().getUsers().put(this.uuid,this);
    }

    public TaxUser (String uuid , String name, long lastPayement, int taxnotpayed,long taxpayedoffline, boolean islandRemoved, boolean lockdown,boolean addToUsers){
        this.uuid = uuid;
        this.name = name;
        this.lastPayement = lastPayement;
        this.taxnotpayed = taxnotpayed;
        this.taxpayedoffline = taxpayedoffline;
        this.islandRemoved = islandRemoved;
        this.lockdown = lockdown;
        if (addToUsers){
             SkyTax.getSkyTax().getUsers().put(this.uuid,this);
        }
    }

    public OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    public static TaxUser getUser(String uuid) {
        return SkyTax.getSkyTax().getUsers().get(uuid);
    }

    public static TaxUser getUser(UUID uuid) {
        return getUser(uuid.toString());
    }

    public static TaxUser getUser(OfflinePlayer p) {
        if (p == null) return null;
        return  SkyTax.getSkyTax().getUsers().containsKey(p.getUniqueId().toString()) ?  SkyTax.getSkyTax().getUsers().get(p.getUniqueId().toString()) : new TaxUser(p);
    }

}
