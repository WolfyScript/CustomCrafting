package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.SmokingRecipe;

public class CustomRecipeSmoking extends CustomRecipeCooking<CustomRecipeSmoking, SmokingRecipe> {

    public CustomRecipeSmoking(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomRecipeSmoking(NamespacedKey key) {
        super(key);
    }

    public CustomRecipeSmoking(CustomRecipeSmoking customRecipeSmoking) {
        super(customRecipeSmoking);
    }

    @Override
    public RecipeType<CustomRecipeSmoking> getRecipeType() {
        return RecipeType.SMOKER;
    }

    @Override
    public CustomRecipeSmoking clone() {
        return new CustomRecipeSmoking(this);
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
