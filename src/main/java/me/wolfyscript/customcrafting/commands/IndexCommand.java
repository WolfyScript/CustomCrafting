package me.wolfyscript.customcrafting.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class IndexCommand extends Command {

    private final HashMap<String, AbstractSubCommand> subCommands = new HashMap<>();

    protected IndexCommand(@NotNull String name) {
        super(name);
    }

    protected IndexCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public void registerSubCommand(AbstractSubCommand subCommand) {
        subCommands.put(subCommand.getLabel(), subCommand);
        subCommand.getAlias().forEach(s -> subCommands.putIfAbsent(s, subCommand));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            if (subCommands.containsKey(args[0])) {
                return subCommands.get(args[0]).onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return false;
    }

    @Override
    public @NotNull
    List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] strings) throws IllegalArgumentException {
        List<String> results = new ArrayList<>();
        if (strings.length > 1) {
            AbstractSubCommand subCommand = subCommands.get(strings[0]);
            if (subCommand != null) {
                results = subCommand.onTabComplete(sender, strings[0], Arrays.copyOfRange(strings, 1, strings.length));
            }
        } else if (sender instanceof Player) {
            StringUtil.copyPartialMatches(strings[0], subCommands.keySet(), results);
        }
        if (results == null) {
            results = new ArrayList<>();
        }
        Collections.sort(results);
        return results;
    }

    public Map<String, AbstractSubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
