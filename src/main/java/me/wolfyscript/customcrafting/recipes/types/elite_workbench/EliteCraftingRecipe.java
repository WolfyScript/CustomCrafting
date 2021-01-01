package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class EliteCraftingRecipe extends CraftingRecipe<EliteCraftingRecipe> {

    protected int requiredGridSize;

    public EliteCraftingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public EliteCraftingRecipe(){
        super();
    }

    public EliteCraftingRecipe(EliteCraftingRecipe eliteCraftingRecipe){
        super(eliteCraftingRecipe);
    }

    public int getRequiredGridSize() {
        return requiredGridSize;
    }

    @Override
    public RecipeType<EliteCraftingRecipe> getRecipeType() {
        return RecipeType.ELITE_WORKBENCH;
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        event.setButton(6, "back");
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        if (!getIngredients().isEmpty()) {
            event.setButton(24, new NamespacedKey("recipe_book", isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off"));
            if (getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT)) {

            }
            List<Condition> conditions = getConditions().values().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("permission")).collect(Collectors.toList());
            int startSlot = 9 / (conditions.size() + 1);
            int slot = 0;
            for (Condition condition : conditions) {
                if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                    event.setButton(36 + startSlot + slot, new NamespacedKey("recipe_book", "conditions." + condition.getId()));
                    slot += 2;
                }
            }
            startSlot = 0;
            int gridSize = 6;
            int invSlot;
            for (int i = 0; i < gridSize * gridSize; i++) {
                invSlot = startSlot + i + (i / gridSize) * 3;
                event.setButton(invSlot, new NamespacedKey("recipe_book", "ingredient.container_" + invSlot));
            }
            event.setButton(25, new NamespacedKey("recipe_book", "ingredient.container_25"));
        }
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
    }
}
