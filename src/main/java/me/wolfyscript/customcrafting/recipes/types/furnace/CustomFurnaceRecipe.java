package me.wolfyscript.customcrafting.recipes.types.furnace;

import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.stream.Collectors;

public class CustomFurnaceRecipe extends CustomCookingRecipe<FurnaceRecipe> {

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
        return new FurnaceRecipe(new org.bukkit.NamespacedKey(getNamespacedKey().getNamespace(), getNamespacedKey().getKey()), getCustomResult().create(), new RecipeChoice.ExactChoice(getSource().stream().map(CustomItem::create).collect(Collectors.toList())), getExp(), getCookingTime());
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.FURNACE;
    }
}
