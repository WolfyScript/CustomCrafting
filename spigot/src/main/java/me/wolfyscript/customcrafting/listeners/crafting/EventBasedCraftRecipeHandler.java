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
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Stream;

public class EventBasedCraftRecipeHandler implements Listener {

    private final CustomCrafting customCrafting;
    private final CraftManager craftManager;

    public EventBasedCraftRecipeHandler(CustomCrafting customCrafting, CraftManager craftManager) {
        this.customCrafting = customCrafting;
        this.craftManager = craftManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof CraftingInventory inventory)) return;
        if (event.getSlot() == 0) {
            ItemStack resultItem = inventory.getResult();
            ItemStack cursor = event.getCursor();
            if (ItemUtils.isAirOrNull(resultItem) || (!ItemUtils.isAirOrNull(cursor) && !cursor.isSimilar(resultItem) && !event.isShiftClick())) {
                event.setCancelled(true);
                return;
            }
            craftManager.get(event.getWhoClicked().getUniqueId()).ifPresent(craftingData -> {
                event.setCancelled(true);
                var player = (Player) event.getWhoClicked();
                if (event.isShiftClick() || ItemUtils.isAirOrNull(cursor) || cursor.getAmount() + resultItem.getAmount() <= cursor.getMaxStackSize()) {
                    // At this point do not change the inventory! Because that would call the PrepareItemCraftEvent, invalidating the recipe and preventing consumption of the recipe!
                    final int count = craftManager.collectResult(event, craftingData, player);
                    ItemStack[] matrix = craftingData.getRecipe().shrinkMatrix(player, inventory, count, craftingData, inventory.getMatrix().length == 9 ? 3 : 2);
                    // Now all calculations are done, so we can update the inventory
                    inventory.setMatrix(matrix);
                    player.updateInventory();
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        var player = (Player) e.getView().getPlayer();
        try {
            CraftManager.MatrixData matrix = CraftManager.MatrixData.of(e.getInventory().getMatrix());
            Block block = e.getInventory().getLocation() == null ? player.getLocation().getBlock() : e.getInventory().getLocation().getBlock();
            craftManager.checkCraftingMatrix(matrix, Conditions.Data.of(player).setInventoryView(e.getView()).setBlock(block), RecipeType.Container.CRAFTING)
                    .map(craftingData -> craftingData.getResult().item(craftingData, player, block))
                    .ifPresentOrElse(result -> {
                        e.getInventory().setResult(result);
                        Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
                    }, () -> {
                        // No valid custom recipes found
                        if (!(e.getRecipe() instanceof Keyed)) return;

                        // We need placeholder recipes that simply use material choices, because otherwise we can get duplication issues and buggy behaviour like flickering.
                        // Here we need to disable those placeholder recipes and check for a vanilla recipe the placeholder may override.
                        if (ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(((Keyed) e.getRecipe()).getKey())) {
                            // TODO: Can't determine the vanilla recipe! We may need NMS for that in the future. For now simply override vanilla recipes.
                            e.getInventory().setResult(ItemUtils.AIR);
                            Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
                            return;
                        }

                        var namespacedKey = NamespacedKey.fromBukkit(((Keyed) e.getRecipe()).getKey());
                        //Check for custom recipe that overrides the vanilla recipe
                        if (customCrafting.getDisableRecipesHandler().getRecipes().contains(namespacedKey) || customCrafting.getRegistries().getRecipes().getAdvancedCrafting(namespacedKey) != null) {
                            //Recipe is disabled or it is a custom recipe!
                            e.getInventory().setResult(ItemUtils.AIR);
                            Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
                            return;
                        }
                        //Check for items that are not allowed in vanilla recipes.
                        //If one is found, then cancel the recipe.
                        if (Stream.of(matrix.getMatrix()).map(CustomItem::getByItemStack).anyMatch(i -> i != null && i.isBlockVanillaRecipes())) {
                            e.getInventory().setResult(ItemUtils.AIR);
                        }
                        //At this point the vanilla recipe is valid and can be crafted
                        Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
                    });
        } catch (Exception ex) {
            customCrafting.getLogger().severe("-------- [Error occurred while crafting Recipe!] --------");
            ex.printStackTrace();
            customCrafting.getLogger().severe("-------- [Error occurred while crafting Recipe!] --------");
            craftManager.remove(player.getUniqueId());
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

}
