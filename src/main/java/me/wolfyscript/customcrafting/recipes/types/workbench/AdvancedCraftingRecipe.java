package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;

public abstract class AdvancedCraftingRecipe extends CraftingRecipe<AdvancedCraftingRecipe> {

    private boolean vanillaRecipe;

    public AdvancedCraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.vanillaRecipe = node.path("vanillaRecipe").asBoolean(false);
    }

    public AdvancedCraftingRecipe() {
        super();
        this.vanillaRecipe = false;
    }

    public AdvancedCraftingRecipe(AdvancedCraftingRecipe advancedCraftingRecipe) {
        super(advancedCraftingRecipe);
        this.vanillaRecipe = advancedCraftingRecipe.allowVanillaRecipe();
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("vanillaRecipe", vanillaRecipe);
    }

    public boolean allowVanillaRecipe() {
        return vanillaRecipe;
    }

    public void setAllowVanillaRecipe(boolean vanillaRecipe) {
        this.vanillaRecipe = vanillaRecipe;
    }

    @Override
    public RecipeType<AdvancedCraftingRecipe> getRecipeType() {
        return Types.WORKBENCH;
    }
}
