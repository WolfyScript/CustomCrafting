package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;

import java.util.List;

public class IngredientData {

    private int slot;
    private Ingredient ingredient;

    public IngredientData() {
        this.ingredient = null;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void put(int variantSlot, CustomItem variant) {
        List<APIReference> references = ingredient.getItems();
        if (references.size() > variantSlot) {
            references.set(variantSlot, variant.getApiReference());
        } else {
            references.add(variant.getApiReference());
        }
    }
}
