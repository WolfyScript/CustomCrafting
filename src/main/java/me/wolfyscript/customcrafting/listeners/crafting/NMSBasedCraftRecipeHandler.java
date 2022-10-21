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

package me.wolfyscript.customcrafting.listeners.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Keyed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class NMSBasedCraftRecipeHandler implements Listener {

    private final CustomCrafting customCrafting;
    private final CraftManager craftManager;

    public NMSBasedCraftRecipeHandler(CustomCrafting customCrafting, CraftManager craftManager) {
        this.customCrafting = customCrafting;
        this.craftManager = craftManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof CraftingInventory inventory)) return;
        // Update MatrixData for the clicked inventory
        craftManager.clearCurrentMatrixData(event.getView());
        if (event.getSlot() == 0 && inventory instanceof Keyed keyed && keyed.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            ItemStack resultItem = inventory.getResult();
            ItemStack cursor = event.getCursor();
            if (ItemUtils.isAirOrNull(resultItem) || (!ItemUtils.isAirOrNull(cursor) && !cursor.isSimilar(resultItem) && !event.isShiftClick())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemCraft(CraftItemEvent event) {
        // Update MatrixData for the clicked inventory
        craftManager.clearCurrentMatrixData(event.getView());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        // Update MatrixData for the clicked inventory
        craftManager.clearCurrentMatrixData(e.getView());
    }

}
