package me.wolfyscript.customcrafting.gui.crafting.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.recipes.crafting.RecipeUtils;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CraftingSlotButton extends ItemInputButton {

    public CraftingSlotButton(int recipeSlot) {
        super("crafting.slot_" + recipeSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                TestCache cache = ((TestCache) guiHandler.getCustomCache());
                EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
                Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                    int gridSize = eliteWorkbench.getCurrentGridSize();
                    int startSlot = (gridSize == 3 ? 2 : gridSize == 4 || gridSize == 5 ? 1 : 0);
                    int itemSlot;
                    for (int i = 0; i < gridSize*gridSize; i++) {
                        itemSlot =  startSlot + i + (i / gridSize) * (9-gridSize);
                        eliteWorkbench.getContents()[i] = inventory.getItem(itemSlot);
                    }
                    ItemStack result = RecipeUtils.preCheckRecipe(eliteWorkbench.getContents(), player, false, inventory, true, eliteWorkbench != null && eliteWorkbench.getEliteWorkbenchData().isAdvancedRecipes());
                    eliteWorkbench.setResult(result);
                });
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                TestCache cache = ((TestCache) guiHandler.getCustomCache());
                EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
                if (eliteWorkbench.getContents() != null) {
                    ItemStack slotItem = eliteWorkbench.getContents()[recipeSlot];
                    itemStack = slotItem == null ? new ItemStack(Material.AIR) : slotItem;
                }
                return itemStack;
            }
        }));
    }
}
