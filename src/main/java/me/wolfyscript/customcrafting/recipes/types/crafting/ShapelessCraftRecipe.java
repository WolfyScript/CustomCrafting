package me.wolfyscript.customcrafting.recipes.types.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.AbstractShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.stream.Collectors;

public class ShapelessCraftRecipe extends AbstractShapelessCraftingRecipe<ShapelessCraftRecipe, AdvancedRecipeSettings> implements ICustomVanillaRecipe<ShapelessRecipe> {

    public ShapelessCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3);
    }

    public ShapelessCraftRecipe() {
        super(3);
    }

    public ShapelessCraftRecipe(ShapelessCraftRecipe craftingRecipe) {
        super(craftingRecipe);
    }

    public ShapelessCraftRecipe(CraftingRecipe<?, AdvancedRecipeSettings> craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<ShapelessCraftRecipe> getRecipeType() {
        return Types.WORKBENCH_SHAPELESS;
    }

    @Override
    public ShapelessCraftRecipe clone() {
        return new ShapelessCraftRecipe(this);
    }

    @Override
    public ShapelessRecipe getVanillaRecipe() {
        if (!getSettings().isAllowVanillaRecipe() && !getResult().isEmpty()) {
            var shapelessRecipe = new ShapelessRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            for (Ingredient value : getIngredients().values()) {
                shapelessRecipe.addIngredient(new RecipeChoice.ExactChoice(value.getChoices().parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList())));
            }
            shapelessRecipe.setGroup(getGroup());
            return shapelessRecipe;
        }
        return null;
    }
}
