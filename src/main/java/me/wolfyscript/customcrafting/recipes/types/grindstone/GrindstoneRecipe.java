package me.wolfyscript.customcrafting.recipes.types.grindstone;

import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GrindstoneRecipe extends CustomRecipe {

    private List<CustomItem> inputTop, inputBottom, result;
    private float xp;

    public GrindstoneRecipe(NamespacedKey namespacedKey, JsonNode node){
        super(namespacedKey, node);
        this.xp = node.path("exp").floatValue();
        {
            List<CustomItem> input = new ArrayList<>();
            JsonNode resultNode = node.path("input_top");
            if (resultNode.isObject()) {
                input.add(new CustomItem(mapper.convertValue(resultNode, APIReference.class)));
                JsonNode variantsNode = resultNode.path("variants");
                for (JsonNode jsonNode : variantsNode) {
                    input.add(new CustomItem(mapper.convertValue(jsonNode, APIReference.class)));
                }
            } else {
                resultNode.elements().forEachRemaining(n -> input.add(new CustomItem(mapper.convertValue(n, APIReference.class))));
            }
            this.inputTop = input.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
        }
        {
            List<CustomItem> input = new ArrayList<>();
            JsonNode resultNode = node.path("input_bottom");
            if (resultNode.isObject()) {
                input.add(new CustomItem(mapper.convertValue(resultNode, APIReference.class)));
                JsonNode variantsNode = resultNode.path("variants");
                for (JsonNode jsonNode : variantsNode) {
                    input.add(new CustomItem(mapper.convertValue(jsonNode, APIReference.class)));
                }
            } else {
                resultNode.elements().forEachRemaining(n -> input.add(new CustomItem(mapper.convertValue(n, APIReference.class))));
            }
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
        this.result = grindstoneRecipe.getCustomResults();
        this.inputBottom = grindstoneRecipe.getInputBottom();
        this.inputTop = grindstoneRecipe.getInputTop();
        this.xp = grindstoneRecipe.getXp();
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.GRINDSTONE;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public List<CustomItem> getInputTop() {
        return inputTop;
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
        return inputBottom;
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

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeNumberField("exp", xp);
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : result) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("input_top");
            for (CustomItem customItem : getInputTop()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("input_bottom");
            for (CustomItem customItem : getInputBottom()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        event.setButton(0, "back");
        event.setButton(11, "recipe_book", "ingredient.container_11");
        event.setButton(12, "none", "glass_green");
        event.setButton(21, "none", "glass_green");
        event.setButton(22, "recipe_book", "grindstone");
        event.setButton(23, "none", "glass_green");
        event.setButton(24, "recipe_book", "ingredient.container_24");
        event.setButton(29, "recipe_book", "ingredient.container_29");
        event.setButton(30, "none", "glass_green");

        ItemStack whiteGlass = event.getInventory().getItem(53);
        ItemMeta itemMeta = whiteGlass.getItemMeta();
        itemMeta.setCustomModelData(9008);
        whiteGlass.setItemMeta(itemMeta);
        event.setItem(53, whiteGlass);
    }
}
