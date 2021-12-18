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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.CookingRecipeData;
import me.wolfyscript.customcrafting.utils.cooking.FurnaceListener1_17Adapter;
import me.wolfyscript.customcrafting.utils.cooking.CookingManager;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FurnaceListener implements Listener {

    protected final CustomCrafting customCrafting;
    protected final WolfyUtilities api;
    protected final CookingManager manager;

    public FurnaceListener(CustomCrafting customCrafting, CookingManager manager) {
        this.manager = manager;
        this.customCrafting = customCrafting;
        this.api = customCrafting.getApi();
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_17)) {
            Bukkit.getPluginManager().registerEvents(new FurnaceListener1_17Adapter(customCrafting, manager), customCrafting);
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory() instanceof FurnaceInventory && event.getSlotType().equals(InventoryType.SlotType.FUEL)) {
            if (event.getCursor() == null) return;
            Optional<CustomItem> fuelItem = api.getRegistries().getCustomItems().values().stream().filter(customItem -> customItem.getFuelSettings().getBurnTime() > 0 && customItem.isSimilar(event.getCursor())).findFirst();
            if (fuelItem.isPresent()) {
                var location = event.getInventory().getLocation();
                if (fuelItem.get().getFuelSettings().getAllowedBlocks().contains(location != null ? location.getBlock().getType() : Material.FURNACE)) {
                    InventoryUtils.calculateClickedSlot(event);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        ItemStack input = event.getFuel();
        for (CustomItem customItem : api.getRegistries().getCustomItems().values()) {
            var fuelSettings = customItem.getFuelSettings();
            if (fuelSettings.getBurnTime() > 0 && customItem.isSimilar(input) && fuelSettings.getAllowedBlocks().contains(event.getBlock().getType())) {
                event.setCancelled(false);
                event.setBurning(true);
                event.setBurnTime(fuelSettings.getBurnTime());
                break;
            }
        }
    }

    /*
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSmeltCacheTestLOW(FurnaceSmeltEvent event) {
        customCrafting.getLogger().info("low: Is custom: " + CustomCrafting.inst().getCookingManager().hasCustomRecipe(event));
    }
     */

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        Pair<CookingRecipeData<?>, Boolean> data = manager.getCustomRecipeCache(event);
        if (data.getValue()) {
            if (data.getKey() == null) {
                event.setCancelled(true);
                return;
            }
            manager.getAdapter().applyResult(event);
        }
    }

}
