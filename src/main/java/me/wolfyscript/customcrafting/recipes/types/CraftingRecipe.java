package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class CraftingRecipe<C extends CraftingRecipe<?>> extends CustomRecipe<C, SlotResultTarget> implements ICraftingRecipe {

    protected static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    protected boolean shapeless;
    protected Map<Character, Ingredient> ingredients;

    protected List<Ingredient> items;

    protected int bookGridSize = 3;
    protected int bookSquaredGrid = 9;

    public CraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        //Get Ingredients
        Map<Character, Ingredient> ingredients = new TreeMap<>();
        node.path("ingredients").fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            Ingredient ingredient = ItemLoader.loadIngredient(entry.getValue());
            ingredients.put(key.charAt(0), ingredient);
        });
        this.ingredients = ingredients;
    }

    public CraftingRecipe() {
        super();
        this.result = new Result<>();
        this.ingredients = new HashMap<>();
    }

    public CraftingRecipe(CraftingRecipe<?> craftingRecipe) {
        super(craftingRecipe);
        this.result = craftingRecipe.getResult();
        this.ingredients = craftingRecipe.getIngredients();
    }

    @Override
    public Map<Character, Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public void setIngredients(Map<Character, Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public Ingredient getIngredients(char key) {
        return getIngredients().getOrDefault(key, new Ingredient());
    }

    @Override
    public Ingredient getIngredients(int slot) {
        return getIngredients(LETTERS[slot]);
    }

    @Override
    public void setIngredients(char key, Ingredient ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            this.ingredients.remove(key);
        } else {
            this.ingredients.put(key, ingredients);
        }
    }

    @Override
    public void setIngredients(int slot, Ingredient ingredients) {
        setIngredients(LETTERS[slot], ingredients);
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
            ((IngredientContainerButton) cluster.getButton("ingredient.container_" + bookSquaredGrid)).setVariants(guiHandler, this.getResult());
            for (int i = 0; i < bookSquaredGrid; i++) {
                Ingredient variants = getIngredients(i);
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
        gen.writeObjectField("result", result);
        {
            gen.writeObjectFieldStart("ingredients");
            for (Map.Entry<Character, Ingredient> entry : ingredients.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    gen.writeArrayFieldStart(entry.getKey().toString());
                    gen.writeObject(entry.getValue());
                    gen.writeEndArray();
                }
            }
            gen.writeEndObject();
        }
    }
}
