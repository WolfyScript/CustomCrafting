package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

public class CraftingRecipeShapeless extends AbstractRecipeShapeless<CraftingRecipeShapeless, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapelessRecipe> {

    public CraftingRecipeShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    public CraftingRecipeShapeless(NamespacedKey key) {
        super(key, 3, new AdvancedRecipeSettings());
    }

    public CraftingRecipeShapeless(CraftingRecipeShapeless craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeShapeless> getRecipeType() {
        return RecipeType.WORKBENCH_SHAPELESS;
    }

    @Override
    public CraftingRecipeShapeless clone() {
        return new CraftingRecipeShapeless(this);
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe getVanillaRecipe() {
        if (!getSettings().isAllowVanillaRecipe() && !getResult().isEmpty()) {
            var shapelessRecipe = new org.bukkit.inventory.ShapelessRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            for (Ingredient value : ingredients) {
                shapelessRecipe.addIngredient(new RecipeChoice.ExactChoice(value.getChoices().stream().map(CustomItem::create).distinct().toList()));
            }
            shapelessRecipe.setGroup(getGroup());
            return shapelessRecipe;
        }
        return null;
    }
}
