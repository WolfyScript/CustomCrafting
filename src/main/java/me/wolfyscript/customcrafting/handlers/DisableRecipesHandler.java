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

package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DisableRecipesHandler {

    private final CustomCrafting customCrafting;
    private final MainConfig config;

    private final Set<NamespacedKey> recipes = new HashSet<>();
    private final Map<org.bukkit.NamespacedKey, Recipe> cachedRecipes = new HashMap<>();

    public DisableRecipesHandler(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.config = customCrafting.getConfigHandler().getConfig();

        if (!config.getDisabledRecipes().isEmpty()) {
            recipes.addAll(config.getDisabledRecipes().parallelStream().map(NamespacedKey::of).filter(Objects::nonNull).toList());
            recipes.forEach(key -> {
                if (customCrafting.getRegistries().getRecipes().has(key)) {
                    disableRecipe(Objects.requireNonNull(customCrafting.getRegistries().getRecipes().get(key)));
                } else {
                    org.bukkit.NamespacedKey bukkitKey = org.bukkit.NamespacedKey.fromString(key.toString());
                    if (bukkitKey != null) {
                        disableBukkitRecipe(bukkitKey);
                    }
                }
            });
        }
    }

    public void saveDisabledRecipes() {
        config.setDisabledRecipes(recipes);
        config.save();
    }

    /**
     * @return A list of recipes that are disabled.
     */
    public Set<NamespacedKey> getRecipes() {
        return recipes;
    }

    public void toggleRecipe(CustomRecipe<?> recipe) {
        if (recipe.isDisabled()) {
            enableRecipe(recipe);
        } else {
            disableRecipe(recipe);
        }
    }

    /**
     * Disables the {@link CustomRecipe} and removes it's bukkit counterpart if it is an instance of {@link ICustomVanillaRecipe}.
     *
     * @param recipe The recipe to disable.
     */
    public void disableRecipe(CustomRecipe<?> recipe) {
        var namespacedKey = recipe.getNamespacedKey();
        recipes.add(namespacedKey);
        if (recipe instanceof ICustomVanillaRecipe<?>) {
            Bukkit.removeRecipe(new org.bukkit.NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey()));
        }
        saveDisabledRecipes();
    }

    /**
     * Enables the {@link me.wolfyscript.customcrafting.recipes.CustomRecipe} and adds the Bukkit recipe if it is an instance of {@link ICustomVanillaRecipe}.
     *
     * @param recipe The recipe to enable.
     */
    public void enableRecipe(CustomRecipe<?> recipe) {
        var namespacedKey = recipe.getNamespacedKey();
        if (recipe instanceof ICustomVanillaRecipe<?> customVanillaRecipe) {
            Bukkit.addRecipe(customVanillaRecipe.getVanillaRecipe());
        }
        recipes.remove(namespacedKey);
        saveDisabledRecipes();
    }

    public boolean isBukkitRecipeDisabled(org.bukkit.NamespacedKey namespacedKey) {
        return recipes.contains(NamespacedKey.fromBukkit(namespacedKey));
    }

    public void toggleBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        if (isBukkitRecipeDisabled(namespacedKey)) {
            enableBukkitRecipe(namespacedKey);
        } else {
            disableBukkitRecipe(namespacedKey);
        }
    }

    /**
     * Disables the specified bukkit recipe.
     * The {@link Recipe} is cached before being removed from Bukkit, to easily add it
     * on runtime when enabled using {@link #enableBukkitRecipe(org.bukkit.NamespacedKey)}.
     *
     * @param namespacedKey The {@link org.bukkit.NamespacedKey} of the recipe to remove.
     */
    public void disableBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        Recipe bukkitRecipe = Bukkit.getRecipe(namespacedKey);
        if (bukkitRecipe != null) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.undiscoverRecipe(namespacedKey);
            }
            if (!namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
                recipes.add(NamespacedKey.fromBukkit(namespacedKey));
                //Cache the recipe if it is a Bukkit recipe, so we can add it again at runtime, without the requirement to reload everything.
                cachedRecipes.put(namespacedKey, bukkitRecipe);
            }
            Bukkit.removeRecipe(namespacedKey);
        }
        saveDisabledRecipes();
    }

    /**
     * Enables the specified recipe again and tries to add the previously cached
     * {@link Recipe} back into Bukkit.
     *
     * @param namespacedKey The {@link org.bukkit.NamespacedKey} of the recipe to enable.
     */
    public void enableBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        recipes.remove(NamespacedKey.fromBukkit(namespacedKey));
        Recipe bukkitRecipe = cachedRecipes.get(namespacedKey);
        if (bukkitRecipe != null) {
            Bukkit.addRecipe(bukkitRecipe);
        }
        saveDisabledRecipes();
    }

    public List<Recipe> getCachedVanillaRecipes() {
        return List.copyOf(cachedRecipes.values());
    }
}
