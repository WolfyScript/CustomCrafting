package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RecipeIngredientButton extends ItemInputButton<CCCache> {

    public RecipeIngredientButton(int recipeSlot, CustomCrafting customCrafting) {
        super("recipe.ingredient_" + recipeSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                cache.getIngredientData().setSlot(recipeSlot);
                Ingredient ingredient = getIngredient(cache, recipeSlot);
                if (ingredient != null) {
                    cache.getIngredientData().setIngredient(ingredient);
                    cache.getIngredientData().setNew(false);
                } else {
                    cache.getIngredientData().setIngredient(new Ingredient());
                    cache.getIngredientData().setNew(true);
                }
                guiHandler.openWindow("ingredients");
                return true;
            }
            return false;
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                if (((InventoryClickEvent) event).getClickedInventory() != null && ((InventoryClickEvent) event).getClickedInventory().equals(event.getView().getBottomInventory())) {
                    return;
                }
            }
            cache.getCraftingRecipe().getIngredients(recipeSlot).put(0, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            Ingredient ingredient = getIngredient(cache, recipeSlot);
            if (ingredient != null && ingredient.getItems().size() > 0) {
                return CustomItem.with(ingredient.getItems().get(0)).create();
            }
            return ItemUtils.AIR;
        }));
    }

    public static Ingredient getIngredient(CCCache cache, int recipeSlot) {
        Ingredient ingredient = null;
        switch (cache.getRecipeType().getType()) {
            case ELITE_WORKBENCH:
            case WORKBENCH:
                ingredient = cache.getCraftingRecipe().getIngredients(recipeSlot);
                break;
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
            case CAMPFIRE:
                ingredient = cache.getCookingRecipe().getSource();
                break;
            case SMITHING:
                ingredient = recipeSlot == 0 ? cache.getSmithingRecipe().getBase() : cache.getSmithingRecipe().getAddition();
                break;
            case GRINDSTONE:
                ingredient = recipeSlot == 0 ? cache.getGrindstoneRecipe().getInputTop() : cache.getGrindstoneRecipe().getInputBottom();
                break;
            case STONECUTTER:
                ingredient = cache.getStonecutterRecipe().getSource();
                break;
            case ANVIL:
                ingredient = cache.getAnvilRecipe().getInput(recipeSlot);
                break;
            case CAULDRON:
                //TODO: Find a way to implement Ingredients
                break;
            case BREWING_STAND:
                ingredient = cache.getBrewingRecipe().getIngredients();
        }
        return ingredient;
    }
}
