package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.RecipePacketType;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.IShapelessCraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

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
    public CraftingData check(List<List<ItemStack>> ingredients) {
        return check(this, getIngredients(), isExactMeta(), ingredients);
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
