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

package me.wolfyscript.customcrafting.gui.cauldron;

import java.util.Map;
import java.util.Optional;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheCauldronWorkstation;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CauldronResultMenu extends CCWindow {

    protected static final String RESULT = "result_slot";

    protected CauldronResultMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, CauldronWorkstationCluster.CAULDRON_RESULT.getKey(), 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        for (int resultSlot = 0; resultSlot < 4; resultSlot++) {
            int finalResultSlot = resultSlot;
            getButtonBuilder().itemInput("result_" + resultSlot).state(state -> state.icon(Material.AIR)
                    .action((cache, guiHandler, player, inventory, slot, event) -> false)
                    .postAction((cache, guiHandler, player, inventory, itemStack, i, event) -> {
                        cache.getCauldronWorkstation().getBlockData().ifPresent(cauldronBlockData -> cauldronBlockData.getResult()[finalResultSlot] = itemStack);
                    })
                    .render((cache, guiHandler, player, inventory, itemStack, slot) -> {
                        ItemStack result = cache.getCauldronWorkstation().getBlockData().map(cauldronBlockData -> cauldronBlockData.getResult()[finalResultSlot]).orElse(ItemUtils.AIR);
                        return CallbackButtonRender.UpdateResult.of(result);
                    })).register();
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        //Prevent super class from rendering
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        if (customCrafting.getConfigHandler().getConfig().isGUIDrawBackground()) {
            for (int i = 0; i < getSize(); i++) {
                event.setButton(i, ClusterMain.GLASS_GRAY);
            }
        }

        CCCache cache = event.getGuiHandler().getCustomCache();
        CacheCauldronWorkstation cacheCauldronWorkstation = cache.getCauldronWorkstation();
        Optional<CauldronBlockData> optionalCauldronBlockData = cacheCauldronWorkstation.getBlockData();

        if (optionalCauldronBlockData.isPresent()) {
            CauldronBlockData blockData = optionalCauldronBlockData.get();
            if (!blockData.isResultEmpty()) {
                event.setButton(21, "result_0");
                event.setButton(22, "result_1");
                event.setButton(30, "result_2");
                event.setButton(31, "result_3");
                return;
            }
            Bukkit.getScheduler().runTask(customCrafting, () -> event.getGuiHandler().reloadWindow(CauldronWorkstationCluster.CAULDRON_MAIN));
        }

    }

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        Player player = guiHandler.getPlayer();
        World world = player.getWorld();
        CCCache cache = guiHandler.getCustomCache();

        //Reset cache
        CacheCauldronWorkstation cacheCauldronWorkstation = cache.getCauldronWorkstation();
        cacheCauldronWorkstation.setPreCookEvent(null);
        cacheCauldronWorkstation.setBlockData(null);
        cacheCauldronWorkstation.setBlock(null);
        for (ItemStack itemStack : cacheCauldronWorkstation.getInput()) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                Map<Integer, ItemStack> items = player.getInventory().addItem(itemStack);
                items.values().forEach(itemStack1 -> world.dropItemNaturally(player.getLocation(), itemStack1));
            }
        }
        cacheCauldronWorkstation.resetInput();
        return false;
    }

}
