package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonContainerItemIngredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

public class IngredientMenu extends CCWindow {

    public IngredientMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "ingredient", 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 36; i++) {
            registerButton(new ButtonContainerItemIngredient(i));
        }
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            if (cache.getIngredientData().isNew()) {
                setIngredient(cache, cache.getIngredientData().getIngredient(), cache.getIngredientData().getSlot());
            }
            guiHandler.openPreviousWindow();
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "item_container_" + i);
        }
        update.setButton(49, "recipe_creator", "tags");
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> update) {



    }

    public static void setIngredient(CCCache cache, Ingredient ingredient, int recipeSlot) {
        switch (cache.getRecipeType().getType()) {
            case WORKBENCH:
            case ELITE_WORKBENCH:
                cache.getCraftingRecipe().setIngredients(recipeSlot, ingredient);
                break;
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
            case CAMPFIRE:
                cache.getCookingRecipe().setSource(ingredient);
                break;
            case SMITHING:
                if (recipeSlot == 0) {
                    cache.getSmithingRecipe().setBase(ingredient);
                } else {
                    cache.getSmithingRecipe().setAddition(ingredient);
                }
                break;
            case GRINDSTONE:
                if (recipeSlot == 0) {
                    cache.getGrindstoneRecipe().setInputTop(ingredient);
                } else {
                    cache.getGrindstoneRecipe().setInputBottom(ingredient);
                }
                break;
            case STONECUTTER:
                cache.getStonecutterRecipe().setSource(ingredient);
                break;
            case ANVIL:
                cache.getAnvilRecipe().setInput(recipeSlot, ingredient);
                break;
            case CAULDRON:
                cache.getCauldronRecipe().setIngredients(ingredient);
                break;
            case BREWING_STAND:
                cache.getBrewingRecipe().setIngredients(ingredient);
        }
    }

    public static Ingredient getIngredient(CCCache cache, int recipeSlot) {
        Ingredient ingredient = null;
        switch (cache.getRecipeType().getType()) {
            case ELITE_WORKBENCH:
            case WORKBENCH:
                ingredient = cache.getCraftingRecipe().getIngredients(recipeSlot);
                break;
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
            case CAMPFIRE:
                ingredient = cache.getCookingRecipe().getSource();
                break;
            case SMITHING:
                ingredient = recipeSlot == 0 ? cache.getSmithingRecipe().getBase() : cache.getSmithingRecipe().getAddition();
                break;
            case GRINDSTONE:
                ingredient = recipeSlot == 0 ? cache.getGrindstoneRecipe().getInputTop() : cache.getGrindstoneRecipe().getInputBottom();
                break;
            case STONECUTTER:
                ingredient = cache.getStonecutterRecipe().getSource();
                break;
            case ANVIL:
                ingredient = cache.getAnvilRecipe().getInput(recipeSlot);
                break;
            case CAULDRON:
                ingredient = cache.getCauldronRecipe().getIngredients();
                break;
            case BREWING_STAND:
                ingredient = recipeSlot == 0 ? cache.getBrewingRecipe().getIngredients() : cache.getBrewingRecipe().getAllowedItems();
        }
        return ingredient;
    }
}
