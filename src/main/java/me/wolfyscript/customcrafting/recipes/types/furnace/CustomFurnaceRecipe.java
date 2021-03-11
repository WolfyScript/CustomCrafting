package me.wolfyscript.customcrafting.recipes.types.furnace;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.stream.Collectors;

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
        RecipeChoice choice = isExactMeta() ? new RecipeChoice.ExactChoice(getSource().parallelStream().map(CustomItem::create).collect(Collectors.toList())) : new RecipeChoice.MaterialChoice(getSource().parallelStream().map(i -> i.create().getType()).collect(Collectors.toList()));
        return new FurnaceRecipe(getNamespacedKey().toBukkit(CustomCrafting.getInst()), getResult().create(), choice, getExp(), getCookingTime());
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
