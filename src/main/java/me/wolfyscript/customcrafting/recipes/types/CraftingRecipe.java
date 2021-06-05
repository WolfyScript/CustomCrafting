package me.wolfyscript.customcrafting.recipes.types;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.target.SlotResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CraftingRecipe<C extends CraftingRecipe<C>> extends CustomRecipe<C, SlotResultTarget> implements ICraftingRecipe {

    protected static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    protected boolean shapeless;
    private Map<Character, Ingredient> ingredients;

    protected int bookGridSize = 3;
    protected int bookSquaredGrid = 9;

    protected CraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        setIngredients(Streams.stream(node.path("ingredients").fields()).collect(Collectors.toMap(entry -> entry.getKey().charAt(0), entry -> ItemLoader.loadIngredient(entry.getValue()))));
    }

    protected CraftingRecipe() {
        super();
        this.ingredients = new HashMap<>();
    }

    protected CraftingRecipe(CraftingRecipe<C> craftingRecipe) {
        super(craftingRecipe);
        this.bookGridSize = craftingRecipe.bookGridSize;
        this.bookSquaredGrid = craftingRecipe.bookSquaredGrid;
        this.shapeless = craftingRecipe.shapeless;
        this.ingredients = craftingRecipe.ingredients;
    }

    @Override
    public Map<Character, Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return getIngredients(LETTERS.charAt(slot));
    }

    @Override
    public void setIngredients(Map<Character, Ingredient> ingredients) {
        this.ingredients = ingredients.entrySet().stream().filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, o2) -> o));
    }

    @Override
    public void setIngredient(char key, Ingredient ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            this.ingredients.remove(key);
        } else {
            ingredients.buildChoices();
            this.ingredients.put(key, ingredients);
        }
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredients) {
        setIngredient(LETTERS.charAt(slot), ingredients);
    }

    @Override
    public boolean isShapeless() {
        return shapeless;
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        if (!getIngredients().isEmpty()) {
            ((IngredientContainerButton) cluster.getButton("ingredient.container_" + bookSquaredGrid)).setVariants(guiHandler, this.getResult());
            for (int i = 0; i < bookSquaredGrid; i++) {
                Ingredient ingredient = getIngredient(i);
                if (ingredient != null) {
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_" + i)).setVariants(guiHandler, ingredient);
                }
            }
        }
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
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
        gen.writeObjectField("ingredients", ingredients);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeBoolean(shapeless);
        byteBuf.writeInt(bookGridSize);
        byteBuf.writeVarInt(ingredients.size());
        ingredients.forEach((key, ingredient) -> {
            byteBuf.writeInt(LETTERS.indexOf(key));
            byteBuf.writeVarInt(ingredient.size());
            for (CustomItem choice : ingredient.getChoices()) {
                byteBuf.writeItemStack(choice.create());
            }
        });

    }
}
