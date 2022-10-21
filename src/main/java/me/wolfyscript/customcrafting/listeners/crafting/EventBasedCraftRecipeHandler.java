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

import java.util.stream.Stream;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

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
                    //Clear Matrix to prevent duplication and buggy behaviour.
                    //This must not update the inventory yet, as that would call the PrepareItemCraftEvent, invalidating the recipe and preventing consumption of the recipe!
                    //But clearing it later can cause other issues too!
                    //So lets just set the items to AIR and amount to 0...
                    inventory.clear();
                    final int count = craftManager.collectResult(event, craftingData, player);
                    //...do all the calculations & item replacements...
                    //...and finally update the inventory.
                    player.updateInventory();
                    //Reset Matrix with the re-calculated items. (1 tick later, to not cause duplication!)
                    //This will result in a short flicker of the items in the inventory... still better than duplications, so the flickering won't be fixed!
                    Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                        inventory.setMatrix(craftingData.getRecipe().shrinkMatrix(player, inventory, count, craftingData, inventory.getMatrix().length == 9 ? 3 : 2));
                    }, 1);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        var player = (Player) e.getView().getPlayer();
        try {
            ItemStack[] matrix = e.getInventory().getMatrix();
            ItemStack result = craftManager.preCheckRecipe(matrix, player, e.getInventory(), false, true);
            if (!ItemUtils.isAirOrNull(result)) {
                e.getInventory().setResult(result);
                Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
                return;
            }
            //No valid custom recipes found
            if (!(e.getRecipe() instanceof Keyed)) return;
            //Vanilla Recipe is available.
            //Check for custom recipe that overrides the vanilla recipe
            var namespacedKey = NamespacedKey.fromBukkit(((Keyed) e.getRecipe()).getKey());
            if (customCrafting.getDisableRecipesHandler().getRecipes().contains(namespacedKey) || customCrafting.getRegistries().getRecipes().getAdvancedCrafting(namespacedKey) != null) {
                //Recipe is disabled or it is a custom recipe!
                e.getInventory().setResult(ItemUtils.AIR);
                Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
                return;
            }
            //Check for items that are not allowed in vanilla recipes.
            //If one is found, then cancel the recipe.
            if (Stream.of(matrix).map(CustomItem::getByItemStack).anyMatch(i -> i != null && i.isBlockVanillaRecipes())) {
                e.getInventory().setResult(ItemUtils.AIR);
            }
            //At this point the vanilla recipe is valid and can be crafted
            Bukkit.getScheduler().runTask(customCrafting, player::updateInventory);
        } catch (Exception ex) {
            customCrafting.getLogger().severe("-------- [Error occurred while crafting Recipe!] --------");
            ex.printStackTrace();
            customCrafting.getLogger().severe("-------- [Error occurred while crafting Recipe!] --------");
            craftManager.remove(player.getUniqueId());
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

}
