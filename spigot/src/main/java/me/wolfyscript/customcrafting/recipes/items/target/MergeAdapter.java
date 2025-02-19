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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = { "key" })
public abstract class MergeAdapter implements Keyed {

    @JsonInclude
    protected final NamespacedKey key;

    /**
     * These are the slots selected by the merge option.
     * For crafting recipes they indicate the Ingredient id/s.
     * For any other recipe with fixed slots they indicate the inventory slot.
     */
    @JsonIgnore
    protected int[] slots;

    protected MergeAdapter(NamespacedKey key) {
        this.key = key;
    }

    protected MergeAdapter(MergeAdapter adapter) {
        this.key = new NamespacedKey(adapter.key.getNamespace(), adapter.key.getKey());
        this.slots = adapter.slots.clone();
    }

    @JsonGetter("key")
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    void setSlots(int[] slots) {
        this.slots = slots;
    }

    /**
     * Only called when {@link #merge(RecipeData, Player, Block, StackReference, ItemStack)} is unimplemented, for backwards compatibility!<br>
     * <b>Implement {@link #merge(RecipeData, Player, Block, StackReference, ItemStack)} Instead!</b>
     *
     * @param recipeData   The {@link RecipeData}, that contains all the data of the pre-crafted recipe, like ingredients and their slots, result, and the recipe itself.
     * @param player       The player that has crafted the item. <strong>Might be null! e.g. Furnaces, and other workstations without player interaction!</strong>
     * @param block        The block that has processed the recipe. <strong>Might be null! e.g. for the 2x2 player crafting grid!</strong>
     * @param customResult The {@link CustomItem} of the crafted item.
     * @param result       The actual manipulable result {@link ItemStack}. <strong>Previous adapters might have already manipulated this item!</strong>
     * @return The manipulated {@link ItemStack} that should be passed to the next adapter or set as the end result.
     * @deprecated CustomItems are no longer used as references! <b>Implement {@link #merge(RecipeData, Player, Block, StackReference, ItemStack)} instead!</b>
     */
    @Deprecated(forRemoval = true, since = "4.16.9")
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        return result;
    }

    /**
     * Called when the data is merged inside of recipes like Furnace, Smithing Table, etc.
     *
     * @param recipeData   The {@link RecipeData}, that contains all the data of the pre-crafted recipe, like ingredients and their slots, result, and the recipe itself.
     * @param player       The player that has crafted the item. <strong>Might be null! e.g. Furnaces, and other workstations without player interaction!</strong>
     * @param block        The block that has processed the recipe. <strong>Might be null! e.g. for the 2x2 player crafting grid!</strong>
     * @param resultReference The reference to the original stack
     * @param result       The actual manipulable result {@link ItemStack}. <strong>Previous adapters might have already manipulated this item!</strong>
     * @return The manipulated {@link ItemStack} that should be passed to the next adapter or set as the end result.
     */
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        return merge(recipeData,
                player,
                block,
                resultReference.identifier()
                        .map(identifier -> identifier instanceof WolfyUtilsStackIdentifier wuIdentifier ? wuIdentifier : null)
                        .map(wolfyUtilsStackIdentifier -> wolfyUtilsStackIdentifier.customItem().orElse(new CustomItem(Material.AIR)))
                        .orElse(new CustomItem(resultReference)),
                result
        );
    }

    public abstract MergeAdapter clone();

    /**
     * @deprecated Not called! Replaced with {@link #merge(RecipeData, Player, Block, CustomItem, ItemStack)}!
     */
    @Deprecated
    public ItemStack merge(ItemStack[] ingredients, @Nullable Player player, CustomItem customResult, ItemStack result) {
        return result;
    }
}
