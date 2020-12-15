package me.wolfyscript.customcrafting.recipes.types.brewing;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrewingRecipe extends CustomRecipe<BrewingRecipe> {

    List<CustomItem> allowedItems; //The CustomItems that can be used. Needs to be a potion of course.
    private List<CustomItem> ingredients; //The top ingredient of the recipe. Always required.
    private int fuelCost; //The fuel cost of recipe
    private int brewTime; //The brew time in ticks

    /**
     * TODO: Link settings to CustomItems to further customize specific items.
     */

    //These options are for general changes made to the potions, if advanced features are not required or you want to edit all effects before editing them further in detail.
    private int durationChange; //added to the Duration. if <0 it will be subtracted
    private int amplifierChange; //added to the Amplifier. if <0 it will be subtracted
    private boolean resetEffects; //If true resets all the effects
    private Color effectColor; //Alternative to colorChange

    //These options are more precise and you can specify the exact effect you want to edit.
    private List<PotionEffectType> effectRemovals; //These effects will be removed from the potions
    private Map<PotionEffect, Boolean> effectAdditions; //These effects will be added with an option if they should be replaced if they are already present
    private Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades; //These effects will be added to the existing potion effects. Meaning that the the values of these PotionEffects will added to the existing effects and boolean values will be replaced.

    //Instead of all these options you can use a set result.
    private List<CustomItem> result;

    //Conditions for the Potions inside the 3 slots at the bottom
    private Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects; //The effects that are required with the current Duration and amplitude. Integer values == 0 will be ignored and any value will be allowed.


    public BrewingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        ingredients = Streams.stream(node.path("ingredients").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
        this.fuelCost = node.path("fuel_cost").asInt(1);
        this.brewTime = node.path("brew_time").asInt(80);
        allowedItems = Streams.stream(node.path("allowed_items").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());

        setDurationChange(node.path("duration_change").asInt());
        setAmplifierChange(node.path("amplifier_change").asInt());
        setResetEffects(node.path("reset_effects").asBoolean(false));
        setEffectColor(node.has("color") ? mapper.convertValue(node.path("color"), Color.class) : null);

        setEffectRemovals(Streams.stream(node.path("effect_removals").elements()).map(n -> mapper.convertValue(n, PotionEffectType.class)).collect(Collectors.toList()));
        Map<PotionEffect, Boolean> effectAdditions = new HashMap<>();
        node.path("effect_additions").elements().forEachRemaining(n -> {
            PotionEffect potionEffect = mapper.convertValue(n.path("effect"), PotionEffect.class);
            if (potionEffect != null) {
                effectAdditions.put(potionEffect, n.path("replace").asBoolean());
            }
        });
        setEffectAdditions(effectAdditions);

        Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades = new HashMap<>();
        node.path("effect_upgrades").elements().forEachRemaining(n -> {
            PotionEffectType potionEffect = mapper.convertValue(n.path("effect_type"), PotionEffectType.class);
            if (potionEffect != null) {
                effectUpgrades.put(potionEffect, new Pair<>(n.get("amplifier").asInt(), n.path("duration").asInt()));
            }
        });
        setEffectUpgrades(effectUpgrades);

        this.result = Streams.stream(node.path("results").elements()).map(n -> mapper.convertValue(n, CustomItem.class)).collect(Collectors.toList());

        Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects = new HashMap<>();
        node.path("required_effects").elements().forEachRemaining(n -> {
            PotionEffectType potionEffect = mapper.convertValue(n.path("type"), PotionEffectType.class);
            if (potionEffect != null) {
                requiredEffects.put(potionEffect, new Pair<>(n.get("amplifier").asInt(), n.path("duration").asInt()));
            }
        });
        setRequiredEffects(requiredEffects);
    }

    public BrewingRecipe() {
        super();
        this.ingredients = new ArrayList<>();
        this.fuelCost = 1;
        this.brewTime = 400;
        this.allowedItems = new ArrayList<>();

        this.durationChange = 0;
        this.amplifierChange = 0;
        this.resetEffects = false;
        this.effectColor = null;
        this.effectRemovals = new ArrayList<>();
        this.effectAdditions = new HashMap<>();
        this.effectUpgrades = new HashMap<>();
        this.result = new ArrayList<>();
        this.requiredEffects = new HashMap<>();
    }

    public BrewingRecipe(BrewingRecipe brewingRecipe) {
        super(brewingRecipe);
        this.ingredients = brewingRecipe.getIngredients();
        this.fuelCost = brewingRecipe.getFuelCost();
        this.brewTime = brewingRecipe.getBrewTime();

        this.allowedItems = brewingRecipe.getAllowedItems();

        this.durationChange = brewingRecipe.getDurationChange();
        this.amplifierChange = brewingRecipe.getAmplifierChange();
        this.resetEffects = brewingRecipe.isResetEffects();
        this.effectColor = brewingRecipe.getEffectColor();
        this.effectRemovals = brewingRecipe.getEffectRemovals();
        this.effectAdditions = brewingRecipe.getEffectAdditions();
        this.effectUpgrades = brewingRecipe.getEffectUpgrades();
        this.result = brewingRecipe.getResults();
        this.requiredEffects = brewingRecipe.getRequiredEffects();
    }

    @Override
    public RecipeType<BrewingRecipe> getRecipeType() {
        return RecipeType.BREWING_STAND;
    }

    @Override
    public List<CustomItem> getResults() {
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

    public int getDurationChange() {
        return durationChange;
    }

    public void setDurationChange(int durationChange) {
        this.durationChange = durationChange;
    }

    public int getAmplifierChange() {
        return amplifierChange;
    }

    public void setAmplifierChange(int amplifierChange) {
        this.amplifierChange = amplifierChange;
    }

    public boolean isResetEffects() {
        return resetEffects;
    }

    public void setResetEffects(boolean resetEffects) {
        this.resetEffects = resetEffects;
    }

    public Color getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
    }

    public List<PotionEffectType> getEffectRemovals() {
        return effectRemovals;
    }

    public void setEffectRemovals(List<PotionEffectType> effectRemovals) {
        this.effectRemovals = effectRemovals;
    }

    public Map<PotionEffect, Boolean> getEffectAdditions() {
        return effectAdditions;
    }

    public void setEffectAdditions(Map<PotionEffect, Boolean> effectAdditions) {
        this.effectAdditions = effectAdditions;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getEffectUpgrades() {
        return effectUpgrades;
    }

    public void setEffectUpgrades(Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades) {
        this.effectUpgrades = effectUpgrades;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getRequiredEffects() {
        return requiredEffects;
    }

    public void setRequiredEffects(Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects) {
        this.requiredEffects = requiredEffects;
    }

    @Override
    public BrewingRecipe clone() {
        return new BrewingRecipe(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);

        gen.writeArrayFieldStart("ingredients");
        for (CustomItem customItem : getIngredients()) {
            gen.writeObject(customItem.getApiReference());
        }
        gen.writeEndArray();

        gen.writeNumberField("fuel_cost", fuelCost);
        gen.writeNumberField("brew_time", brewTime);

        gen.writeArrayFieldStart("allowed_items");
        for (CustomItem customItem : getAllowedItems()) {
            gen.writeObject(customItem.getApiReference());
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("results");
        for (CustomItem customItem : getResults()) {
            gen.writeObject(customItem.getApiReference());
        }
        gen.writeEndArray();

        //Load options
        gen.writeNumberField("duration_change", durationChange);
        gen.writeNumberField("amplifier_change", amplifierChange);
        gen.writeBooleanField("reset_effects", resetEffects);
        gen.writeObjectField("color", effectColor);

        //Load advanced options
        gen.writeArrayFieldStart("effect_removals");
        for (PotionEffectType effectRemoval : effectRemovals) {
            gen.writeObject(effectRemoval);
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("effect_additions");
        for (Map.Entry<PotionEffect, Boolean> entry : effectAdditions.entrySet()) {
            if (entry.getKey() != null) {
                gen.writeStartObject();
                gen.writeObjectField("effect", entry.getKey());
                gen.writeBooleanField("replace", entry.getValue());
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("effect_upgrades");
        for (Map.Entry<PotionEffectType, Pair<Integer, Integer>> entry : effectUpgrades.entrySet()) {
            if (entry.getKey() != null) {
                gen.writeStartObject();
                gen.writeObjectField("effect_type", entry.getKey());
                gen.writeNumberField("amplifier", entry.getValue().getKey());
                gen.writeNumberField("duration", entry.getValue().getValue());
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();

        //Load input condition options
        gen.writeArrayFieldStart("required_effects");
        for (Map.Entry<PotionEffectType, Pair<Integer, Integer>> entry : requiredEffects.entrySet()) {
            if (entry.getKey() != null) {
                gen.writeStartObject();
                gen.writeObjectField("effect_type", entry.getKey());
                gen.writeNumberField("amplifier", entry.getValue().getKey());
                gen.writeNumberField("duration", entry.getValue().getValue());
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();
    }

    @Override
    public void prepareMenu(GuiHandler<TestCache> guiHandler, GuiCluster<TestCache> cluster) {
        ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, getResults());
        if (!getAllowedItems().isEmpty()) {
            ((IngredientContainerButton) cluster.getButton("ingredient.container_29")).setVariants(guiHandler, getAllowedItems());
        }
    }

    @Override
    public void renderMenu(GuiWindow<TestCache> guiWindow, GuiUpdate<TestCache> event) {
        //TODO MENU
        event.setButton(0, "back");
        event.setButton(11, "recipe_book", "ingredient.container_11");
        event.setButton(20, "recipe_book", "brewing.icon");

        if (!InventoryUtils.isCustomItemsListEmpty(this.getAllowedItems())) {
            event.setButton(29, "recipe_book", "ingredient.container_29");
        }


    }
}
