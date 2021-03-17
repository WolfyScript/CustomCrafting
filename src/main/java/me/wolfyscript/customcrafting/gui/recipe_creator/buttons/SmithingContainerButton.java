package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SmithingContainerButton extends ItemInputButton<CCCache> {

    public SmithingContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("container_" + inputSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                cache.getVariantsData().setSlot(inputSlot);
                cache.getVariantsData().setVariants(inputSlot == 2 ? smithingRecipe.getResults() : inputSlot == 0 ? smithingRecipe.getBase() : smithingRecipe.getAddition());
                guiHandler.openWindow("variants");
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!event.getView().getBottomInventory().equals(((InventoryClickEvent) event).getClickedInventory())) {
                    return;
                }
            }
            CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
            List<CustomItem> items = inputSlot == 2 ? smithingRecipe.getResults() : inputSlot == 0 ? smithingRecipe.getBase() : smithingRecipe.getAddition();
            CustomItem input = !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR);
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
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomSmithingRecipe smithingRecipe = guiHandler.getCustomCache().getSmithingRecipe();
            List<CustomItem> items = inputSlot == 2 ? smithingRecipe.getResults() : inputSlot == 0 ? smithingRecipe.getBase() : smithingRecipe.getAddition();
            return InventoryUtils.isCustomItemsListEmpty(items) ? new ItemStack(Material.AIR) : items.get(0).create();
        }));
    }
}
