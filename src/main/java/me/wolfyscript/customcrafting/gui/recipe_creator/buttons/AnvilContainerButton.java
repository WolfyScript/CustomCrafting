package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AnvilContainerButton extends ItemInputButton {

    public AnvilContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("container_" + inputSlot, new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
            List<CustomItem> items = inputSlot == 2 ? anvilRecipe.getResults() : anvilRecipe.getInput(inputSlot);
            if (event.isRightClick() && event.isShiftClick()) {
                cache.getVariantsData().setSlot(inputSlot);
                cache.getVariantsData().setVariants(items);
                guiHandler.changeToInv("variants");
                return true;
            } else {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    CustomItem input = !ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                    if (items.size() > 0) {
                        items.set(0, input);
                    } else {
                        items.add(input);
                    }
                    if(inputSlot == 2){
                        anvilRecipe.setResult(items);
                    }else{
                        anvilRecipe.setInput(inputSlot, items);
                    }
                });
            }
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            CustomAnvilRecipe anvilRecipe = ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe();
            List<CustomItem> items = inputSlot == 2 ? anvilRecipe.getResults() : anvilRecipe.getInput(inputSlot);
            return InventoryUtils.isCustomItemsListEmpty(items) ? new ItemStack(Material.AIR) : items.get(0).create();
        }));
    }
}
