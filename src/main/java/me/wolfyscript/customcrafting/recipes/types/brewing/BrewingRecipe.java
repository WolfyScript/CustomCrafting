package me.wolfyscript.customcrafting.recipes.types.brewing;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrewingRecipe extends CustomRecipe<BrewingRecipe> {

    private final BrewResultOptions globalOptions;
    List<CustomItem> allowedItems; //The CustomItems that can be used. Needs to be a potion of course.
    private List<CustomItem> ingredients; //The top ingredient of the recipe. Always required.
    private int fuelCost; //The fuel cost of recipe
    private int brewTime; //The brew time in ticks
    private Map<CustomItem, BrewResultOptions> resultOptions;

    /**
     * TODO: Link settings to CustomItems to further customize specific items.
     */

    public BrewingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        ingredients = Streams.stream(node.path("ingredients").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
        this.fuelCost = node.path("fuel_cost").asInt(1);
        this.brewTime = node.path("brew_time").asInt(80);

        this.globalOptions = mapper.convertValue(node.path("globalOptions"), BrewResultOptions.class);

        allowedItems = Streams.stream(node.path("allowed_items").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());

    }

    public BrewingRecipe() {
        super();
        this.ingredients = new ArrayList<>();
        this.fuelCost = 1;
        this.brewTime = 400;

        this.globalOptions = new BrewResultOptions();
        this.resultOptions = new HashMap<>();

        this.allowedItems = new ArrayList<>();
    }

    public BrewingRecipe(BrewingRecipe brewingRecipe) {
        super(brewingRecipe);
        this.ingredients = brewingRecipe.getIngredients();
        this.fuelCost = brewingRecipe.getFuelCost();
        this.brewTime = brewingRecipe.getBrewTime();

        this.globalOptions = brewingRecipe.getGlobalOptions();
        this.resultOptions = brewingRecipe.getResultOptions();

        this.allowedItems = brewingRecipe.getAllowedItems();
    }

    @Override
    public RecipeType<BrewingRecipe> getRecipeType() {
        return RecipeType.BREWING_STAND;
    }

    /**
     * This recipe has no result
     *
     * @return
     */
    @Deprecated
    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>();
    }

    @Deprecated
    @Override
    public void setResult(List<CustomItem> result) {
        //No Result available!
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

    public BrewResultOptions getGlobalOptions() {
        return globalOptions;
    }

    public Map<CustomItem, BrewResultOptions> getResultOptions() {
        return resultOptions;
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
            for (CustomItem customItem : this.getResults()) {
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
        gen.writeNumberField("fuel_cost", fuelCost);
        gen.writeNumberField("brew_time", brewTime);


        {
            gen.writeArrayFieldStart("allowed_items");
            for (CustomItem customItem : getAllowedItems()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdate event) {
        //TODO MENU
        event.setButton(0, "back");
        event.setButton(11, "recipe_book", "ingredient.container_11");
        event.setButton(20, "recipe_book", "brewing.icon");

        if (!InventoryUtils.isCustomItemsListEmpty(this.getAllowedItems())) {
            event.setButton(29, "recipe_book", "ingredient.container_29");
        }


    }
}
