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

import java.io.IOException;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ResourceLoader implements Comparable<ResourceLoader>, Keyed {

    protected final NamespacedKey key;
    protected final CustomCrafting customCrafting;
    protected final MainConfig config;
    protected final WolfyUtilities api;
    protected final ObjectMapper objectMapper;
    private int priority = 0;
    private boolean replaceData = false;

    protected ResourceLoader(CustomCrafting customCrafting, NamespacedKey key) {
        this.key = key;
        this.api = customCrafting.getApi();
        this.config = customCrafting.getConfigHandler().getConfig();
        this.customCrafting = customCrafting;
        this.objectMapper = customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper();
    }

    public abstract void load();

    public void load(boolean upgrade) {
        load();
        if (upgrade) {
            api.getConsole().info("Updating Items & Recipes to the latest format..");
            save();
            api.getConsole().info("Loading updated Items & Recipes...");
            load();
        }
    }

    /**
     * Sets the new value for the "replace data" option.<br>
     * If set to true, this loader overrides already existing recipes that might be loaded by other loaders beforehand.
     *
     * @param replaceData The new boolean value for the "replace data" option
     */
    public void setReplaceData(boolean replaceData) {
        this.replaceData = replaceData;
    }

    /**
     * Gets the value of the "replace data" option.<br>
     * If set to true, this loader overrides already existing recipes that might be loaded by other loaders beforehand.
     *
     * @return replaceData True if enabled; otherwise false
     */
    public boolean isReplaceData() {
        return replaceData;
    }

    public void save() {
        api.getRegistries().getCustomItems().entrySet().forEach(entry -> ItemLoader.saveItem(this, entry.getKey(), entry.getValue()));
        customCrafting.getRegistries().getRecipes().values().forEach(recipe -> recipe.save(this, null));
    }

    /**
     * Saves the specified recipe
     *
     * @param recipe The recipe to save
     * @return true if the recipe was saved successfully; otherwise false.
     */
    public abstract boolean save(CustomRecipe<?> recipe);

    /**
     * Saves the specified CustomItem
     *
     * @param item The recipe to save
     * @return true if the recipe was saved successfully; otherwise false.
     */
    public abstract boolean save(CustomItem item);

    /**
     * Deletes the specified recipe
     *
     * @param recipe The recipe to delete
     * @return true if the recipe was successfully deleted; otherwise false
     */
    public abstract boolean delete(CustomRecipe<?> recipe) throws IOException;

    /**
     * Deletes the specified CustomItem
     *
     * @param item The item to delete
     * @return true if the item was successfully deleted; otherwise false
     */
    public abstract boolean delete(CustomItem item) throws IOException;

    /**
     * Gets the priority of this loader.<br>
     * Loaders of higher priority are loaded first, therefor their recipes and items take priority over the once loaded after.
     *
     * @return The integer priority of this loader.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this loader.<br>
     * Loaders of higher priority are loaded first, therefor their recipes and items take priority over the once loaded after.
     *
     * @param priority The new priority of this loader.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceLoader that)) return false;
        return priority == that.priority && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, priority);
    }

    @Override
    public int compareTo(@NotNull ResourceLoader other) {
        return Integer.compare(other.priority, priority);
    }
}
