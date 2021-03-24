package me.wolfyscript.customcrafting.recipes.types.grindstone;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;

public class GrindstoneRecipe extends CustomRecipe<GrindstoneRecipe, SlotResultTarget> {

    private Ingredient inputTop, inputBottom;
    private int xp;

    public GrindstoneRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.xp = node.path("exp").intValue();
        this.inputTop = ItemLoader.loadIngredient(node.path("input_top"));
        this.inputBottom = ItemLoader.loadIngredient(node.path("input_bottom"));
    }

    public GrindstoneRecipe() {
        super();
        this.result = new Result<>();
        this.inputTop = new Ingredient();
        this.inputBottom = new Ingredient();
        this.xp = 0;
    }

    public GrindstoneRecipe(GrindstoneRecipe grindstoneRecipe) {
        super(grindstoneRecipe);
        this.result = grindstoneRecipe.getResult();
        this.inputBottom = grindstoneRecipe.getInputBottom();
        this.inputTop = grindstoneRecipe.getInputTop();
        this.xp = grindstoneRecipe.getXp();
    }

    @Override
    public RecipeType<GrindstoneRecipe> getRecipeType() {
        return Types.GRINDSTONE;
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
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    @Override
    public GrindstoneRecipe clone() {
        return new GrindstoneRecipe(this);
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
        NamespacedKey glass = new NamespacedKey("none", "glass_green");
        event.setButton(11, new NamespacedKey("recipe_book", "ingredient.container_11"));
        event.setButton(12, glass);
        event.setButton(21, glass);
        event.setButton(22, new NamespacedKey("recipe_book", "grindstone"));
        event.setButton(23, glass);
        event.setButton(24, new NamespacedKey("recipe_book", "ingredient.container_24"));
        event.setButton(29, new NamespacedKey("recipe_book", "ingredient.container_29"));
        event.setButton(30, glass);

        ItemStack whiteGlass = event.getInventory().getItem(53);
        ItemMeta itemMeta = whiteGlass.getItemMeta();
        itemMeta.setCustomModelData(9008);
        whiteGlass.setItemMeta(itemMeta);
        event.setItem(53, whiteGlass);
    }
}
