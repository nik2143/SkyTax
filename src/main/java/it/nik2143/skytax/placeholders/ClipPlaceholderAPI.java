package it.nik2143.skytax.placeholders;

import de.leonhard.storage.Yaml;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.TaxUser;
import it.nik2143.skytax.hooks.IslandsMethods;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClipPlaceholderAPI extends PlaceholderExpansion {

    private final IslandsMethods islandsMethods;
    private final Yaml config;

    public ClipPlaceholderAPI(){
        this.islandsMethods= SkyTax.getSkyTax().getIslandsMethods();
        this.config = SkyTax.getSkyTax().getConfiguration();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "skytax";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Nik2143";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.2";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist()
    {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        DateFormat dataFormat;
        switch (params.toLowerCase()){
            case "shouldpay":
                return islandsMethods.shouldPayTax(TaxUser.getUser(player)) ? "true" : "false";
            case "lastpayement":
                return String.valueOf(TaxUser.getUser(player).lastPayement);
            case "lastpayement_formatted":
                dataFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
                return dataFormat.format(new Date(TaxUser.getUser(player).lastPayement*1000));
            case "newpayement":
                return String.valueOf((TaxUser.getUser(player).lastPayement + config.getInt("TimeToPay")));
            case "newpayement_formatted":
                dataFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
                return dataFormat.format(new Date((TaxUser.getUser(player).lastPayement + config.getInt("TimeToPay"))*1000));
            case "lockdown":
                return TaxUser.getUser(player).lockdown ? "true" : "false";
        }
        return null;
    }
}
