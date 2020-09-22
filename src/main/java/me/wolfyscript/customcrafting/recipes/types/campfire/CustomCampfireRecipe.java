package me.wolfyscript.customcrafting.recipes.types.campfire;

import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.stream.Collectors;

public class CustomCampfireRecipe extends CustomCookingRecipe<CustomCampfireRecipe, CampfireRecipe> {

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
        RecipeChoice choice = isExactMeta() ? new RecipeChoice.ExactChoice(getSource().stream().map(CustomItem::create).collect(Collectors.toList())) : new RecipeChoice.MaterialChoice(getSource().stream().map(i -> i.create().getType()).collect(Collectors.toList()));
        return new CampfireRecipe(new org.bukkit.NamespacedKey(getNamespacedKey().getNamespace(), getNamespacedKey().getKey()), getResult().create(), choice, getExp(), getCookingTime());
    }

    @Override
    public RecipeType<CustomCampfireRecipe> getRecipeType() {
        return RecipeType.CAMPFIRE;
    }

    @Override
    public CustomCampfireRecipe clone() {
        return new CustomCampfireRecipe(this);
    }
}
