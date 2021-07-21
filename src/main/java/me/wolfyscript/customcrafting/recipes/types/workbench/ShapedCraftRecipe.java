package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipePacketType;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.AbstractShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.stream.Collectors;

public class ShapedCraftRecipe extends AbstractShapedCraftRecipe<ShapedCraftRecipe> implements AdvancedCraftingRecipe, ICustomVanillaRecipe<ShapedRecipe> {

    public ShapedCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3);
    }

    public ShapedCraftRecipe() {
        super(3);
    }

    public ShapedCraftRecipe(ShapedCraftRecipe craftingRecipe) {
        super(craftingRecipe);
    }

    public ShapedCraftRecipe(ShapelessCraftRecipe craftingRecipe) {
        super(craftingRecipe);
    }

    public ShapedCraftRecipe(CraftingRecipe<?> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<ShapedCraftRecipe> getRecipeType() {
        return Types.WORKBENCH_SHAPED;
    }

    @Override
    public RecipePacketType getPacketType() {
        return RecipePacketType.CRAFTING_SHAPED;
    }

    @Override
    public ShapedCraftRecipe clone() {
        return new ShapedCraftRecipe(this);
    }

    @Override
    public ShapedRecipe getVanillaRecipe() {
        if (!getResult().isEmpty() && getShape().length > 0) {
            var recipe = new ShapedRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            recipe.shape(getShape());
            getIngredients().forEach((character, items) -> recipe.setIngredient(character, new RecipeChoice.ExactChoice(items.getChoices().parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList()))));
            recipe.setGroup(getGroup());
            return recipe;
        }
        return null;
    }

    @Override
    public boolean allowVanillaRecipe() {
        return false;
    }

    @Override
    public void setAllowVanillaRecipe(boolean vanillaRecipe) {

    }
}