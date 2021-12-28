package it.nik2143.skytax.commands.subcommands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.commands.Subcommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class UpdateCommand extends Subcommand {

    public UpdateCommand() {
        super(0);
    }

    @Override
    public String getPermission(){
        return "skytax.update";
    }

    @Override
    public String getHelpMessage() {
        return Utils.color("&d/skytax update &8- &fCheck if there is an update");
    }

    @Override
    public void execute(CommandSender sender,String[] args) {
        String prefix = SkyTax.getSkyTax().getLanguage().getString("prefix");
        if (!SkyTax.getSkyTax().isOutdated()) {
            sender.sendMessage(Utils.color(prefix + "&7Updated Plugin! There are no new versions available!"));
        } else {
            TextComponent tc = new TextComponent(Utils.color(prefix + "&7There is a new update available (" + SkyTax.getSkyTax().getNewVersion() + ")"));
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/81159"));
            sender.spigot().sendMessage(tc);
        }
    }
}
