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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.tuple.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeView;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.registry.RegistryRecipes;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeLookupCommand extends AbstractSubCommand {

    private final RegistryRecipes registryRecipes;

    public RecipeLookupCommand(CustomCrafting customCrafting) {
        super("lookup", new ArrayList<>(), customCrafting);
        this.registryRecipes = customCrafting.getRegistries().getRecipes();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player player && ChatUtils.checkPerm(player, "customcrafting.cmd.recipes_lookup") && args.length > 0) {
            WolfyUtilsBukkit api = customCrafting.getApi();
            if (args[0].contains(":")) {
                NamespacedKey key = api.getIdentifiers().getNamespaced(args[0]);
                if (key != null) {
                    CustomRecipe<?> customRecipe = registryRecipes.get(key);
                    if (customRecipe != null) {
                        if (customRecipe.checkCondition("permission", Conditions.Data.of(player))) { //Make sure the player has access to the recipe
                            GuiHandler<CCCache> guiHandler = api.getInventoryAPI(CCCache.class).getGuiHandler(player);
                            CCCache cache = guiHandler.getCustomCache();
                            cache.getCacheRecipeView().setRecipe(customRecipe);
                            customRecipe.prepareMenu(guiHandler, guiHandler.getInvAPI().getGuiCluster(ClusterRecipeView.KEY));
                            Bukkit.getScheduler().runTaskLater(customCrafting, () -> guiHandler.openWindow(ClusterRecipeView.RECIPE_SINGLE), 1);
                            return true;
                        }
                        api.getChat().sendMessage((Player) sender, "$commands.recipes.invalid_recipe_permission$", new Pair<>("%recipe%", args[0]));
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
    protected List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player player) {
            List<NamespacedKey> keys = customCrafting.getRegistries().getRecipes().getAvailable(player).stream().map(CustomRecipe::getNamespacedKey).collect(Collectors.toList());
            return NamespacedKeyUtils.getPartialMatches(args[args.length - 1], keys).stream().map(NamespacedKey::toString).collect(Collectors.toList());
        }
        return null;
    }
}
