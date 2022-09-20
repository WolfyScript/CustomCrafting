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

package me.wolfyscript.customcrafting.listeners;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheCauldronWorkstation;
import me.wolfyscript.customcrafting.utils.CauldronUtils;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.cauldron.CauldronWorkstationCluster;
import me.wolfyscript.utilities.api.Permissions;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CauldronListener implements Listener {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;

    public CauldronListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = customCrafting.getApi();
    }

    @EventHandler
    public void onInteractWithCauldron(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getPlayer().isSneaking()) return;
        Block clicked = event.getClickedBlock();
        if (clicked != null && CauldronUtils.isCauldron(clicked.getType()) && event.getPlayer().hasPermission("customcrafting.workstation.cauldron.interact")) {
            ItemStack usedItem = event.getItem();
            if (usedItem != null) {
                Material type = usedItem.getType();
                if (type.equals(Material.POTION) || type.equals(Material.GLASS_BOTTLE) || type.equals(Material.WATER_BUCKET) || type.equals(Material.BUCKET) || type.equals(Material.LAVA_BUCKET)) return;
            }
            WorldStorage worldStorage = ((WolfyCoreBukkit)api.getCore()).getPersistentStorage().getOrCreateWorldStorage(clicked.getWorld());
            BlockStorage blockStorage = worldStorage.getOrCreateAndSetBlockStorage(clicked.getLocation());
            if (blockStorage.getData(CauldronBlockData.ID, CauldronBlockData.class).isEmpty()) {
                var cauldronBlockData = new CauldronBlockData(blockStorage.getPos(), blockStorage.getChunkStorage());
                blockStorage.addOrSetData(cauldronBlockData);
                cauldronBlockData.onLoad();
                blockStorage.getChunkStorage().updateBlock(blockStorage.getPos());
            }
            blockStorage.getData(CauldronBlockData.ID, CauldronBlockData.class).ifPresent(cauldronBlockData -> {
                if (cauldronBlockData.getRecipe().isEmpty() && cauldronBlockData.getPassedTicks() <= 0) {
                    GuiHandler<CCCache> guiHandler = api.getInventoryAPI(CCCache.class).getGuiHandler(event.getPlayer());
                    CacheCauldronWorkstation cauldronWorkstation = guiHandler.getCustomCache().getCauldronWorkstation();
                    cauldronWorkstation.setBlockData(cauldronBlockData);
                    cauldronWorkstation.setBlock(clicked);
                    guiHandler.openCluster(CauldronWorkstationCluster.KEY);
                    event.setCancelled(true);
                }
            });
        }
    }

}
