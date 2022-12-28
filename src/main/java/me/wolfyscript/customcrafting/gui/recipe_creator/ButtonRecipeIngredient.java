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

import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.common.gui.GUIClickInteractionDetails;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class ButtonRecipeIngredient {

    static String getKey(int recipeSlot) {
        return "recipe.ingredient_" + recipeSlot;
    }

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, int recipeSlot) {
        buttonBuilder.itemInput("recipe.ingredient_" + recipeSlot).state(state -> state.icon(Material.AIR).action((holder, cache, btn, slot, details) -> {
            var ingredient = cache.getRecipeCreatorCache().getRecipeCache().getIngredient(recipeSlot);
            if (details instanceof GUIClickInteractionDetails clickDetails && clickDetails.isRightClick() && clickDetails.isShiftClick()) {
                if (clickDetails.getSlot() == slot) {
                    //Since shift clicking now updates all the available ItemInputButtons we only use the first button that was clicked.
                    cache.getRecipeCreatorCache().getIngredientCache().setSlot(recipeSlot);
                    cache.getRecipeCreatorCache().getIngredientCache().setIngredient(ingredient != null ? ingredient : new Ingredient());
                    holder.getGuiHandler().openWindow(MenuIngredient.KEY);
                }
                return ButtonInteractionResult.cancel(true);
            }
            return ButtonInteractionResult.cancel(ingredient != null && ingredient.getItems().isEmpty() && !ingredient.getTags().isEmpty());
        }).postAction((holder, cache, btn, slot, itemStack, details) -> {
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
        }).render((holder, cache, btn, slot, itemStack) -> {
            var ingredient = cache.getRecipeCreatorCache().getRecipeCache().getIngredient(recipeSlot);
            return CallbackButtonRender.Result.of(ingredient != null ? ingredient.getItemStack() : new ItemStack(Material.AIR));
        })).register();
    }

}
