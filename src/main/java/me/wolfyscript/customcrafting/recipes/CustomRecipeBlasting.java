package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;

public class CustomRecipeBlasting extends CustomRecipeCooking<CustomRecipeBlasting, BlastingRecipe> {

    public CustomRecipeBlasting(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomRecipeBlasting() {
        super();
    }

    public CustomRecipeBlasting(CustomRecipeBlasting customRecipeBlasting){
        super(customRecipeBlasting);
    }

    @Override
    public BlastingRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            return new BlastingRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getChoices().get(0).create(), getRecipeChoice(), getExp(), getCookingTime());
        }
        return null;
    }

    @Override
    public RecipeType<CustomRecipeBlasting> getRecipeType() {
        return RecipeType.BLAST_FURNACE;
    }

    @Override
    public CustomRecipeBlasting clone() {
        return new CustomRecipeBlasting(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.BLAST_FURNACE);
    }
}
