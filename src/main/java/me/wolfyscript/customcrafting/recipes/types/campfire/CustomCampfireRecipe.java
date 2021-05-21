package me.wolfyscript.customcrafting.recipes.types.campfire;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;

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
        if (!getSource().isEmpty()) {
            return new CampfireRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime());
        }
        return null;
    }

    @Override
    public RecipeType<CustomCampfireRecipe> getRecipeType() {
        return Types.CAMPFIRE;
    }

    @Override
    public CustomCampfireRecipe clone() {
        return new CustomCampfireRecipe(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.CAMPFIRE);
    }
}
