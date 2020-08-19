package me.wolfyscript.customcrafting.recipes.types.brewing;

import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BrewingRecipe extends CustomRecipe {

    private List<CustomItem> ingredients, allowedItems, result;
    private int fuelCost;
    private int brewTime;
    private int durationChange;
    private int amplifierChange;

    public BrewingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        {
            List<CustomItem> ingredients = new ArrayList<>();
            JsonNode resultNode = node.path("ingredients");
            if (resultNode.isObject()) {
                ingredients.add(new CustomItem(mapper.convertValue(resultNode, APIReference.class)));
                resultNode.path("variants").forEach(jsonNode -> ingredients.add(new CustomItem(mapper.convertValue(jsonNode, APIReference.class))));
            } else {
                resultNode.elements().forEachRemaining(n -> ingredients.add(new CustomItem(mapper.convertValue(n, APIReference.class))));
            }
            this.ingredients = ingredients.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
        }
        if(result == null){
            this.result = new ArrayList<>();
        }
        {
            List<CustomItem> allowedItems = new ArrayList<>();
            JsonNode resultNode = node.path("allowed_items");
            if (resultNode.isObject()) {
                allowedItems.add(new CustomItem(mapper.convertValue(resultNode, APIReference.class)));
                resultNode.path("variants").forEach(jsonNode -> allowedItems.add(new CustomItem(mapper.convertValue(jsonNode, APIReference.class))));
            } else {
                resultNode.elements().forEachRemaining(n -> allowedItems.add(new CustomItem(mapper.convertValue(n, APIReference.class))));
            }
            this.allowedItems = allowedItems.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
        }
        this.fuelCost = node.path("fuel_cost").asInt();
        this.brewTime = node.path("brew_time").asInt();
        this.durationChange = node.path("duration_change").asInt();
        this.amplifierChange = node.path("amplifier_change").asInt();
    }

    public BrewingRecipe() {
        super();
        this.ingredients = new ArrayList<>();
        this.allowedItems = new ArrayList<>();
        this.result = new ArrayList<>();
        this.fuelCost = 1;
        this.brewTime = 400;
        this.durationChange = 1;
        this.amplifierChange = 1;
    }

    public BrewingRecipe(BrewingRecipe brewingRecipe) {
        super(brewingRecipe);
        this.ingredients = brewingRecipe.getIngredients();
        this.allowedItems = brewingRecipe.getAllowedItems();
        this.result = brewingRecipe.getCustomResults();
        this.fuelCost = brewingRecipe.getFuelCost();
        this.brewTime = brewingRecipe.getBrewTime();
        this.durationChange = brewingRecipe.getDurationChange();
        this.amplifierChange = brewingRecipe.getAmplifierChange();
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.BREWING_STAND;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return new ArrayList<>(result);
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public int getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(int fuelCost) {
        this.fuelCost = fuelCost;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }

    public List<CustomItem> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setIngredients(List<CustomItem> ingredients) {
        this.ingredients = ingredients;
    }

    public List<CustomItem> getAllowedItems() {
        return new ArrayList<>(allowedItems);
    }

    public void setAllowedItems(List<CustomItem> allowedItems) {
        this.allowedItems = allowedItems;
    }

    public int getAmplifierChange() {
        return amplifierChange;
    }

    public void setAmplifierChange(int amplifierChange) {
        this.amplifierChange = amplifierChange;
    }

    public int getDurationChange() {
        return durationChange;
    }

    public void setDurationChange(int durationChange) {
        this.durationChange = durationChange;
    }

    @Override
    public BrewingRecipe clone() {
        return new BrewingRecipe(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : getCustomResults()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("ingredients");
            for (CustomItem customItem : getIngredients()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("allowed_items");
            for (CustomItem customItem : getAllowedItems()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        gen.writeNumberField("fuel_cost", fuelCost);
        gen.writeNumberField("brew_time", brewTime);
        gen.writeNumberField("duration_change", durationChange);
        gen.writeNumberField("amplifier_change", amplifierChange);
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        //TODO MENU
        event.setButton(0, "back");
        event.setButton(11, "recipe_book", "ingredient.container_11");
        event.setButton(20, "recipe_book", "brewing.icon");

        if (!InventoryUtils.isCustomItemsListEmpty(this.getAllowedItems())) {
            event.setButton(29, "recipe_book", "ingredient.container_29");
        }
        if (this.getDurationChange() > 0) {
            event.setButton(23, "recipe_book", "brewing.potion_duration");
        }
        if (this.getAmplifierChange() > 0) {
            event.setButton(25, "recipe_book", "brewing.potion_amplifier");
        }

    }
}
