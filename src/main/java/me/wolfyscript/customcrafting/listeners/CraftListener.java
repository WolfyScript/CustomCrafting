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
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Stream;

public class CraftListener implements Listener {

    private final CustomCrafting customCrafting;
    private final CraftManager craftManager;

    public CraftListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.craftManager = customCrafting.getCraftManager();
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE) && !customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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
                    for (int i = 0; i < 10; i++) {
                        ItemStack item = inventory.getItem(i);
                        if (item != null) {
                            item.setAmount(0);
                            item.setType(Material.AIR);
                        }
                    }
                    //...do all the calculations & item replacements...
                    craftManager.consumeRecipe(event);
                    //...and finally update the inventory.
                    player.updateInventory();
                    //Reset Matrix with the re-calculated items. (1 tick later, to not cause duplication!)
                    //This will result in a short flicker of the items in the inventory... still better than duplications, so the flickering won't be fixed!
                    Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                        craftingData.getIndexedBySlot().forEach((integer, ingredientData) -> inventory.setItem(integer + 1, ingredientData.itemStack()));
                        player.updateInventory();
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

    @EventHandler
    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {
        org.bukkit.NamespacedKey key = event.getRecipe();
        if (key.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            CustomRecipe<?> recipe = customCrafting.getRegistries().getRecipes().get(NamespacedKey.fromBukkit(key));
            if (recipe instanceof ICustomVanillaRecipe<?> vanillaRecipe && vanillaRecipe.isVisibleVanillaBook()) {
                event.setCancelled(recipe.isHidden() || recipe.isDisabled());
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Automatically discovers available custom recipes for players.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        List<org.bukkit.NamespacedKey> discoveredCustomRecipes = player.getDiscoveredRecipes().stream().filter(namespacedKey -> namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)).toList();
        customCrafting.getRegistries().getRecipes().getAvailable(player).stream()
                .filter(recipe -> recipe instanceof ICustomVanillaRecipe<?>)
                .map(recipe -> new org.bukkit.NamespacedKey(recipe.getNamespacedKey().getNamespace(), recipe.getNamespacedKey().getKey()))
                .filter(namespacedKey -> !discoveredCustomRecipes.contains(namespacedKey))
                .forEach(player::discoverRecipe);
    }
}
