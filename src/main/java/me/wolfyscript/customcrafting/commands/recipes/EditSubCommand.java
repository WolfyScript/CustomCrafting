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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditSubCommand extends AbstractSubCommand {

    public EditSubCommand(CustomCrafting customCrafting) {
        super("edit", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player player && ChatUtils.checkPerm(player, "customcrafting.cmd.recipes.edit") && args.length > 0) {
            WolfyUtilities api = customCrafting.getApi();
            if (args[0].contains(":")) {
                NamespacedKey key = NamespacedKey.of(args[0]);
                if (key != null) {
                    CustomRecipe<?> customRecipe = customCrafting.getRegistries().getRecipes().get(key);
                    if (customRecipe != null) {
                        GuiHandler<CCCache> guiHandler = api.getInventoryAPI(CCCache.class).getGuiHandler(player);
                        CCCache cache = guiHandler.getCustomCache();
                        cache.setSetting(Setting.RECIPE_CREATOR);
                        var creatorCache = cache.getRecipeCreatorCache();
                        creatorCache.setRecipeType(customRecipe.getRecipeType());
                        try {
                            creatorCache.loadRecipeIntoCache(customRecipe);
                            Bukkit.getScheduler().runTaskLater(customCrafting, () -> api.getInventoryAPI().openGui(player, new NamespacedKey(ClusterRecipeCreator.KEY, creatorCache.getRecipeType().getCreatorID())), 1);
                        } catch (IllegalArgumentException ex) {
                            api.getChat().sendMessage((Player) sender, "$commands.recipes.invalid_recipe$", new Pair<>("%recipe%", args[0]));
                        }
                    } else {
                        api.getChat().sendMessage((Player) sender, "$commands.recipes.invalid_recipe$", new Pair<>("%recipe%", args[0]));
                    }
                }
            }
        }
        return true;
    }

    @Nullable
    @Override
    protected List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        return NamespacedKeyUtils.getPartialMatches(args[args.length - 1], customCrafting.getRegistries().getRecipes().keySet()).stream().map(NamespacedKey::toString).collect(Collectors.toList());
    }
}
