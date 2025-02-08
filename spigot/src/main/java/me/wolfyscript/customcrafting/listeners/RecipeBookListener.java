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

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeView;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

import java.util.Objects;

public class RecipeBookListener implements Listener {

    private CustomCrafting customCrafting;

    public RecipeBookListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        if (customCrafting.isPaper()) {
            Bukkit.getPluginManager().registerEvents(new PaperListener(), customCrafting);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onClickBottomInv(InventoryClickEvent event) {
        if (event.getInventory() instanceof GUIInventory<?> inventory && Objects.equals(event.getClickedInventory(), event.getView().getBottomInventory())) {
            String namespace = inventory.getWindow().getNamespacedKey().getNamespace();
            if (Objects.equals(namespace, ClusterRecipeBook.KEY) || Objects.equals(namespace, ClusterRecipeView.KEY)) {
                event.setCancelled(true);
            }
        }
    }

    public class PaperListener implements Listener {

        @EventHandler
        public void onRecipeBookClick(PlayerRecipeBookClickEvent event) {
            if (!ICustomVanillaRecipe.isDisplayRecipe(event.getRecipe())) return;
            if (event.getPlayer().getOpenInventory().getTopInventory() instanceof CraftingInventory craftingInventory) {
                Bukkit.getScheduler().runTask(customCrafting, () -> Bukkit.getPluginManager().callEvent(new PrepareItemCraftEvent(craftingInventory, event.getPlayer().getOpenInventory(), false)));
            }
        }

    }

}
