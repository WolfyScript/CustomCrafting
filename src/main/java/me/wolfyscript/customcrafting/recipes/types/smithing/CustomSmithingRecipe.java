package me.wolfyscript.customcrafting.recipes.types.smithing;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.SlotResultTarget;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;

public class CustomSmithingRecipe extends CustomRecipe<CustomSmithingRecipe, SlotResultTarget> {

    private Ingredient base;
    private Ingredient addition;

    private boolean preserveEnchants;

    public CustomSmithingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        base = ItemLoader.loadIngredient(node.path("base"));
        addition = ItemLoader.loadIngredient(node.path("addition"));
        preserveEnchants = node.path("preserve_enchants").asBoolean(true);
    }

    public CustomSmithingRecipe() {
        super();
        this.base = new Ingredient();
        this.addition = new Ingredient();
        this.result = new Result<>();
        this.preserveEnchants = true;
    }

    public CustomSmithingRecipe(CustomSmithingRecipe customSmithingRecipe) {
        super(customSmithingRecipe);
        this.result = customSmithingRecipe.getResult();
        this.base = customSmithingRecipe.getBase();
        this.addition = customSmithingRecipe.getAddition();
        this.preserveEnchants = customSmithingRecipe.isPreserveEnchants();
    }

    @Override
    public RecipeType<CustomSmithingRecipe> getRecipeType() {
        return Types.SMITHING;
    }

    public Ingredient getAddition() {
        return addition;
    }

    public void setAddition(Ingredient addition) {
        this.addition = addition;
    }

    public Ingredient getBase() {
        return base;
    }

    public void setBase(Ingredient base) {
        this.base = base;
    }

    public boolean isPreserveEnchants() {
        return preserveEnchants;
    }

    public void setPreserveEnchants(boolean preserveEnchants) {
        this.preserveEnchants = preserveEnchants;
    }

    @Override
    public CustomSmithingRecipe clone() {
        return new CustomSmithingRecipe(this);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((IngredientContainerButton) cluster.getButton("ingredient.container_10")).setVariants(guiHandler, getBase());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_13")).setVariants(guiHandler, getAddition());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_23")).setVariants(guiHandler, this.getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        event.setButton(0, "back");

        event.setButton(19, new NamespacedKey("recipe_book", "ingredient.container_10"));
        event.setButton(21, new NamespacedKey("recipe_book", "ingredient.container_13"));
        event.setButton(23, new NamespacedKey("recipe_book", "smithing"));
        event.setButton(25, new NamespacedKey("recipe_book", "ingredient.container_23"));
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("preserve_enchants", preserveEnchants);
        gen.writeObjectField("result", result);
        gen.writeObjectField("base", base);
        gen.writeObjectField("addition", addition);
    }
}
