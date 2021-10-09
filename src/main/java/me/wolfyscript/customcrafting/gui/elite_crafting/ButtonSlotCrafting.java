package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class ButtonSlotCrafting extends ItemInputButton<CCCache> {

    ButtonSlotCrafting(int recipeSlot, CustomCrafting customCrafting) {
        super("crafting.slot_" + recipeSlot, new ButtonState<>("", Material.AIR,
                (cache, guiHandler, player, inventory, slot, event) -> false,
                (cache, guiHandler, player, inventory, itemStack, slot, b) -> {
                    EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
                    eliteWorkbench.getContents()[recipeSlot] = inventory.getItem(slot);
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
                })
        );
    }
}
