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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class ButtonRecipeIngredient extends ItemInputButton<CCCache> {

    ButtonRecipeIngredient(int recipeSlot) {
        super("recipe.ingredient_" + recipeSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, guiInventory, slot, event) -> {
            var ingredient = cache.getRecipeCreatorCache().getRecipeCache().getIngredient(recipeSlot);
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getSlot() == slot && clickEvent.isRightClick() && clickEvent.isShiftClick()) {
                //Since shift clicking now updates all the available ItemInputButtons we only use the first button that was clicked.
                cache.getRecipeCreatorCache().getIngredientCache().setSlot(recipeSlot);
                cache.getRecipeCreatorCache().getIngredientCache().setIngredient(ingredient != null ? ingredient : new Ingredient());
                guiHandler.openWindow(MenuIngredient.KEY);
                return true;
            }
            return ingredient != null && ingredient.getItems().isEmpty() && !ingredient.getTags().isEmpty();
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            var ingredient = cache.getRecipeCreatorCache().getRecipeCache().getIngredient(recipeSlot);
            if ((ingredient != null && ingredient.getItems().isEmpty() && !ingredient.getTags().isEmpty()) || event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT) && event.getView().getTopInventory().equals(clickEvent.getClickedInventory())) {
                return;
            }
            if (ingredient == null) {
                ingredient = new Ingredient();
            }
            ingredient.put(0, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
            ingredient.buildChoices();
            cache.getRecipeCreatorCache().getRecipeCache().setIngredient(recipeSlot, ingredient);
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var ingredient = cache.getRecipeCreatorCache().getRecipeCache().getIngredient(recipeSlot);
            return ingredient != null ? ingredient.getItemStack() : new ItemStack(Material.AIR);
        }));
    }

    static String getKey(int recipeSlot) {
        return "recipe.ingredient_" + recipeSlot;
    }

}
