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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

class ButtonContainerItemIngredient extends ItemInputButton<CCCache> {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> {
        var updatedRef = WolfyUtilCore.getInstance().getRegistries().getStackIdentifierParsers().parseFrom(customItem.create()).orElse(null);
        // Update edited ref, so the GUI gets updated!
        items.editRecipeStackVariant(items.getVariantSlot(), updatedRef);
        cache.getRecipeCreatorCache().getIngredientCache().getIngredient().put(items.getVariantSlot(), updatedRef);
    };

    ButtonContainerItemIngredient(int ingredSlot) {
        super("item_container_" + ingredSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, invSlot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (clickEvent.getSlot() != invSlot) return true;
                if (!ItemUtils.isAirOrNull(inventory.getItem(invSlot))) {
                    StackReference variant = cache.getRecipeCreatorCache().getIngredientCache().getIngredient().items().get(ingredSlot);
                    cache.getItems().editRecipeStackVariant(ingredSlot, variant);
                    cache.setApplyItem(APPLY_ITEM);
                    Bukkit.getScheduler().runTask(CustomCrafting.inst(), () -> guiHandler.openWindow(ClusterRecipeCreator.ITEM_EDITOR));
                }
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                return;
            }
            StackReference updatedStack = itemStack == null ? StackReference.of(new ItemStack(Material.AIR)) : guiHandler.getWolfyUtils().getRegistries().getStackIdentifierParsers().parseFrom(itemStack).orElse(null);
            cache.getRecipeCreatorCache().getIngredientCache().getIngredient().put(ingredSlot, updatedStack);
        }, null, (CallbackButtonRender<CCCache>) (cache, guiHandler, player, guiInventory, itemStack, i) -> {
            var data = cache.getRecipeCreatorCache().getIngredientCache().getIngredient();
            return CallbackButtonRender.UpdateResult.of(data != null ? data.getItemStack(ingredSlot) : ItemUtils.AIR);
        }));
    }

}
