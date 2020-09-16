package me.wolfyscript.customcrafting.gui.elite_crafting.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class ResultSlotButton extends ItemInputButton {

    public ResultSlotButton(CustomCrafting customCrafting) {
        super("result_slot", new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getClickedInventory().equals(event.getView().getBottomInventory())) {
                ItemStack itemStack = event.getCurrentItem().clone();
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    for (int i = 0; i < eliteWorkbench.getCurrentGridSize() * eliteWorkbench.getCurrentGridSize(); i++) {
                        ItemStack item = eliteWorkbench.getContents()[i];
                        if (item == null) {
                            eliteWorkbench.getContents()[i] = itemStack;
                            break;
                        } else if (item.isSimilar(itemStack) || itemStack.isSimilar(item)) {
                            if (item.getAmount() + itemStack.getAmount() <= itemStack.getMaxStackSize()) {
                                eliteWorkbench.getContents()[i].setAmount(item.getAmount() + itemStack.getAmount());
                                break;
                            }
                        }
                    }
                });
                return false;
            } else {
                if (eliteWorkbench.getResult() != null) {
                    if (ItemUtils.isAirOrNull(event.getCursor()) || event.getCursor().isSimilar(eliteWorkbench.getResult())) {
                        customCrafting.getRecipeUtils().consumeRecipe(eliteWorkbench.getResult(), eliteWorkbench.getContents(), event);
                        customCrafting.getRecipeUtils().getPreCraftedRecipes().put(event.getWhoClicked().getUniqueId(), null);
                    }
                }
            }
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                EliteWorkbenchData eliteWorkbenchData = eliteWorkbench.getEliteWorkbenchData();
                ItemStack result = customCrafting.getRecipeUtils().preCheckRecipe(eliteWorkbench.getContents(), player, false, inventory, true, eliteWorkbenchData.isAdvancedRecipes());
                eliteWorkbench.setResult(result);
            });
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            return eliteWorkbench.getResult() != null ? eliteWorkbench.getResult() : new ItemStack(Material.AIR);
        }));
    }
}
