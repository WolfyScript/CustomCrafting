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

package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataBaseSubCommand extends AbstractSubCommand {

    private final List<String> DATABASE = Arrays.asList("export");

    public DataBaseSubCommand(CustomCrafting customCrafting) {
        super("database", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player p) {
            var chat = api.getChat();
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.database")) {
                if (customCrafting.getDataHandler().getDatabaseLoader() != null) {
                    chat.sendMessage(p, "&4No Database found!");
                    return true;
                }
                if (args.length >= 1) {
                    switch (args[0]) {
                        case "export_recipes" -> {
                            chat.sendMessage(p, "Exporting recipes to Database...");
                            new Thread(() -> {
                                var dataBaseHandler = customCrafting.getDataHandler().getDatabaseLoader();
                                customCrafting.getRegistries().getRecipes().values().forEach(dataBaseHandler::save);
                                api.getConsole().fine("Successfully exported recipes to database");
                            }).start();
                        }
                        case "export_items" -> {
                            chat.sendMessage(p, "Exporting custom items to Database...");
                            new Thread(() -> {
                                var dataBaseHandler = customCrafting.getDataHandler().getDatabaseLoader();
                                api.getRegistries().getCustomItems().forEach(dataBaseHandler::save);
                                api.getConsole().fine("Successfully exported custom items to database");
                            }).start();
                        }
                        default -> chat.sendMessage(p, "commands.database.invalid_usage");
                    }
                } else {
                    chat.sendMessage(p, "commands.database.invalid_usage");
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] strings) {
        List<String> results = strings.length == 1 ? StringUtil.copyPartialMatches(strings[0], DATABASE, new ArrayList<>()) : new ArrayList<>();
        Collections.sort(results);
        return results;
    }
}
