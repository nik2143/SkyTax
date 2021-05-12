package it.nik2143.skytax;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.IEssentialsSpawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class Utils {

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void teleportToSpawn(Player player){
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("EssentialsSpawn")){
            player.teleport(((IEssentialsSpawn) Bukkit.getServer().getPluginManager().getPlugin("EssentialsSpawn")).getSpawn(((Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials")).getUser(player).getGroup()));
            return;
        }
        player.performCommand(SkyTax.getSkyTax().getConfiguration().getOrDefault("spawn-command","/spawn"));
    }
}
