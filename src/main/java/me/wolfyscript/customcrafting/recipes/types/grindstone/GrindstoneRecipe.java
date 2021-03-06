package me.wolfyscript.customcrafting.recipes.types.grindstone;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GrindstoneRecipe extends CustomRecipe<GrindstoneRecipe> {

    private List<CustomItem> inputTop, inputBottom, result;
    private int xp;

    public GrindstoneRecipe(NamespacedKey namespacedKey, JsonNode node){
        super(namespacedKey, node);
        this.xp = node.path("exp").intValue();
        {
            List<CustomItem> input = new ArrayList<>();
            node.path("input_top").elements().forEachRemaining(n -> ItemLoader.loadToList(n, input));
            this.inputTop = input.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
        }
        {
            List<CustomItem> input = new ArrayList<>();
            node.path("input_bottom").elements().forEachRemaining(n -> ItemLoader.loadToList(n, input));
            this.inputBottom = input.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
        }
    }

    public GrindstoneRecipe() {
        super();
        this.result = new ArrayList<>();
        this.inputTop = new ArrayList<>();
        this.inputBottom = new ArrayList<>();
        this.xp = 0;
    }

    public GrindstoneRecipe(GrindstoneRecipe grindstoneRecipe){
        super(grindstoneRecipe);
        this.result = grindstoneRecipe.getResults();
        this.inputBottom = grindstoneRecipe.getInputBottom();
        this.inputTop = grindstoneRecipe.getInputTop();
        this.xp = grindstoneRecipe.getXp();
    }

    @Override
    public RecipeType<GrindstoneRecipe> getRecipeType() {
        return Types.GRINDSTONE;
    }

    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>(result);
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public List<CustomItem> getInputTop() {
        return new ArrayList<>(inputTop);
    }

    public void setInputTop(List<CustomItem> inputTop) {
        this.inputTop = inputTop;
    }

    public void setInputTop(CustomItem item) {
        if (this.inputTop.size() > 0) {
            inputTop.set(0, item);
        } else {
            inputTop.add(item);
        }
    }

    public List<CustomItem> getInputBottom() {
        return new ArrayList<>(inputBottom);
    }

    public void setInputBottom(List<CustomItem> inputBottom) {
        this.inputBottom = inputBottom;
    }

    public void setInputBottom(CustomItem item) {
        if (this.inputBottom.size() > 0) {
            inputBottom.set(0, item);
        } else {
            inputBottom.add(item);
        }
    }

    public void setResult(int variant, CustomItem ingredient) {
        if (variant < result.size()) {
            result.set(variant, ingredient);
        } else {
            result.add(ingredient);
        }
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
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : result) {
                saveCustomItem(customItem, gen);
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("input_top");
            for (CustomItem customItem : getInputTop()) {
                saveCustomItem(customItem, gen);
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("input_bottom");
            for (CustomItem customItem : getInputBottom()) {
                saveCustomItem(customItem, gen);
            }
            gen.writeEndArray();
        }
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, getInputTop());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_29")).setVariants(guiHandler, getInputBottom());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, getResults());
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
