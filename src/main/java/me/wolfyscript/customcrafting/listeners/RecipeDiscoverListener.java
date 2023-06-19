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

package me.wolfyscript.customcrafting.listeners;

import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

public class RecipeDiscoverListener implements Listener {

    private final CustomCrafting customCrafting;

    public RecipeDiscoverListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {
        org.bukkit.NamespacedKey key = event.getRecipe();
        if (key.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            if (key.getKey().startsWith("cc_placeholder.")) {
                event.setCancelled(true);
                return;
            }
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
