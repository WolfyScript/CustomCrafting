package me.wolfyscript.customcrafting.gui.crafting.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.PlayerCache;
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
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                EliteWorkbench eliteWorkbenchData = cache.getEliteWorkbench();
                if (eliteWorkbenchData.getContents() != null) {
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        eliteWorkbenchData.getContents()[recipeSlot] = inventory.getItem(slot);
                        EliteWorkbenchData eliteWorkbench = eliteWorkbenchData.getEliteWorkbenchData();
                        ItemStack result = RecipeUtils.preCheckRecipe(eliteWorkbenchData.getContents(), player, false, inventory, true, eliteWorkbench != null && eliteWorkbench.isAdvancedRecipes());
                        if (result != null) {
                            eliteWorkbenchData.setResult(result);
                        } else {
                            eliteWorkbenchData.setResult(new ItemStack(Material.AIR));
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
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
