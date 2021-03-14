package me.wolfyscript.customcrafting.recipes.types.furnace;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;

public class CustomFurnaceRecipe extends CustomCookingRecipe<CustomFurnaceRecipe, FurnaceRecipe> {

    public CustomFurnaceRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomFurnaceRecipe() {
        super();
    }

    public CustomFurnaceRecipe(CustomFurnaceRecipe customFurnaceRecipe){
        super(customFurnaceRecipe);
    }

    @Override
    public FurnaceRecipe getVanillaRecipe() {
        return new FurnaceRecipe(getNamespacedKey().toBukkit(), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime());
    }

    @Override
    public RecipeType<CustomFurnaceRecipe> getRecipeType() {
        return Types.FURNACE;
    }

    @Override
    public CustomFurnaceRecipe clone() {
        return new CustomFurnaceRecipe(this);
    }
}
