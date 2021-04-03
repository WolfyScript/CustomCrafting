package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;

public class SmithingCreator extends RecipeCreator {

    public SmithingCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "smithing", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));
        registerButton(new ButtonRecipeResult());
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
        ((ToggleButton<CCCache>) getCluster().getButton("exact_meta")).setState(event.getGuiHandler(), smithingRecipe.isExactMeta());
        ((ToggleButton<CCCache>) getCluster().getButton("hidden")).setState(event.getGuiHandler(), smithingRecipe.isHidden());
        event.setButton(1, RecipeCreatorCluster.HIDDEN);
        event.setButton(3, RecipeCreatorCluster.CONDITIONS);
        event.setButton(5, RecipeCreatorCluster.PRIORITY);
        event.setButton(7, RecipeCreatorCluster.EXACT_META);
        event.setButton(19, "recipe.ingredient_0");
        event.setButton(22, "recipe.ingredient_1");
        event.setButton(25, "recipe.result");

        if (smithingRecipe.hasNamespacedKey()) {
            event.setButton(43, RecipeCreatorCluster.SAVE);
        }
        event.setButton(44, RecipeCreatorCluster.SAVE_AS);
    }

    @Override
    public boolean validToSave(CCCache cache) {
        CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
        return !smithingRecipe.getBase().isEmpty() && !smithingRecipe.getAddition().isEmpty() && !smithingRecipe.getResult().isEmpty();
    }
}
