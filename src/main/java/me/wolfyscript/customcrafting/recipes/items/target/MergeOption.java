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

package me.wolfyscript.customcrafting.recipes.items.target;

import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAutoDetect;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MergeOption {

    private int[] slots;
    private List<MergeAdapter> adapters;

    protected MergeOption() {
    }

    public MergeOption(int[] slots, MergeAdapter... adapters) {
        this.slots = slots;
        this.adapters = Arrays.asList(adapters);
    }

    public MergeOption(MergeOption mergeOption) {
        this.slots = mergeOption.slots.clone();
        this.adapters = mergeOption.adapters.stream().map(MergeAdapter::clone).toList();
    }

    /**
     * Merges the nbt of recipes onto the resulting {@link ItemStack} using the specified {@link MergeAdapter}s.
     * The order in which the adapters are called is the same order as they were added.
     *
     * @param recipeData   The {@link RecipeData}, that contains all the data of the pre-crafted recipe, like ingredients and their slots, result, and the recipe itself.
     * @param player       The player that has crafted the item. <strong>Might be null! e.g. Furnaces, and other workstations without player interaction!</strong>
     * @param block        The block that has processed the recipe. <strong>Might be null! e.g. 2x2 player crafting grid, inventories with an associated block, etc.!</strong>
     * @param customResult The {@link CustomItem} of the crafted item.
     * @param result       The actual manipulable result {@link ItemStack}. <strong>Previous adapters might have already manipulated this item!</strong>
     * @return The final manipulated resulting {@link ItemStack} of this option, that will be passed to the next option if one is available.
     */
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        var currentItem = result;
        for (MergeAdapter adapter : adapters) {
            currentItem = adapter.merge(recipeData, player, block, customResult, currentItem);
        }
        return currentItem;
    }

    public int[] getSlots() {
        return slots;
    }

    public void setSlots(int[] slots) {
        this.slots = slots;
    }

    @JsonProperty("adapters")
    public void setAdapters(List<MergeAdapter> adapters) {
        this.adapters = adapters;
        this.adapters.forEach(adapter -> adapter.setSlots(slots));
    }

    public MergeOption clone() {
        return new MergeOption(this);
    }
}
