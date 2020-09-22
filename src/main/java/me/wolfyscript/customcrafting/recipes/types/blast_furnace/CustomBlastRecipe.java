package me.wolfyscript.customcrafting.recipes.types.blast_furnace;

import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.stream.Collectors;

public class CustomBlastRecipe extends CustomCookingRecipe<CustomBlastRecipe, BlastingRecipe> {

    public CustomBlastRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomBlastRecipe() {
        super();
    }

    public CustomBlastRecipe(CustomBlastRecipe customBlastRecipe){
        super(customBlastRecipe);
    }

    @Override
    public BlastingRecipe getVanillaRecipe() {
        RecipeChoice choice = isExactMeta() ? new RecipeChoice.ExactChoice(getSource().stream().map(CustomItem::create).collect(Collectors.toList())) : new RecipeChoice.MaterialChoice(getSource().stream().map(i -> i.create().getType()).collect(Collectors.toList()));
        return new BlastingRecipe(new org.bukkit.NamespacedKey(getNamespacedKey().getNamespace(), getNamespacedKey().getKey()), getResult().create(), choice, getExp(), getCookingTime());
    }

    @Override
    public RecipeType<CustomBlastRecipe> getRecipeType() {
        return RecipeType.BLAST_FURNACE;
    }

    @Override
    public CustomBlastRecipe clone() {
        return new CustomBlastRecipe(this);
    }
}
