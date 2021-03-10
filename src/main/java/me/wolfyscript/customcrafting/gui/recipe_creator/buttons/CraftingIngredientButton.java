package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.utils.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingIngredientButton extends ItemInputButton<CCCache> {

    public CraftingIngredientButton(int recipeSlot, CustomCrafting customCrafting) {
        super("crafting.container_" + recipeSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            CraftingRecipe<?> workbench = cache.getCraftingRecipe();
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                if ((!(workbench instanceof EliteCraftingRecipe) && recipeSlot == 9) || (workbench instanceof EliteCraftingRecipe && recipeSlot == 36)) {
                    if (workbench.getResults() != null) {
                        cache.getVariantsData().setSlot(recipeSlot);
                        cache.getVariantsData().setVariants(workbench.getResults());
                    }
                } else {
                    cache.getIngredientData().setSlot(recipeSlot);
                    if (workbench.getIngredients(recipeSlot) != null) {
                        cache.getIngredientData().setIngredient(workbench.getIngredients(recipeSlot));
                        cache.getIngredientData().setNew(false);
                    } else {
                        cache.getIngredientData().setIngredient(new Ingredient());
                        cache.getIngredientData().setNew(true);
                    }
                }
                guiHandler.openWindow("variants");
                return true;
            }
            return false;
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                return;
            }
            CraftingRecipe<?> workbench = cache.getCraftingRecipe();
            CustomItem customItem = !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null;
            if ((!(workbench instanceof EliteCraftingRecipe) && recipeSlot == 9) || (workbench instanceof EliteCraftingRecipe && recipeSlot == 36)) {
                workbench.setResult(0, customItem);
            } else {
                workbench.setIngredient(recipeSlot, 0, customItem);
            }
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CraftingRecipe<?> workbench = cache.getCraftingRecipe();
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
