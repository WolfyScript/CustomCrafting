package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrewingContainerButton extends ItemInputButton<CCCache> {

    public BrewingContainerButton(int recipeSlot, CustomCrafting customCrafting) {
        super("brewing.container_" + recipeSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                List<CustomItem> variants = recipeSlot == 1 ? brewingRecipe.getAllowedItems() : brewingRecipe.getResults();
                if (variants == null) variants = new ArrayList<>();
                cache.getVariantsData().setSlot(recipeSlot);
                cache.getVariantsData().setVariants(variants);
                guiHandler.openWindow("variants");
                return true;
            }
            return false;
        },(cache, guiHandler, player, guiInventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                return;
            }
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            CustomItem customItem = new CustomItem(Material.AIR);
            if (!ItemUtils.isAirOrNull(itemStack)) {
                customItem = CustomItem.getReferenceByItemStack(itemStack);
            }
            List<CustomItem> inputs;
            if (recipeSlot == 1) {
                inputs = brewingRecipe.getAllowedItems();
                if (inputs.size() > 0) {
                    inputs.set(0, customItem);
                } else {
                    inputs.add(customItem);
                }
                brewingRecipe.setAllowedItems(inputs);
            } else {
                inputs = brewingRecipe.getResults();
                if (inputs.size() > 0) {
                    inputs.set(0, customItem);
                } else {
                    inputs.add(customItem);
                }
                brewingRecipe.setResult(inputs);
            }
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            BrewingRecipe brewingRecipe = guiHandler.getCustomCache().getBrewingRecipe();
            if (recipeSlot == 1) {
                return !InventoryUtils.isCustomItemsListEmpty(brewingRecipe.getAllowedItems()) ? brewingRecipe.getAllowedItems().get(0).create() : new ItemStack(Material.AIR);
            }
            return !InventoryUtils.isCustomItemsListEmpty(brewingRecipe.getResults()) ? brewingRecipe.getResults().get(0).create() : new ItemStack(Material.AIR);
        }));
    }
}
