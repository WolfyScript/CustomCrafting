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

package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class RecipeList {

    private String namespace;
    private String folder;
    private final Deque<String> folders;
    private int page;
    private RecipeType<?> filterType;
    private Class<? extends Recipe> filterClass;

    public RecipeList() {
        this.namespace = null;
        this.page = 0;
        this.folders = new ArrayDeque<>();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folders.peekFirst();
    }

    public void goUpFolder() {
        folders.pop();
    }

    public void goDownFolder(String folder) {
        folders.push(folder);
    }

    public int getPage(int maxPages) {
        if (this.page > maxPages) {
            this.page = maxPages;
        }
        return this.page;
    }

    public int getMaxPages(int size) {
        return size / 45 + (size % 45 > 0 ? 1 : 0);
    }

    public void setFilterType(RecipeType<?> filterType) {
        this.filterType = filterType;
        if (filterType != null) {
            this.filterClass = switch (filterType.getType()) {
                case CRAFTING_SHAPED -> ShapedRecipe.class;
                case CRAFTING_SHAPELESS -> ShapelessRecipe.class;
                case SMOKER -> SmokingRecipe.class;
                case FURNACE -> FurnaceRecipe.class;
                case BLAST_FURNACE -> BlastingRecipe.class;
                case CAMPFIRE -> CampfireRecipe.class;
                case SMITHING -> SmithingRecipe.class;
                case STONECUTTER -> StonecuttingRecipe.class;
                default -> null;
            };
        }
    }

    public RecipeType<?> getFilterType() {
        return filterType;
    }

    public void filterCustomRecipes(List<CustomRecipe<?>> recipes) {
        if (filterType != null) {
            recipes.removeIf(recipe -> !filterType.isInstance(recipe));
        }
    }

    public boolean filterBukkitRecipe(Recipe recipe) {
        return filterClass == null || filterClass.isInstance(recipe);
    }

    public void filterVanillaRecipes(List<NamespacedKey> recipes) {
        if (filterClass != null) {
            recipes.removeIf(key -> !filterClass.isInstance(Bukkit.getRecipe(key.bukkit())));
        }
    }
}
