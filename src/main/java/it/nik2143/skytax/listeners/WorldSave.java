package it.nik2143.skytax.listeners;

import it.nik2143.skytax.SkyTax;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldSave implements Listener {

    long lastsave;

    @EventHandler
    public void onWorldSave(WorldSaveEvent e){
        if (System.currentTimeMillis() > lastsave+60000) {
            lastsave = System.currentTimeMillis();
            SkyTax.getSkyTax().getDataManager().saveDataFuture();
        }
    }


}
