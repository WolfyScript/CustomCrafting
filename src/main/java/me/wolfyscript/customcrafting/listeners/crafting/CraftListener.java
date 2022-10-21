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

package me.wolfyscript.customcrafting.listeners.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

import java.util.List;

public class CraftListener implements Listener {

    private final CustomCrafting customCrafting;
    private final CraftManager craftManager;

    public CraftListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.craftManager = customCrafting.getCraftManager();

        if (customCrafting.getConfigHandler().getConfig().isNMSBasedCrafting()) {
            customCrafting.getLogger().info("Using NMS Based Crafting Recipes. If you encounter any issues, please disable it!");
            Bukkit.getPluginManager().registerEvents(new NMSBasedCraftRecipeHandler(customCrafting, craftManager), customCrafting);
        } else {
            Bukkit.getPluginManager().registerEvents(new EventBasedCraftRecipeHandler(customCrafting, craftManager), customCrafting);
        }
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE) && !customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {
        org.bukkit.NamespacedKey key = event.getRecipe();
        if (key.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            CustomRecipe<?> recipe = customCrafting.getRegistries().getRecipes().get(NamespacedKey.fromBukkit(key));
            if (recipe instanceof ICustomVanillaRecipe<?> vanillaRecipe && vanillaRecipe.isVisibleVanillaBook()) {
                event.setCancelled(recipe.isHidden() || recipe.isDisabled());
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Automatically discovers available custom recipes for players.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        List<org.bukkit.NamespacedKey> discoveredCustomRecipes = player.getDiscoveredRecipes().stream().filter(namespacedKey -> namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)).toList();
        customCrafting.getRegistries().getRecipes().getAvailable(player).stream()
                .filter(recipe -> recipe instanceof ICustomVanillaRecipe<?> vanillaRecipe && vanillaRecipe.isAutoDiscover())
                .map(recipe -> new org.bukkit.NamespacedKey(recipe.getNamespacedKey().getNamespace(), recipe.getNamespacedKey().getKey()))
                .filter(namespacedKey -> !discoveredCustomRecipes.contains(namespacedKey))
                .forEach(player::discoverRecipe);
    }
}
