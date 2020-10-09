package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BrewingContainerButton extends ItemInputButton {

    public BrewingContainerButton(int recipeSlot, CustomCrafting customCrafting) {
        super("brewing.container_" + recipeSlot, new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.isRightClick() && event.isShiftClick()) {
                List<CustomItem> variants = new ArrayList<>();
                switch (recipeSlot) {
                    case 0:
                        variants = brewingRecipe.getIngredients();
                        break;
                    case 1:
                        variants = brewingRecipe.getAllowedItems();
                        break;
                    case 2:
                        variants = brewingRecipe.getResults();
                        break;
                }
                if (variants == null) variants = new ArrayList<>();
                cache.getVariantsData().setSlot(recipeSlot);
                cache.getVariantsData().setVariants(variants);
                guiHandler.changeToInv("variants");
                return true;
            } else {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    CustomItem customItem = new CustomItem(Material.AIR);
                    if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                        customItem = CustomItem.getReferenceByItemStack(inventory.getItem(slot));
                    }
                    List<CustomItem> inputs;
                    switch (recipeSlot) {
                        case 0:
                            inputs = brewingRecipe.getIngredients();
                            if (inputs.size() > 0) {
                                inputs.set(0, customItem);
                            } else {
                                inputs.add(customItem);
                            }
                            brewingRecipe.setIngredients(inputs);
                            break;
                        case 1:
                            inputs = brewingRecipe.getAllowedItems();
                            if (inputs.size() > 0) {
                                inputs.set(0, customItem);
                            } else {
                                inputs.add(customItem);
                            }
                            brewingRecipe.setAllowedItems(inputs);
                            break;
                        case 2:
                            inputs = brewingRecipe.getResults();
                            if (inputs.size() > 0) {
                                inputs.set(0, customItem);
                            } else {
                                inputs.add(customItem);
                            }
                            brewingRecipe.setResult(inputs);
                            break;
                    }
                });
            }
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            BrewingRecipe brewingRecipe = ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe();
            switch (recipeSlot) {
                case 0:
                    return !InventoryUtils.isCustomItemsListEmpty(brewingRecipe.getIngredients()) ? brewingRecipe.getIngredients().get(0).create() : new ItemStack(Material.AIR);
                case 1:
                    return !InventoryUtils.isCustomItemsListEmpty(brewingRecipe.getAllowedItems()) ? brewingRecipe.getAllowedItems().get(0).create() : new ItemStack(Material.AIR);
                case 2:
                    return !InventoryUtils.isCustomItemsListEmpty(brewingRecipe.getResults()) ? brewingRecipe.getResults().get(0).create() : new ItemStack(Material.AIR);
            }
            return new ItemStack(Material.AIR);
        }));
    }
}
