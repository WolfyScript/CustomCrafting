package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;

public class CustomRecipeGrindstone extends CustomRecipe<CustomRecipeGrindstone> {

    private Ingredient inputTop;
    private Ingredient inputBottom;
    private int xp;

    public CustomRecipeGrindstone(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.xp = node.path("exp").intValue();
        this.inputTop = ItemLoader.loadIngredient(node.path("input_top"));
        this.inputBottom = ItemLoader.loadIngredient(node.path("input_bottom"));
    }

    public CustomRecipeGrindstone() {
        super();
        this.result = new Result();
        this.inputTop = new Ingredient();
        this.inputBottom = new Ingredient();
        this.xp = 0;
    }

    public CustomRecipeGrindstone(CustomRecipeGrindstone customRecipeGrindstone) {
        super(customRecipeGrindstone);
        this.inputBottom = customRecipeGrindstone.getInputBottom();
        this.inputTop = customRecipeGrindstone.getInputTop();
        this.xp = customRecipeGrindstone.getXp();
    }

    @Override
    public RecipeType<CustomRecipeGrindstone> getRecipeType() {
        return RecipeType.GRINDSTONE;
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            setInputTop(ingredient);
        } else {
            setInputBottom(ingredient);
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getInputTop() : getInputBottom();
    }

    public Ingredient getInputTop() {
        return inputTop;
    }

    public void setInputTop(Ingredient inputTop) {
        this.inputTop = inputTop;
    }

    public Ingredient getInputBottom() {
        return inputBottom;
    }

    public void setInputBottom(Ingredient inputBottom) {
        this.inputBottom = inputBottom;
        Preconditions.checkArgument(!inputBottom.isEmpty(), "Invalid Bottom ingredient! Recipe must have non-air base ingredient!");
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    @Override
    public CustomRecipeGrindstone clone() {
        return new CustomRecipeGrindstone(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeNumberField("exp", xp);
        gen.writeObjectField("result", result);
        gen.writeObjectField("input_top", getInputTop());
        gen.writeObjectField("input_bottom", getInputBottom());
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, getInputTop());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_29")).setVariants(guiHandler, getInputBottom());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        event.setButton(11, new NamespacedKey("recipe_book", "ingredient.container_11"));
        event.setButton(12, MainCluster.GLASS_GREEN);
        event.setButton(21, MainCluster.GLASS_GREEN);
        event.setButton(22, new NamespacedKey("recipe_book", "grindstone"));
        event.setButton(23, MainCluster.GLASS_GREEN);
        event.setButton(24, new NamespacedKey("recipe_book", "ingredient.container_24"));
        event.setButton(29, new NamespacedKey("recipe_book", "ingredient.container_29"));
        event.setButton(30, MainCluster.GLASS_GREEN);
    }
}
