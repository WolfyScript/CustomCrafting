package me.wolfyscript.customcrafting.utils.recipe_item;

import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Ingredient extends RecipeItemStack {

    public Ingredient() {
        super();
    }

    public Ingredient(Material... materials) {
        super(materials);
    }

    public Ingredient(ItemStack... items) {
        super(items);
    }

    public Ingredient(NamespacedKey... tags) {
        super(tags);
    }

    public Ingredient(APIReference... references) {
        super(references);
    }

    public Ingredient(List<APIReference> references, List<NamespacedKey> tags) {
        super(references, tags);
    }

}
