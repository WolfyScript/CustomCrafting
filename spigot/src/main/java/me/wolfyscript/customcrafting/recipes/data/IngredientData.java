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

package me.wolfyscript.customcrafting.recipes.data;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import org.bukkit.inventory.ItemStack;

/**
 * Contains the data for a single Ingredient.
 * <p>
 * It contains:<br>
 * - the recipe slot this data belongs to,<br>
 * - the Ingredient of the recipe,<br>
 * - the CustomItem that was chosen from that Ingredient,<br>
 * - and the created ItemStack of the CustomItem.
 */
public record IngredientData(int matrixSlot, int recipeSlot, Ingredient ingredient, StackReference reference, ItemStack itemStack) {

    @Deprecated(forRemoval = true, since = "4.16.9")
    public IngredientData(int matrixSlot, int recipeSlot, Ingredient ingredient, CustomItem customItem, ItemStack itemStack) {
         this(matrixSlot, recipeSlot, ingredient, fromCustomItem(customItem, itemStack), itemStack);
    }

    private static StackReference fromCustomItem(CustomItem customItem, ItemStack itemStack) {
        if (customItem.hasNamespacedKey()) {
            // It's a saved CustomItem and bound to WU
            return new StackReference(WolfyUtilCore.getInstance(), new WolfyUtilsStackIdentifier(customItem.getNamespacedKey()), customItem.getWeight(), customItem.getAmount(), itemStack);
        }
        return customItem.stackReference();
    }

    @Deprecated(forRemoval = true, since = "4.16.9")
    public CustomItem customItem() {
        return CustomItem.of(reference.convert());
    }

}
