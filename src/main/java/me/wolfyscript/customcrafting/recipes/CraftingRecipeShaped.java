package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

import java.util.stream.Collectors;

public class CraftingRecipeShaped extends AbstractRecipeShaped<CraftingRecipeShaped, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapedRecipe> {

    public CraftingRecipeShaped(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    public CraftingRecipeShaped() {
        super(3, new AdvancedRecipeSettings());
    }

    public CraftingRecipeShaped(CraftingRecipeShaped craftingRecipe) {
        super(craftingRecipe);
    }

    public CraftingRecipeShaped(CraftingRecipeShapeless craftingRecipe) {
        super(craftingRecipe);
    }

    public CraftingRecipeShaped(CraftingRecipe<?, AdvancedRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeShaped> getRecipeType() {
        return RecipeType.WORKBENCH_SHAPED;
    }

    @Override
    public CraftingRecipeShaped clone() {
        return new CraftingRecipeShaped(this);
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe getVanillaRecipe() {
        if (!getResult().isEmpty() && !ingredientsFlat.isEmpty()) {
            var recipe = new org.bukkit.inventory.ShapedRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            recipe.shape(getShape());
            getIngredients().forEach((character, items) -> recipe.setIngredient(character, new RecipeChoice.ExactChoice(items.getChoices().parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList()))));
            recipe.setGroup(getGroup());
            return recipe;
        }
        return null;
    }
}