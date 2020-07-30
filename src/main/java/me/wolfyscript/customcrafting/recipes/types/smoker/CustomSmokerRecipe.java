package me.wolfyscript.customcrafting.recipes.types.smoker;

import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

import java.util.stream.Collectors;

public class CustomSmokerRecipe extends CustomCookingRecipe<SmokingRecipe> {

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
    public RecipeType getRecipeType() {
        return RecipeType.SMOKER;
    }

    @Override
    public SmokingRecipe getVanillaRecipe() {
        return new SmokingRecipe(new org.bukkit.NamespacedKey(getNamespacedKey().getNamespace(), getNamespacedKey().getKey()), getCustomResult().create(), new RecipeChoice.ExactChoice(getSource().stream().map(customItem -> customItem.create()).collect(Collectors.toList())), getExp(), getCookingTime());
    }
}
