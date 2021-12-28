package it.nik2143.skytax.commands;

import it.nik2143.skytax.SkyTax;
import it.nik2143.skytax.Utils;
import it.nik2143.skytax.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class SkyTaxCommand implements CommandExecutor, TabCompleter {

    private final HashMap<String, Subcommand> subcommands;

    public SkyTaxCommand() {
        subcommands = new HashMap<>();
        subcommands.put("reload", new ReloadCommand());
        subcommands.put("update", new UpdateCommand());
        subcommands.put("about",new AboutCommand());
        subcommands.put("isunlock",new IsUnlockCommand());
        subcommands.put("forceunlock",new ForceUnlockCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender,null);
            return true;
        }
        String commandName = args[0].toLowerCase(Locale.ROOT);
        if (subcommands.containsKey(commandName)) {
            Subcommand subcommand = subcommands.get(commandName);
            if (subcommand.getPermission()!=null && !sender.hasPermission(subcommand.getPermission())){
                sender.sendMessage(Utils.color(
                        SkyTax.getSkyTax().getLanguage().getString("prefix")
                        + SkyTax.getSkyTax().getLanguage().getString("permissions-error")));
                return true;
            }
            if (args.length-1<subcommand.getMinArgs()){
                sendHelp(sender,subcommand);
                return true;
            }
            if (sender instanceof Player){
                subcommands.get(commandName).execute((Player)sender,args);
            } else {
                subcommands.get(commandName).execute(sender,args);
            }
            return true;
        }
        sendHelp(sender,null);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length==1){
            final List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0],
                    subcommands.entrySet().stream()
                            .filter(e->e.getValue().getPermission()==null || sender.hasPermission(e.getValue().getPermission()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList()),
                    completions);
            Collections.sort(completions);
            return completions;
        }
        String commandName = args[0].toLowerCase(Locale.ROOT);
        if (subcommands.containsKey(commandName)){
            return subcommands.get(commandName).tabAutoComplete(sender,args);
        }
        return Collections.emptyList();
    }

    private void sendHelp(CommandSender sender,@Nullable Subcommand subcommand){
        sender.sendMessage(Utils.color("&6&lSky&e&lTax"));
        sender.sendMessage(Utils.color("&8&m&l-*-------------------*-"));
        if (subcommand!=null){
            sender.sendMessage(subcommand.getHelpMessage());
        } else {
            subcommands.values().stream()
                    .filter(subcommand1 -> subcommand1.getPermission()==null || sender.hasPermission(subcommand1.getPermission()))
                    .forEach(subcommand1 -> sender.sendMessage(subcommand1.getHelpMessage()));
        }
        sender.sendMessage(Utils.color("&8&m&l-*-------------------*-"));
    }
}
