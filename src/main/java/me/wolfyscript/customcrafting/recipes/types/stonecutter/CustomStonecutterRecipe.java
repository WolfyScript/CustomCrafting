package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomStonecutterRecipe extends CustomRecipe<CustomStonecutterRecipe> implements ICustomVanillaRecipe<StonecuttingRecipe> {

    private List<CustomItem> result;
    private Ingredient source;

    public CustomStonecutterRecipe() {
        super();
        this.result = new ArrayList<>();
        this.source = new Ingredient();
    }

    public CustomStonecutterRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.result = new ArrayList<>();
        ItemLoader.loadToList(node.path("result"), this.result);
        this.source = ItemLoader.loadRecipeItem(node.path("source"));
    }

    public CustomStonecutterRecipe(CustomStonecutterRecipe customStonecutterRecipe) {
        super(customStonecutterRecipe);
        this.result = customStonecutterRecipe.getResults();
        this.source = customStonecutterRecipe.getSource();
    }

    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>(result);
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField("result", result.get(0).getApiReference());
        gen.writeObjectField("source", this.source);
    }

    @Override
    public RecipeType<CustomStonecutterRecipe> getRecipeType() {
        return Types.STONECUTTER;
    }

    @Override
    public CustomStonecutterRecipe clone() {
        return new CustomStonecutterRecipe(this);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((IngredientContainerButton) cluster.getButton("ingredient.container_20")).setVariants(guiHandler, getSource());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, Collections.singletonList(getResult()));
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        //TODO STONECUTTER
        NamespacedKey glass = new NamespacedKey("none", "glass_green");
        event.setButton(20, new NamespacedKey("recipe_book", "ingredient.container_20"));
        event.setButton(24, new NamespacedKey("recipe_book", "ingredient.container_24"));
        event.setButton(29, glass);
        event.setButton(30, glass);
        event.setButton(31, new NamespacedKey("recipe_book", "stonecutter"));
        event.setButton(32, glass);
        event.setButton(33, glass);

        ItemStack whiteGlass = event.getInventory().getItem(53);
        ItemMeta itemMeta = whiteGlass.getItemMeta();
        itemMeta.setCustomModelData(9007);
        whiteGlass.setItemMeta(itemMeta);
        event.setItem(53, whiteGlass);
    }

    @Override
    public StonecuttingRecipe getVanillaRecipe() {
        if (getResult() != null) {
            RecipeChoice choice = isExactMeta() ? new RecipeChoice.ExactChoice(getSource().getBukkitChoices()) : new RecipeChoice.MaterialChoice(getSource().getBukkitChoices().stream().map(ItemStack::getType).collect(Collectors.toList()));
            return new StonecuttingRecipe(namespacedKey.toBukkit(), getResult().create(), choice);
        }
        return null;
    }
}
