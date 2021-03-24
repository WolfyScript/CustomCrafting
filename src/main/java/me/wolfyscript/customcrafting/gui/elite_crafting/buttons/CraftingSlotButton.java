package me.wolfyscript.customcrafting.gui.elite_crafting.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CraftingSlotButton extends ItemInputButton<CCCache> {

    public CraftingSlotButton(int recipeSlot, CustomCrafting customCrafting) {
        super("crafting.slot_" + recipeSlot, new ButtonState<>("", Material.AIR,
                (cache, guiHandler, player, inventory, slot, event) -> false,
                (cache, guiHandler, player, inventory, itemStack, slot, b) -> {
                    EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
                    int gridSize = eliteWorkbench.getCurrentGridSize();
                    int startSlot = (gridSize == 3 ? 2 : gridSize == 4 || gridSize == 5 ? 1 : 0);
                    int itemSlot;
                    for (int i = 0; i < gridSize * gridSize; i++) {
                        itemSlot = startSlot + i + (i / gridSize) * (9 - gridSize);
                        eliteWorkbench.getContents()[i] = inventory.getItem(itemSlot);
                    }
                    ItemStack result = customCrafting.getCraftManager().preCheckRecipe(eliteWorkbench.getContents(), player, inventory, true, eliteWorkbench.getEliteWorkbenchData().isAdvancedRecipes());
                    eliteWorkbench.setResult(result);
                }, null,
                (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                    EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
                    if (eliteWorkbench.getContents() != null) {
                        ItemStack slotItem = eliteWorkbench.getContents()[recipeSlot];
                        return slotItem == null ? new ItemStack(Material.AIR) : slotItem;
                    }
                    return new ItemStack(Material.AIR);
                }));
    }
}
