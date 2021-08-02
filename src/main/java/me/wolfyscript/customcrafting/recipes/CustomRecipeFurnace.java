package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;

public class CustomRecipeFurnace extends CustomRecipeCooking<CustomRecipeFurnace, FurnaceRecipe> {

    public CustomRecipeFurnace(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomRecipeFurnace() {
        super();
    }

    public CustomRecipeFurnace(CustomRecipeFurnace customRecipeFurnace){
        super(customRecipeFurnace);
    }

    @Override
    public FurnaceRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            return new FurnaceRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime());
        }
        return null;
    }

    @Override
    public RecipeType<CustomRecipeFurnace> getRecipeType() {
        return RecipeType.FURNACE;
    }

    @Override
    public CustomRecipeFurnace clone() {
        return new CustomRecipeFurnace(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.FURNACE);
    }
}
