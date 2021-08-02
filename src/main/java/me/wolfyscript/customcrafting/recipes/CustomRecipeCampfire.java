package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;

public class CustomRecipeCampfire extends CustomRecipeCooking<CustomRecipeCampfire, CampfireRecipe> {

    public CustomRecipeCampfire(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public CustomRecipeCampfire() {
        super();
    }

    public CustomRecipeCampfire(CustomRecipeCampfire customRecipeCampfire){
        super(customRecipeCampfire);
    }

    @Override
    public CampfireRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            return new CampfireRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime());
        }
        return null;
    }

    @Override
    public RecipeType<CustomRecipeCampfire> getRecipeType() {
        return RecipeType.CAMPFIRE;
    }

    @Override
    public CustomRecipeCampfire clone() {
        return new CustomRecipeCampfire(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.CAMPFIRE);
    }
}
