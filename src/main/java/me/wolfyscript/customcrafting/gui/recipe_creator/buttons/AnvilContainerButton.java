package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
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

public class AnvilContainerButton extends ItemInputButton<CCCache> {

    public AnvilContainerButton(int inputSlot, CustomCrafting customCrafting) {
        super("container_" + inputSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                cache.getVariantsData().setSlot(inputSlot);
                cache.getVariantsData().setVariants(inputSlot == 2 ? anvilRecipe.getResults() : anvilRecipe.getInput(inputSlot));
                guiHandler.openWindow("variants");
                return true;
            }
            return false;
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (!event.getView().getBottomInventory().equals(((InventoryClickEvent) event).getClickedInventory())) {
                    return;
                }
            }
            CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
            List<CustomItem> items = inputSlot == 2 ? anvilRecipe.getResults() : anvilRecipe.getInput(inputSlot);

            CustomItem input = !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR);
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
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomAnvilRecipe anvilRecipe = guiHandler.getCustomCache().getAnvilRecipe();
            List<CustomItem> items = inputSlot == 2 ? anvilRecipe.getResults() : anvilRecipe.getInput(inputSlot);
            return InventoryUtils.isCustomItemsListEmpty(items) ? new ItemStack(Material.AIR) : items.get(0).create();
        }));
    }
}
