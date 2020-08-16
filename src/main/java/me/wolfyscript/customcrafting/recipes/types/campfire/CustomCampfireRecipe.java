package me.wolfyscript.customcrafting.recipes.types.campfire;

import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.stream.Collectors;

public class CustomCampfireRecipe extends CustomCookingRecipe<CampfireRecipe> {

    public CustomCampfireRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomCampfireRecipe() {
        super();
    }

    public CustomCampfireRecipe(CustomCampfireRecipe customCampfireRecipe){
        super(customCampfireRecipe);
    }

    @Override
    public CampfireRecipe getVanillaRecipe() {
        return new CampfireRecipe(new org.bukkit.NamespacedKey(getNamespacedKey().getNamespace(), getNamespacedKey().getKey()), getCustomResult().create(), new RecipeChoice.ExactChoice(getSource().stream().map(customItem -> customItem.create()).collect(Collectors.toList())), getExp(), getCookingTime());
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.CAMPFIRE;
    }
}
