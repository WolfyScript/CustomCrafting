package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.IngredientMenu;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ButtonRecipeIngredient extends ItemInputButton<CCCache> {

    public ButtonRecipeIngredient(int recipeSlot) {
        super("recipe.ingredient_" + recipeSlot, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            Ingredient ingredient = IngredientMenu.getIngredient(cache, recipeSlot);
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick() && ((InventoryClickEvent) event).isShiftClick()) {
                cache.getIngredientData().setSlot(recipeSlot);
                if (ingredient != null) {
                    cache.getIngredientData().setIngredient(ingredient);
                    cache.getIngredientData().setNew(false);
                } else {
                    cache.getIngredientData().setIngredient(new Ingredient());
                    cache.getIngredientData().setNew(true);
                }
                guiHandler.openWindow("ingredient");
                return true;
            }
            return ingredient != null && ingredient.getItems().isEmpty() && !ingredient.getTags().isEmpty();
        }, (cache, guiHandler, player, inventory, itemStack, i, event) -> {
            Ingredient ingredient = IngredientMenu.getIngredient(cache, recipeSlot);
            if (ingredient != null && ingredient.getItems().isEmpty() && !ingredient.getTags().isEmpty()) {
                return;
            }
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT) && event.getView().getTopInventory().equals(((InventoryClickEvent) event).getClickedInventory())) {
                return;
            }
            if (ingredient == null) {
                ingredient = new Ingredient();
                IngredientMenu.setIngredient(cache, ingredient, recipeSlot);
            }
            ingredient.put(0, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            Ingredient ingredient = IngredientMenu.getIngredient(cache, recipeSlot);
            return ingredient != null ? ingredient.getItemStack() : new ItemStack(Material.AIR);
        }));
    }

}
