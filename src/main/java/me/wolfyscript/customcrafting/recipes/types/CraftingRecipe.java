package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class CraftingRecipe<C extends CraftingRecipe<?>> extends CustomRecipe<C> implements ICraftingRecipe {

    protected static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    protected boolean shapeless;

    protected List<CustomItem> result;
    protected Map<Character, List<CustomItem>> ingredients;

    protected int bookGridSize = 3;
    protected int bookSquaredGrid = 9;

    public CraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        //Get Ingredients
        {
            Map<Character, List<CustomItem>> ingredients = new TreeMap<>();
            JsonNode ingredientsNode = node.path("ingredients");
            ingredientsNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                List<CustomItem> data = new ArrayList<>();
                entry.getValue().elements().forEachRemaining(item -> data.add(CustomItem.of(mapper.convertValue(item, APIReference.class))));
                ingredients.put(key.charAt(0), data.stream().filter(item -> !ItemUtils.isAirOrNull(item)).collect(Collectors.toList()));
            });
            this.ingredients = ingredients;
        }
    }

    public CraftingRecipe(){
        super();
        this.result = new ArrayList<>();
        this.ingredients = new HashMap<>();
    }

    public CraftingRecipe(CraftingRecipe<?> craftingRecipe){
        super(craftingRecipe);
        this.result = craftingRecipe.getResults();
        this.ingredients = craftingRecipe.getIngredients();
    }

    @Override
    public Map<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    @Override
    public List<CustomItem> getIngredients(char key) {
        return new ArrayList<>(getIngredients().getOrDefault(key, new ArrayList<>()));
    }

    @Override
    public List<CustomItem> getIngredients(int slot) {
        return getIngredients(LETTERS[slot]);
    }

    @Override
    public void setIngredients(char key, List<CustomItem> ingredients) {
        this.ingredients.put(key, ingredients);
    }

    @Override
    public void setIngredients(int slot, List<CustomItem> ingredients) {
        this.ingredients.put(LETTERS[slot], ingredients);
    }

    @Override
    public CustomItem getIngredient(int slot) {
        return getIngredient(LETTERS[slot]);
    }

    @Override
    public CustomItem getIngredient(char key) {
        List<CustomItem> list = getIngredients(key);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public void setIngredient(int slot, int variant, CustomItem customItem) {
        setIngredient(LETTERS[slot], variant, customItem);
    }

    @Override
    public void setIngredient(char key, int variant, CustomItem customItem) {
        List<CustomItem> ingredient = getIngredients(key);
        if (variant < ingredient.size()) {
            if (ItemUtils.isAirOrNull(customItem)) {
                ingredient.remove(variant);
            } else {
                ingredient.set(variant, customItem);
            }
        } else if (!ItemUtils.isAirOrNull(customItem)) {
            ingredient.add(customItem);
        }
        ingredients.put(key, ingredient);
    }

    public void setIngredients(Map<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public void setResult(int variant, CustomItem customItem) {
        if (variant < result.size()) {
            if (ItemUtils.isAirOrNull(customItem)) {
                result.remove(variant);
            } else {
                result.set(variant, customItem);
            }
        } else if (!ItemUtils.isAirOrNull(customItem)) {
            result.add(customItem);
        }
    }

    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>(result);
    }

    @Override
    public boolean isShapeless() {
        return shapeless;
    }

    @Override
    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        if (!getIngredients().isEmpty()) {
            ((IngredientContainerButton) cluster.getButton("ingredient.container_" + bookSquaredGrid)).setVariants(guiHandler, getResults());
            for (int i = 0; i < bookSquaredGrid; i++) {
                List<CustomItem> variants = getIngredients(i);
                ((IngredientContainerButton) cluster.getButton("ingredient.container_" + i)).setVariants(guiHandler, variants);
            }
        }
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        if (!getIngredients().isEmpty()) {
            if (this instanceof AdvancedCraftingRecipe && getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT)) {
                NamespacedKey glass = new NamespacedKey("none", "glass_purple");
                for (int i = 0; i < 9; i++) {
                    event.setButton(i, glass);
                }
                for (int i = 36; i < 54; i++) {
                    event.setButton(i, glass);
                }
            }
            if (getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT)) {
                //TODO display for admins
            }
            List<Condition> conditions = getConditions().values().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("advanced_workbench") && !condition.getId().equals("permission")).collect(Collectors.toList());
            int startSlot = 9 / (conditions.size() + 1);
            int slot = 0;
            for (Condition condition : conditions) {
                if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                    event.setButton(36 + startSlot + slot, new NamespacedKey("recipe_book", "conditions." + condition.getId()));
                    slot += 2;
                }
            }
            event.setButton(this instanceof EliteCraftingRecipe ? 24 : 23, new NamespacedKey("recipe_book", isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off"));
            startSlot = this instanceof EliteCraftingRecipe ? 0 : 10;
            for (int i = 0; i < bookSquaredGrid; i++) {
                event.setButton(startSlot + i + (i / bookGridSize) * (9 - bookGridSize), new NamespacedKey("recipe_book", "ingredient.container_" + i));
            }
            event.setButton(25, new NamespacedKey("recipe_book", "ingredient.container_" + bookSquaredGrid));
        }
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("shapeless", shapeless);
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : getResults()) {
                saveCustomItem(customItem, gen);
            }
            gen.writeEndArray();
        }
        {
            gen.writeObjectFieldStart("ingredients");
            for (Map.Entry<Character, List<CustomItem>> entry : ingredients.entrySet()) {
                List<CustomItem> ingred = entry.getValue();
                if(!InventoryUtils.isCustomItemsListEmpty(ingred)){
                    gen.writeArrayFieldStart(entry.getKey().toString());
                    for (CustomItem customItem : ingred) {
                        saveCustomItem(customItem, gen);
                    }
                    gen.writeEndArray();
                }
            }
            gen.writeEndObject();
        }
    }
}
