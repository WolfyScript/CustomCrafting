package me.wolfyscript.customcrafting.recipes.types.smoker;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.SmokingRecipe;

public class CustomSmokerRecipe extends CustomCookingRecipe<CustomSmokerRecipe, SmokingRecipe> {

    public CustomSmokerRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomSmokerRecipe() {
        super();
    }

    public CustomSmokerRecipe(CustomSmokerRecipe customSmokerRecipe) {
        super(customSmokerRecipe);
    }

    @Override
    public RecipeType<CustomSmokerRecipe> getRecipeType() {
        return Types.SMOKER;
    }

    @Override
    public CustomSmokerRecipe clone() {
        return new CustomSmokerRecipe(this);
    }

    @Override
    public SmokingRecipe getVanillaRecipe() {
        return new SmokingRecipe(getNamespacedKey().toBukkit(), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime());
    }
}
