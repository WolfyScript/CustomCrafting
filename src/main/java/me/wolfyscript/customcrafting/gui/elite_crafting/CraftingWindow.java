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

import java.util.List;
import java.util.Map;
import java.util.Set;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.InteractionUtils;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

abstract class CraftingWindow extends CCWindow {

    protected static final String RESULT = "result_slot";
    static final List<Integer> RESULT_SLOTS = List.of(16, 25, 43);
    protected final int gridSize;
    protected static final Set<Integer> CRAFTING_SLOTS_2 = Set.of(
            3, 4,
            12, 13
    );
    protected static final Set<Integer> CRAFTING_SLOTS_3 = Set.of(
            2, 3, 4,
            11, 12, 13,
            20, 21, 22
    );
    protected static final Set<Integer> CRAFTING_SLOTS_4 = Set.of(
            1, 2, 3, 4,
            10, 11, 12, 13,
            19, 20, 21, 22,
            28, 29, 30, 31
    );
    protected static final Set<Integer> CRAFTING_SLOTS_5 = Set.of(
            1, 2, 3, 4, 5,
            10, 11, 12, 13, 14,
            19, 20, 21, 22, 23,
            28, 29, 30, 31, 32,
            37, 38, 39, 40, 41
    );
    protected static final Set<Integer> CRAFTING_SLOTS_6 = Set.of(
            0, 1, 2, 3, 4, 5,
            9, 10, 11, 12, 13, 14,
            18, 19, 20, 21, 22, 23,
            27, 28, 29, 30, 31, 32,
            36, 37, 38, 39, 40, 41,
            45, 46, 47, 48, 49, 50
    );
    protected static final Map<Byte, Set<Integer>> CRAFTING_SLOTS_MAP = Map.of(
            (byte) 2, CRAFTING_SLOTS_2,
            (byte) 3, CRAFTING_SLOTS_3,
            (byte) 4, CRAFTING_SLOTS_4,
            (byte) 5, CRAFTING_SLOTS_5,
            (byte) 6, CRAFTING_SLOTS_6
    );

    protected CraftingWindow(GuiCluster<CCCache> cluster, String namespace, int size, CustomCrafting customCrafting, int gridSize) {
        super(cluster, namespace, size, customCrafting);
        setForceSyncUpdate(true);
        this.gridSize = gridSize;
    }

    @Override
    public void onInit() {
        for (int i = 0; i < gridSize * gridSize; i++) {
            final int recipeSlot = i;
            getButtonBuilder().itemInput("crafting.slot_" + recipeSlot).state(state -> state.icon(Material.AIR)
                    .action((cache, guiHandler, player, inventory, slot, event) -> {
                        if (cache.getEliteWorkbench() == null || event instanceof InventoryClickEvent clickEvent && CraftingWindow.RESULT_SLOTS.contains(clickEvent.getSlot())) {
                            return true;
                        }
                        CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
                        if (cacheEliteCraftingTable.getContents() != null) {
                            return InteractionUtils.applyItemFromInteractionEvent(slot, event, CRAFTING_SLOTS_MAP.get(cacheEliteCraftingTable.getCurrentGridSize()), itemStack -> cacheEliteCraftingTable.getContents()[recipeSlot] = itemStack);
                        }
                        return true;
                    }).postAction((cache, guiHandler, player, inventory, itemStack, slot, inventoryInteractEvent) -> {
                        CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
                        if (cacheEliteCraftingTable.getContents() != null) {
                            Block targetBlock = player.getTargetBlock(null, 8);
                            customCrafting.getCraftManager().checkCraftingMatrix(
                                            cacheEliteCraftingTable.getContents(),
                                            Conditions.Data.of(player).setBlock(targetBlock).setInventoryView(player.getOpenInventory()).setEliteCraftingTableSettings(cacheEliteCraftingTable.getSettings()),
                                            RecipeType.Container.ELITE_CRAFTING,
                                            cacheEliteCraftingTable.isAdvancedCraftingRecipes() ? RecipeType.Container.CRAFTING : null
                                    )
                                    .map(data -> data.getResult().getItem(data, player, targetBlock))
                                    .ifPresent(cacheEliteCraftingTable::setResult);
                        } else {
                            cacheEliteCraftingTable.setResult(new ItemStack(Material.AIR));
                        }
                    }).render((cache, guiHandler, player, guiInventory, itemStack, i1) -> {
                        CacheEliteCraftingTable cacheEliteCraftingTable = cache.getEliteWorkbench();
                        if (cacheEliteCraftingTable.getContents() != null) {
                            ItemStack slotItem = cacheEliteCraftingTable.getContents()[recipeSlot];
                            return CallbackButtonRender.UpdateResult.of(slotItem == null ? new ItemStack(Material.AIR) : slotItem);
                        }
                        return CallbackButtonRender.UpdateResult.of(new ItemStack(Material.AIR));
                    })).register();
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
            cacheEliteCraftingTable.setCustomItem(null);
            cacheEliteCraftingTable.setSettings(null);
            cacheEliteCraftingTable.setCustomItemAndData(null, null);
            cacheEliteCraftingTable.setResult(new ItemStack(Material.AIR));
            cacheEliteCraftingTable.setContents(null);
            cacheEliteCraftingTable.setCurrentGridSize((byte) 0);
        }, 1);
        return false;
    }

}
