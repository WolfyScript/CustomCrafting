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

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.common.gui.ClickType;
import com.wolfyscript.utilities.common.gui.GUIClickInteractionDetails;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuIngredient extends CCWindow {

    static final String KEY = "ingredient";
    private static final String REPLACE_WITH_REMAINS = "replace_with_remains";
    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getRecipeCreatorCache().getIngredientCache().getIngredient().put(items.getVariantSlot(), CustomItem.getReferenceByItemStack(customItem.create()));

    public MenuIngredient(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, KEY, 54, customCrafting);
        setForceSyncUpdate(true);
    }

    private void registerButtonContainerItemIngredient(int ingredSlot) {
       getButtonBuilder().itemInput("item_container_" + ingredSlot).state(state -> state.icon(Material.AIR).action((holder, cache, btn, slot, details) -> {
           if (details instanceof GUIClickInteractionDetails clickDetails && clickDetails.isShiftClick() && clickDetails.isRightClick()) {
               final var inventory = holder.getInventory();
               if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                   cache.getItems().setVariant(ingredSlot, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
                   cache.setApplyItem(APPLY_ITEM);
                   holder.getGuiHandler().openWindow(ClusterRecipeCreator.ITEM_EDITOR);
               }
               return ButtonInteractionResult.cancel(true);
           }
           return ButtonInteractionResult.cancel(false);
       }).postAction((holder, cache, btn, slot, itemStack, details) -> {
           if (details instanceof GUIClickInteractionDetails clickEvent && clickEvent.getClickType() == ClickType.SHIFT_SECONDARY) {
               return;
           }
           cache.getRecipeCreatorCache().getIngredientCache().getIngredient().put(ingredSlot, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
       }).render((holder, cache, btn, slot, itemStack) -> {
           var data = cache.getRecipeCreatorCache().getIngredientCache().getIngredient();
           return CallbackButtonRender.Result.of(data != null ? data.getItemStack(ingredSlot) : ItemUtils.AIR);
       })).register();
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 36; i++) {
            registerButtonContainerItemIngredient(i);
        }
        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((holder, cache, btn, slot, details) -> {
            var creatorCache = cache.getRecipeCreatorCache();
            creatorCache.getRecipeCache().setIngredient(creatorCache.getIngredientCache().getSlot(), creatorCache.getIngredientCache().getIngredient());
            holder.getGuiHandler().openPreviousWindow();
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().action("tags").state(s -> s.key(ClusterRecipeCreator.TAGS).icon(Material.NAME_TAG).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getTagSettingsCache().setRecipeItemStack(cache.getRecipeCreatorCache().getIngredientCache().getIngredient());
            holder.getGuiHandler().openWindow("tag_settings");
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().toggle(REPLACE_WITH_REMAINS).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getIngredientCache().getIngredient().isReplaceWithRemains()).enabledState(state -> state.subKey("enabled").icon(Material.BUCKET).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getIngredientCache().getIngredient().setReplaceWithRemains(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(state -> state.subKey("disabled").icon(Material.BUCKET).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getIngredientCache().getIngredient().setReplaceWithRemains(true);
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        update.setButton(8, ClusterMain.GUI_HELP);
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "item_container_" + i);
        }
        update.setButton(48, "tags");
        update.setButton(50, REPLACE_WITH_REMAINS);
    }
}
