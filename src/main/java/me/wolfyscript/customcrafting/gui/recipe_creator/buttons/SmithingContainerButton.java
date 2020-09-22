package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SmithingContainerButton extends ItemInputButton {

    public SmithingContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("container_" + inputSlot, new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();

            List<CustomItem> items = inputSlot == 2 ? smithingRecipe.getResults() : inputSlot == 0 ? smithingRecipe.getBase() : smithingRecipe.getAddition();

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
                        smithingRecipe.setResult(items);
                    }else if(inputSlot == 0){
                        smithingRecipe.setBase(items);
                    }else{
                        smithingRecipe.setAddition(items);
                    }
                });
            }
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            CustomSmithingRecipe smithingRecipe = ((TestCache) guiHandler.getCustomCache()).getSmithingRecipe();
            List<CustomItem> items = inputSlot == 2 ? smithingRecipe.getResults() : inputSlot == 0 ? smithingRecipe.getBase() : smithingRecipe.getAddition();
            return InventoryUtils.isCustomItemsListEmpty(items) ? new ItemStack(Material.AIR) : items.get(0).create();
        }));
    }
}
