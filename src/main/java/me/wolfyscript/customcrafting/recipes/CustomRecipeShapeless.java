package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

import java.util.stream.Collectors;

public class CustomRecipeShapeless extends AbstractRecipeShapeless<CustomRecipeShapeless, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapelessRecipe> {

    public CustomRecipeShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3);
    }

    public CustomRecipeShapeless() {
        super(3);
    }

    public CustomRecipeShapeless(CustomRecipeShapeless craftingRecipe) {
        super(craftingRecipe);
    }

    public CustomRecipeShapeless(CraftingRecipe<?, AdvancedRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CustomRecipeShapeless> getRecipeType() {
        return Types.WORKBENCH_SHAPELESS;
    }

    @Override
    public CustomRecipeShapeless clone() {
        return new CustomRecipeShapeless(this);
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe getVanillaRecipe() {
        if (!getSettings().isAllowVanillaRecipe() && !getResult().isEmpty()) {
            var shapelessRecipe = new org.bukkit.inventory.ShapelessRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            for (Ingredient value : getIngredients().values()) {
                shapelessRecipe.addIngredient(new RecipeChoice.ExactChoice(value.getChoices().parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList())));
            }
            shapelessRecipe.setGroup(getGroup());
            return shapelessRecipe;
        }
        return null;
    }
}
