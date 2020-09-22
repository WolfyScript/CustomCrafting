package me.wolfyscript.customcrafting.recipes.types.smithing;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomSmithingRecipe extends CustomRecipe<CustomSmithingRecipe> {

    private List<CustomItem> base;
    private List<CustomItem> addition;
    private List<CustomItem> result;

    public CustomSmithingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        base = Streams.stream(node.path("base").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
        addition = Streams.stream(node.path("addition").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
        result = Streams.stream(node.path("result").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
    }

    public CustomSmithingRecipe() {
        super();
        this.base = new ArrayList<>();
        this.addition = new ArrayList<>();
        this.result = new ArrayList<>();
    }

    public CustomSmithingRecipe(CustomSmithingRecipe customSmithingRecipe) {
        super(customSmithingRecipe);
        this.result = customSmithingRecipe.getResults();
        this.base = customSmithingRecipe.getBase();
        this.addition = customSmithingRecipe.getAddition();
    }

    @Override
    public RecipeType<CustomSmithingRecipe> getRecipeType() {
        return RecipeType.SMITHING;
    }

    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>(result);
    }

    public List<CustomItem> getAddition() {
        return new ArrayList<>(addition);
    }

    public void setAddition(List<CustomItem> addition) {
        this.addition = addition;
    }

    public List<CustomItem> getBase() {
        return new ArrayList<>(base);
    }

    public void setBase(List<CustomItem> base) {
        this.base = base;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    @Override
    public CustomSmithingRecipe clone() {
        return new CustomSmithingRecipe(this);
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdate event) {
        event.setButton(0, "back");
        event.setButton(10, "recipe_book", "ingredient.container_10");
        event.setButton(13, "recipe_book", "ingredient.container_13");
        event.setButton(19, "none", "glass_green");
        event.setButton(22, "none", "glass_green");
        event.setButton(28, "none", "glass_green");
        event.setButton(29, "none", "glass_green");
        event.setButton(30, "none", "glass_green");
        event.setButton(32, "none", "glass_green");
        event.setButton(33, "none", "glass_green");

        event.setButton(34, "recipe_book", "ingredient.container_34");
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : result) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("base");
            for (CustomItem customItem : base) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("addition");
            for (CustomItem customItem : addition) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
    }
}
