package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.RecipePacketType;
import me.wolfyscript.customcrafting.recipes.types.IShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public CraftingData check(List<List<ItemStack>> ingredients) {
        List<Character> usedKeys = new ArrayList<>();
        Map<Vec2d, CustomItem> foundItems = new HashMap<>();
        Map<Ingredient, Vec2d> mappedIngredients = new HashMap<>();
        for (int i = 0; i < ingredients.size(); i++) {
            for (int j = 0; j < ingredients.get(i).size(); j++) {
                checkIngredient(j, i, getIngredients(), usedKeys, foundItems, mappedIngredients, ingredients.get(i).get(j), isExactMeta());
            }
        }
        if (usedKeys.containsAll(getIngredients().keySet())) {
            return new CraftingData(this, foundItems, mappedIngredients);
        }
        return null;
    }

    @Override
    public RecipePacketType getPacketType() {
        return RecipePacketType.ELITE_CRAFTING_SHAPELESS;
    }

    @Override
    public ShapelessEliteCraftRecipe clone() {
        return new ShapelessEliteCraftRecipe(this);
    }
}
