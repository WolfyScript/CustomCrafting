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
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResultTarget {

    private final List<MergeOption> mergeOptions;

    protected ResultTarget() {
        this.mergeOptions = new ArrayList<>();
    }

    protected ResultTarget(ResultTarget target) {
        this.mergeOptions = target.mergeOptions.stream().map(MergeOption::clone).toList();
    }

    /**
     * Merges the nbt of recipes onto the resulting {@link ItemStack} using the specified {@link MergeOption}s
     *
     * @param recipeData       The {@link RecipeData}, that contains all the data of the pre-crafted recipe, like ingredients and their slots, result, and the recipe itself.
     * @param player           The player that has crafted the item. <strong>Might be null! e.g. Furnaces, and other workstations without player interaction!</strong>
     * @param block            The block that has processed the recipe. <strong>Might be null! e.g. for the 2x2 player crafting grid!</strong>
     * @param customItemResult The {@link CustomItem} of the crafted item.
     * @param result           The actual manipulable result {@link ItemStack}. <strong>Previous adapters might have already manipulated this item!</strong>
     * @return The final manipulated resulting {@link ItemStack}.
     */
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customItemResult, ItemStack result) {
        var currentItem = result;
        for (MergeOption mergeOption : mergeOptions) {
            currentItem = mergeOption.merge(recipeData, player, block, customItemResult, result);
        }
        return currentItem;
    }

    /**
     * Adds a {@link MergeOption} to the Target.
     *
     * @param option The MergeOption to add.
     * @return The {@link ResultTarget} for chaining this method.
     */
    public ResultTarget addOption(MergeOption option) {
        mergeOptions.add(option);
        return this;
    }


}
