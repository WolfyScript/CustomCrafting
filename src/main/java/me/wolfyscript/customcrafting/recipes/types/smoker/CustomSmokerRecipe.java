package me.wolfyscript.customcrafting.recipes.types.smoker;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
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
    public boolean validType(Material material) {
        return material.equals(Material.SMOKER);
    }

    @Override
    public SmokingRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            return new SmokingRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime());
        }
        return null;
    }
}
