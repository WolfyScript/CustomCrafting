package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;

public class StonecutterCreator extends RecipeCreator {

    public StonecutterCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "stonecutter", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeResult());
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        ((ToggleButton<CCCache>) getButton("hidden")).setState(update.getGuiHandler(), update.getGuiHandler().getCustomCache().getStonecutterRecipe().isHidden());
        update.setButton(0, BACK);
        update.setButton(4, "hidden");
        update.setButton(20, "recipe.ingredient_0");
        update.setButton(24, "stonecutter.result");

        if (update.getGuiHandler().getCustomCache().getStonecutterRecipe().hasNamespacedKey()) {
            update.setButton(43, RecipeCreatorCluster.SAVE);
        }
        update.setButton(44, RecipeCreatorCluster.SAVE_AS);
    }

    @Override
    public boolean validToSave(CCCache cache) {
        CustomStonecutterRecipe recipe = cache.getStonecutterRecipe();
        return !recipe.getResult().isEmpty() && !recipe.getSource().isEmpty();
    }
}
