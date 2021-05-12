package it.nik2143.skytax.commands;

import de.leonhard.storage.Yaml;
import it.nik2143.skytax.hooks.IslandsMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

    public class SkyTaxCommand implements CommandExecutor {

        private final IslandsMethods islandsMethods;

        public SkyTaxCommand ( IslandsMethods islandsMethods){
            this.islandsMethods = islandsMethods;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if (command.getName().equalsIgnoreCase("skytax")) {
                if (args.length >= 1) {
                    switch (args[0]){
                        case "reload":
                            new ReloadCommand(sender).execute();
                            break;
                        case "update":
                            new UpdateCommand(sender).execute();
                            break;
                        case "about":
                            new AboutCommand(sender).execute();
                            break;
                        case "forceunlock":
                            new ForceUnlockCommand(sender,args,islandsMethods).execete();
                            break;
                        case "isunlock":
                            new IsUnlockCommand(sender,islandsMethods).execute();
                            break;
                        case "help":
                            new HelpCommand(sender).execute();
                            break;
                        default:
                            new HelpCommand(sender).execute();
                            return true;
                    }
                } else {
                    new HelpCommand(sender).execute();
                    return true;
                }
            }
            return true;
        }
    }
