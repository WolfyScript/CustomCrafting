package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.conditions.AdvancedWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public abstract class CraftingRecipe<C extends CraftingRecipe<C, S>, S extends CraftingRecipeSettings<S>> extends CustomRecipe<C> implements ICraftingRecipe {

    protected static final String INGREDIENTS_KEY = "ingredients";

    protected List<Ingredient> ingredients;

    protected int requiredGridSize;
    protected int bookSquaredGrid;

    private final S settings;

    protected CraftingRecipe(NamespacedKey namespacedKey, JsonNode node, int gridSize, Class<S> settingsType) {
        super(namespacedKey, node);
        this.ingredients = List.of();
        this.requiredGridSize = gridSize;
        this.bookSquaredGrid = requiredGridSize * requiredGridSize;
        this.settings = Objects.requireNonNullElseGet(mapper.convertValue(node.path("settings"), new TypeReference<>() {
        }), () -> {
            try {
                return settingsType.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        });
    }

    protected CraftingRecipe(NamespacedKey key, int gridSize, S settings) {
        super(key);
        this.ingredients = List.of();
        this.requiredGridSize = gridSize;
        this.bookSquaredGrid = requiredGridSize * requiredGridSize;
        this.settings = settings;
    }

    protected CraftingRecipe(CraftingRecipe<C, S> craftingRecipe) {
        super(craftingRecipe);
        this.ingredients = craftingRecipe.ingredients != null ? craftingRecipe.ingredients.stream().map(Ingredient::clone).toList() : List.of();
        this.requiredGridSize = craftingRecipe.requiredGridSize;
        this.bookSquaredGrid = craftingRecipe.bookSquaredGrid;
        this.settings = craftingRecipe.settings.clone();
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return ingredients.get(slot);
    }

    public S getSettings() {
        return settings;
    }

    /**
     * This method returns the ingredients of the recipe. The list is unmodifiable!<br>
     * <br>
     * <b>Shaped recipe:</b><br>
     * The returned list contains the flattened ingredients, they are created from the already shrunken shape, so the list might be smaller than 9 or 36 in case of elite crafting recipes.
     * Slots that do not have an associated ingredient in the shape are filled with empty {@link Ingredient} objects.
     *
     * @return An unmodifiable list of the ingredients.
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        if (!ingredients.isEmpty()) {
            ((IngredientContainerButton) cluster.getButton("ingredient.container_" + bookSquaredGrid)).setVariants(guiHandler, this.getResult());
            for (int i = 0; i < bookSquaredGrid; i++) {
                var ingredient = ingredients.get(i);
                if (ingredient != null) {
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_" + i)).setVariants(guiHandler, ingredient);
                }
            }
        }
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        if (!ingredients.isEmpty()) {
            if (RecipeType.WORKBENCH.isInstance(this) && getConditions().has(AdvancedWorkbenchCondition.KEY)) {
                var glass = new NamespacedKey("none", "glass_purple");
                for (int i = 0; i < 9; i++) {
                    event.setButton(i, glass);
                }
                for (int i = 36; i < 54; i++) {
                    event.setButton(i, glass);
                }
            }
            List<Condition<?>> conditions = getConditions().getValues().stream().filter(condition -> !condition.getId().equals("advanced_workbench") && !condition.getId().equals("permission")).toList();
            int startSlot = 9 / (conditions.size() + 1);
            int slot = 0;
            for (Condition<?> condition : conditions) {
                event.setButton(36 + startSlot + slot, new NamespacedKey("recipe_book", "conditions." + condition.getNamespacedKey().toString("__")));
                slot += 2;
            }
            boolean elite = RecipeType.ELITE_WORKBENCH.isInstance(this);
            event.setButton(elite ? 24 : 23, new NamespacedKey("recipe_book", isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off"));
            startSlot = elite ? 0 : 10;
            for (int i = 0; i < bookSquaredGrid; i++) {
                event.setButton(startSlot + i + (i / requiredGridSize) * (9 - requiredGridSize), new NamespacedKey("recipe_book", "ingredient.container_" + i));
            }
            event.setButton(25, new NamespacedKey("recipe_book", "ingredient.container_" + bookSquaredGrid));
        }
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField(KEY_RESULT, result);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeInt(requiredGridSize);
    }

}
