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

import java.io.File;
import java.io.IOException;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.wolfyscript.utilities.dependency.Dependency;
import com.wolfyscript.utilities.verification.VerificationResult;
import com.wolfyscript.utilities.verification.Verifier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.compatibility.PluginIntegration;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ResourceLoader implements Comparable<ResourceLoader>, Keyed {

    protected final NamespacedKey key;
    protected final CustomCrafting customCrafting;
    protected final MainConfig config;
    protected final WolfyUtilities api;
    protected final ObjectMapper objectMapper;
    private int priority = 0;
    private boolean replaceData = false;
    private boolean doneLoading = false;

    protected final Multimap<CustomRecipe<?>, Dependency> recipeDependencies = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    protected final List<VerificationResult<? extends CustomRecipe<?>>> invalidRecipes = new ArrayList<>();
    protected final List<NamespacedKey> failedRecipes = new ArrayList<>();
    private final Deque<ScheduledPluginIntegrationTask> scheduledPluginIntegrationTasks = new ArrayDeque<>();

    protected ResourceLoader(CustomCrafting customCrafting, NamespacedKey key) {
        this.key = key;
        this.api = customCrafting.getApi();
        this.config = customCrafting.getConfigHandler().getConfig();
        this.customCrafting = customCrafting;
        this.objectMapper = customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper();
    }

    protected abstract void load();

    public void load(boolean upgrade) {
        doneLoading = false;
        load();
        if (upgrade) {
            if (!backup()) {
                api.getConsole().warn("Aborting Items & Recipes config upgrade to the latest format!");
                return;
            }
            api.getConsole().info("Updating Items & Recipes to the latest format..");
            save();
            api.getConsole().info("Loading updated Items & Recipes...");
            load();
        }

        doneLoading = true;
        // Run scheduled plugin loaders
        runScheduledPluginIntegrations();
    }

    public synchronized void schedulePluginIntegration(ScheduledPluginIntegrationTask task) {
        scheduledPluginIntegrationTasks.add(task);
        if (!doneLoading) {
            return;
        }
        // Load directly when it is already done loading
        runScheduledPluginIntegrations();
    }

    /**
     * Loads the recipes for plugin integrations that were scheduled while recipes were still loading
     */
    private synchronized void runScheduledPluginIntegrations() {
        doneLoading = false;
        while (!scheduledPluginIntegrationTasks.isEmpty()) {
            var task = scheduledPluginIntegrationTasks.pop();
            int amount = validatePending(task.integration());
            task.callback().accept(task.integration(), amount);
        }
        doneLoading = true;
    }

    protected abstract int validatePending(PluginIntegration pluginIntegration);

    protected static <T extends CustomRecipe<?>> Optional<VerificationResult<T>> validateRecipe(T recipe) {
        var validator = (Verifier<T>) CustomCrafting.inst().getRegistries().getVerifiers().get(recipe.getRecipeType().getNamespacedKey());
        if (validator == null) return Optional.empty();
        return Optional.of(validator.validate(recipe));
    }

    protected void markInvalid(VerificationResult<? extends CustomRecipe<?>> recipe) {
        synchronized (invalidRecipes) {
            invalidRecipes.add(recipe);
        }
    }

    protected void markFailed(NamespacedKey recipe) {
        synchronized (failedRecipes) {
            failedRecipes.add(recipe);
        }
    }

    public Set<CustomRecipe<?>> getPendingRecipes() {
        return Collections.unmodifiableSet(recipeDependencies.keySet());
    }

    public List<VerificationResult<? extends CustomRecipe<?>>> getInvalidRecipes() {
        return Collections.unmodifiableList(invalidRecipes);
    }

    public List<NamespacedKey> getFailedRecipes() {
        return Collections.unmodifiableList(failedRecipes);
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
        backup();
        api.getRegistries().getCustomItems().entrySet().forEach(entry -> ItemLoader.saveItem(this, entry.getKey(), entry.getValue()));
        customCrafting.getRegistries().getRecipes().values().forEach(recipe -> recipe.save(this, null));
    }

    public boolean backup() { return true; }

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

    public static boolean isValidFile(File file) {
        String fileName = file.getName();
        return fileName.startsWith(".") || (!fileName.endsWith(".json") && !fileName.endsWith(".conf"));
    }

}
