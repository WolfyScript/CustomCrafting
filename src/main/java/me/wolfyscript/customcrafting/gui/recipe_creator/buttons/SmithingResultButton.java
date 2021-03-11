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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SmithingResultButton extends ItemInputButton<CCCache> {

    public SmithingResultButton(CustomCrafting customCrafting) {
        super("result", new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                cache.getVariantsData().setSlot(2);
                cache.getVariantsData().setVariants(smithingRecipe.getResults());
                guiHandler.openWindow("variants");
                return true;
            }
            return false;
        }, (cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                return;
            }
            CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
            List<CustomItem> items = smithingRecipe.getResults();
            CustomItem input = !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : new CustomItem(Material.AIR);
            if (items.size() > 0) {
                items.set(0, input);
            } else {
                items.add(input);
            }
            smithingRecipe.setResult(items);
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomSmithingRecipe smithingRecipe = guiHandler.getCustomCache().getSmithingRecipe();
            List<CustomItem> items = smithingRecipe.getResults();
            return InventoryUtils.isCustomItemsListEmpty(items) ? new ItemStack(Material.AIR) : items.get(0).create();
        }));
    }
}
