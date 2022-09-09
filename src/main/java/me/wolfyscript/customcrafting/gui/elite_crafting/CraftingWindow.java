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

package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

abstract class CraftingWindow extends CCWindow {

    protected static final String RESULT = "result_slot";
    static final List<Integer> RESULT_SLOTS = List.of(16, 25, 43);
    protected final int gridSize;

    protected CraftingWindow(GuiCluster<CCCache> cluster, String namespace, int size, CustomCrafting customCrafting, int gridSize) {
        super(cluster, namespace, size, customCrafting);
        setForceSyncUpdate(true);
        this.gridSize = gridSize;
    }

    @Override
    public void onInit() {
        for (int i = 0; i < gridSize * gridSize; i++) {
            registerButton(new ButtonSlotCrafting(i, customCrafting));
        }
        registerButton(new ButtonSlotResult(customCrafting));
        registerButton(new DummyButton<>("texture_dark", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("texture_light", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        //Prevent super class from rendering
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        for (int i = 0; i < getSize(); i++) {
            event.setButton(i, ClusterMain.GLASS_BLACK);
        }
        CCCache cache = event.getGuiHandler().getCustomCache();
        CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
        if (cacheEliteCraftingTable.getContents() == null || cacheEliteCraftingTable.getCurrentGridSize() <= 0) {
            cacheEliteCraftingTable.setCurrentGridSize((byte) gridSize);
            cacheEliteCraftingTable.setContents(new ItemStack[gridSize * gridSize]);
        }
        int slot;
        for (int i = 0; i < gridSize * gridSize; i++) {
            slot = getGridX() + i + (i / gridSize) * (9 - gridSize);
            event.setButton(slot, "crafting.slot_" + i);
        }
    }

    public abstract int getGridX();

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        Player player = guiHandler.getPlayer();
        CCCache cache = guiHandler.getCustomCache();
        Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
            CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
            if (cacheEliteCraftingTable.getContents() != null) {
                for (ItemStack itemStack : cacheEliteCraftingTable.getContents()) {
                    if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                        player.getInventory().addItem(itemStack);
                    }
                }
            }
            cacheEliteCraftingTable.setCustomItemAndData(null, null);
            cacheEliteCraftingTable.setResult(new ItemStack(Material.AIR));
            cacheEliteCraftingTable.setContents(null);
            cacheEliteCraftingTable.setCurrentGridSize((byte) 0);
        }, 1);
        return false;
    }

}
