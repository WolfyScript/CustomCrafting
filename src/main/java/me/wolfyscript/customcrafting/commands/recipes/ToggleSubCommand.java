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

package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToggleSubCommand extends AbstractSubCommand {

    public ToggleSubCommand(CustomCrafting customCrafting) {
        super("toggle", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player && ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.toggle") && args.length > 0) {
            String id = args[0];
            if (id.contains(":")) {
                var namespacedKey = me.wolfyscript.utilities.util.NamespacedKey.of(id);
                if (customCrafting.getDisableRecipesHandler().getRecipes().contains(namespacedKey)) {
                    sender.sendMessage("Enabled recipe " + id);
                    customCrafting.getDisableRecipesHandler().getRecipes().remove(namespacedKey);
                } else {
                    sender.sendMessage("Disabled recipe " + id);
                    customCrafting.getDisableRecipesHandler().getRecipes().add(namespacedKey);
                    if (namespacedKey != null) {
                        Bukkit.getOnlinePlayers().forEach(player -> player.undiscoverRecipe(namespacedKey.toBukkit(customCrafting)));
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        List<String> recipes = customCrafting.getDataHandler().getBukkitNamespacedKeys();
        recipes.addAll(CCRegistry.RECIPES.get(CraftingRecipe.class).stream().map(recipe -> recipe.getNamespacedKey().toString()).collect(Collectors.toSet()));
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}
