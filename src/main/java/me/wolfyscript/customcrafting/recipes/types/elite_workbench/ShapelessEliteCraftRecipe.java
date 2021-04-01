package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.IShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShapelessEliteCraftRecipe extends EliteCraftingRecipe implements IShapelessCraftingRecipe {

    public ShapelessEliteCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        shapeless = true;
    }

    public ShapelessEliteCraftRecipe() {
        super();
        this.shapeless = true;
    }

    public ShapelessEliteCraftRecipe(EliteCraftingRecipe eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
        this.shapeless = true;
    }

    public ShapelessEliteCraftRecipe(ShapelessEliteCraftRecipe eliteCraftingRecipe) {
        this((EliteCraftingRecipe) eliteCraftingRecipe);
    }

    @Override
    public CraftingData check(ItemStack[] matrix, List<List<ItemStack>> ingredients) {
        List<Character> usedKeys = new ArrayList<>();
        HashMap<Vec2d, CustomItem> foundItems = new HashMap<>();
        for (int i = 0; i < ingredients.size(); i++) {
            for (int j = 0; j < ingredients.get(i).size(); j++) {
                ItemStack itemStack = ingredients.get(i).get(j);
                if (itemStack == null) continue;
                CustomItem item = checkIngredient(getIngredients(), usedKeys, itemStack, isExactMeta());
                if (item != null) {
                    foundItems.put(new Vec2d(j, i), item);
                }
            }
        }
        if (usedKeys.containsAll(getIngredients().keySet())) {
            return new CraftingData(this, foundItems, matrix);
        }
        return null;
    }

    @Override
    public ShapelessEliteCraftRecipe clone() {
        return new ShapelessEliteCraftRecipe(this);
    }
}
