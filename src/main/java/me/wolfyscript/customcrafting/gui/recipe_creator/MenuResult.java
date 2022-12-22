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
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.items.Result;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuResult extends CCWindow {

    private static final ApplyItem APPLY_ITEM = (items, cache, customItem) -> cache.getRecipeCreatorCache().getRecipeCache().getResult().put(items.getVariantSlot(), CustomItem.getReferenceByItemStack(customItem.create()));

    public MenuResult(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "result", 54, customCrafting);
        setForceSyncUpdate(true);
    }

    private void registerButtonContainerItemResult(int variantSlot) {
        getButtonBuilder().itemInput("variant_container_" + variantSlot).state(state -> state.icon(Material.AIR).action((cache, guiHandler, player, inv, button, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!ItemUtils.isAirOrNull(inv.getItem(slot))) {
                    cache.getItems().setVariant(variantSlot, CustomItem.getReferenceByItemStack(inv.getItem(slot)));
                    cache.setApplyItem(APPLY_ITEM);
                    guiHandler.openWindow(ClusterRecipeCreator.ITEM_EDITOR);
                }
                return true;
            }
            return false;
        }).postAction((cache, guiHandler, player, guiInventory, button, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                return;
            }
            cache.getRecipeCreatorCache().getRecipeCache().getResult().put(variantSlot, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
        }).render((cache, guiHandler, player, guiInventory, button, itemStack, i) -> {
            Result result = cache.getRecipeCreatorCache().getRecipeCache().getResult();
            return CallbackButtonRender.UpdateResult.of(result != null ? result.getItemStack(variantSlot) : ItemUtils.AIR);
        })).register();
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButtonContainerItemResult(i);
        }
        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((cache, guiHandler, player, inv, btn, i, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().getResult().buildChoices();
            guiHandler.openPreviousWindow();
            return true;
        })).register();
        getButtonBuilder().action("tags").state(s -> s.key(ClusterRecipeCreator.TAGS).icon(Material.NAME_TAG).action((cache, guiHandler, player, inv, btn, i, event) -> {
            cache.getRecipeCreatorCache().getTagSettingsCache().setRecipeItemStack(cache.getRecipeCreatorCache().getRecipeCache().getResult());
            guiHandler.openWindow("tag_settings");
            return true;
        })).register();
        getButtonBuilder().dummy("target").state(s -> s.icon(Material.ARROW)).register();
        getButtonBuilder().dummy("extensions").state(s -> s.icon(Material.COMMAND_BLOCK)).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        update.setButton(8, ClusterMain.GUI_HELP);
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "variant_container_" + i);
        }
        update.setButton(47, "target");
        update.setButton(49, "extensions");
        update.setButton(51, "tags");


    }
}
