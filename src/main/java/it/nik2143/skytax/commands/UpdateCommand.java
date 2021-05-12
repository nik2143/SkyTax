package it.nik2143.skytax.commands;

import de.leonhard.storage.Yaml;
import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class UpdateCommand {

    private final CommandSender sender;

    public UpdateCommand(CommandSender sender){
        this.sender = sender;
    }

    public void execute(){
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        if (sender.hasPermission("skytax.update")) {
            if (!SkyTax.getSkyTax().outdated) {
                sender.sendMessage(Utils.color(prefix + "&7Updated Plugin! There are no new versions available!"));
            } else {
                TextComponent tc = new TextComponent(Utils.color(prefix + "&7There is a new update available (" + SkyTax.getSkyTax().getNewVersion() + ")"));
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/81159"));
                sender.spigot().sendMessage(tc);
            }
        } else {
            sender.sendMessage(Utils.color( prefix +  SkyTax.getSkyTax().getLanguage().getString("permission-error")));
        }
    }

}
