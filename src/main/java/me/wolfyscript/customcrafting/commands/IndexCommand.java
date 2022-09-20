/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
