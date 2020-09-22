package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftingIngredientButton extends ItemInputButton {

    public CraftingIngredientButton(int recipeSlot, CustomCrafting customCrafting) {
        super("crafting.container_" + recipeSlot, new ButtonState("", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            CraftingRecipe<?> workbench = cache.getCraftingRecipe();
            if (event.isRightClick() && event.isShiftClick()) {
                List<CustomItem> variants = new ArrayList<>();
                if ((!(workbench instanceof EliteCraftingRecipe) && recipeSlot == 9) || (workbench instanceof EliteCraftingRecipe && recipeSlot == 36)) {
                    if (workbench.getResults() != null) {
                        variants = workbench.getResults();
                    }
                } else if (workbench.getIngredients(recipeSlot) != null) {
                    variants = workbench.getIngredients(recipeSlot);
                }
                cache.getVariantsData().setSlot(recipeSlot);
                cache.getVariantsData().setVariants(variants);
                guiHandler.changeToInv("variants");
                return true;
            } else {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    CustomItem customItem = inventory.getItem(slot) != null ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : null;
                    if ((!(workbench instanceof EliteCraftingRecipe) && recipeSlot == 9) || (workbench instanceof EliteCraftingRecipe && recipeSlot == 36)) {
                        workbench.setResult(0, customItem);
                    } else {
                        workbench.setIngredient(recipeSlot, 0, customItem);
                    }
                });
            }
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            CraftingRecipe<?> workbench = ((TestCache) guiHandler.getCustomCache()).getCraftingRecipe();
            itemStack = new ItemStack(Material.AIR);
            if ((!(workbench instanceof EliteCraftingRecipe) && recipeSlot == 9) || (workbench instanceof EliteCraftingRecipe && recipeSlot == 36)) {
                if (workbench.getResult() != null) {
                    itemStack = workbench.getResult().create();
                }
            } else if (workbench.getIngredient(recipeSlot) != null) {
                itemStack = workbench.getIngredient(recipeSlot).create();
            }
            return itemStack;
        }));
    }
}
