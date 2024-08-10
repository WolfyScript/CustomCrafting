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
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CrafterListener implements Listener {

    private final CustomCrafting customCrafting;
    private final CraftManager craftManager;
    private final Map<Vector, NamespacedKey> previouslyUsedRecipe = new HashMap<>();

    public CrafterListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.craftManager = customCrafting.getCraftManager();
    }

    private void applyRecipeToCrafter(CraftingData craftingData, Block block, Crafter crafter, CrafterCraftEvent event) {
        ItemStack result = craftingData.getResult().item(craftingData, null, block);
        ItemStack[] updatedMatrix = craftingData.getRecipe().shrinkMatrix(null, crafter.getInventory(), 1, craftingData, 3);
        event.setResult(result);
        // clear cache right after
        craftingData.getResult().removeCachedItem(block);
        // Update the inventory of the crafter a tik later to override vanilla crafting behaviour
        // If that turns out to be a problem, we could cancel the event and do everything manually, but that is bound to cause issues
        Bukkit.getScheduler().runTask(customCrafting, () -> {
            var inventory = crafter.getSnapshotInventory();
            for (int i = 0; i < updatedMatrix.length; i++) {
                if (crafter.isSlotDisabled(i)) { // Do not set disabled slots. even when setting them to null they are re-enabled
                    continue;
                }
                inventory.setItem(i, updatedMatrix[i]);
            }
            crafter.update(true, true);
        });
    }

    @EventHandler
    public void onCrafterCrafting(CrafterCraftEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Crafter crafter)) {
            return; // should never happen
        }
        Vector blockVec = block.getLocation().toVector();
        CraftManager.MatrixData matrixData = CraftManager.MatrixData.of(crafter.getInventory().getContents());
        Conditions.Data conditionsData = Conditions.Data.of(block);

        // First check the previously used recipe, which is likely to be still in use
        NamespacedKey previousRecipe = previouslyUsedRecipe.get(blockVec);
        if (previousRecipe != null) {
            Optional<CraftingData> craftingData = craftManager.tryRecipe(customCrafting.getRegistries().getRecipes().getAdvancedCrafting(previousRecipe), matrixData, conditionsData);
            if (craftingData.isPresent()) {
                applyRecipeToCrafter(craftingData.get(), block, crafter, event);
                return;
            }
        }
        // No Previous recipe or it failed the check, look for new recipe
        craftManager.checkCraftingMatrix(matrixData, conditionsData, RecipeType.Container.CRAFTING)
                .ifPresentOrElse(craftingData -> {
                    previouslyUsedRecipe.put(blockVec, craftingData.getRecipe().getNamespacedKey()); // Next time check this recipe first
                    applyRecipeToCrafter(craftingData, block, crafter, event);
                }, () -> {
                    var recipe = event.getRecipe();
                    // We need placeholder recipes that simply use material choices, because otherwise we can get duplication issues and buggy behaviour like flickering.
                    // Here we need to disable those placeholder recipes and check for a vanilla recipe the placeholder may override.
                    if (ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(recipe.getKey())) {
                        // Can't determine the vanilla recipe! We may need NMS for that in the future. For now simply override vanilla recipes.
                        event.setCancelled(true);
                        return;
                    }

                    var namespacedKey = NamespacedKey.fromBukkit(recipe.getKey());
                    //Check for custom recipe that overrides the vanilla recipe
                    if (customCrafting.getDisableRecipesHandler().getRecipes().contains(namespacedKey) || customCrafting.getRegistries().getRecipes().getAdvancedCrafting(namespacedKey) != null) {
                        //Recipe is disabled or it is a custom recipe!
                        event.setCancelled(true);
                        return;
                    }
                    //Check for items that are not allowed in vanilla recipes.
                    //If one is found, then cancel the recipe.
                    if (Stream.of(matrixData.getMatrix()).map(CustomItem::getByItemStack).anyMatch(i -> i != null && i.isBlockVanillaRecipes())) {
                        event.setCancelled(true);
                    }
                    //At this point the vanilla recipe is valid and can be crafted
                });
    }

}
