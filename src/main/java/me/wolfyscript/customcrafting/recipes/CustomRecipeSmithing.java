package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;

public class CustomRecipeSmithing extends CustomRecipe<CustomRecipeSmithing> {

    private static final String KEY_BASE = "base";
    private static final String KEY_ADDITION = "addition";

    private Ingredient base;
    private Ingredient addition;

    private boolean preserveEnchants;

    public CustomRecipeSmithing(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        base = ItemLoader.loadIngredient(node.path(KEY_BASE));
        addition = ItemLoader.loadIngredient(node.path(KEY_ADDITION));
        preserveEnchants = node.path("preserve_enchants").asBoolean(true);
    }

    public CustomRecipeSmithing(NamespacedKey key) {
        super(key);
        this.base = new Ingredient();
        this.addition = new Ingredient();
        this.result = new Result();
        this.preserveEnchants = true;
    }

    public CustomRecipeSmithing(CustomRecipeSmithing customRecipeSmithing) {
        super(customRecipeSmithing);
        this.result = customRecipeSmithing.getResult();
        this.base = customRecipeSmithing.getBase();
        this.addition = customRecipeSmithing.getAddition();
        this.preserveEnchants = customRecipeSmithing.isPreserveEnchants();
    }

    @Override
    public RecipeType<CustomRecipeSmithing> getRecipeType() {
        return RecipeType.SMITHING;
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getBase() : getAddition();
    }

    public Ingredient getAddition() {
        return addition;
    }

    public void setAddition(Ingredient addition) {
        this.addition = addition;
        Preconditions.checkArgument(!this.addition.isEmpty(), "Invalid Addition! Recipe must have non-air addition!");
    }

    public Ingredient getBase() {
        return base;
    }

    public void setBase(Ingredient base) {
        this.base = base;
        Preconditions.checkArgument(!this.base.isEmpty(), "Invalid Base ingredient! Recipe must have non-air base ingredient!");
    }

    public boolean isPreserveEnchants() {
        return preserveEnchants;
    }

    public void setPreserveEnchants(boolean preserveEnchants) {
        this.preserveEnchants = preserveEnchants;
    }

    @Override
    public CustomRecipeSmithing clone() {
        return new CustomRecipeSmithing(this);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((IngredientContainerButton) cluster.getButton(IngredientContainerButton.key(10))).setVariants(guiHandler, getBase());
        ((IngredientContainerButton) cluster.getButton(IngredientContainerButton.key(13))).setVariants(guiHandler, getAddition());
        ((IngredientContainerButton) cluster.getButton(IngredientContainerButton.key(23))).setVariants(guiHandler, this.getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        event.setButton(19, IngredientContainerButton.namespacedKey(10));
        event.setButton(21, IngredientContainerButton.namespacedKey(13));
        event.setButton(23, new NamespacedKey("recipe_book", "smithing"));
        event.setButton(25, IngredientContainerButton.namespacedKey(23));
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("preserve_enchants", preserveEnchants);
        gen.writeObjectField(KEY_RESULT, result);
        gen.writeObjectField(KEY_BASE, base);
        gen.writeObjectField(KEY_ADDITION, addition);
    }
}
