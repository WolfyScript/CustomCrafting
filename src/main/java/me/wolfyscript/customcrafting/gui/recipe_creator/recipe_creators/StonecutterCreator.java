package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

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
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        update.setButton(4, RecipeCreatorCluster.HIDDEN);
        update.setButton(20, "recipe.ingredient_0");
        update.setButton(24, "recipe.result");

        update.setButton(42, RecipeCreatorCluster.GROUP);
        if (update.getGuiHandler().getCustomCache().getStonecutterRecipe().hasNamespacedKey()) {
            update.setButton(43, RecipeCreatorCluster.SAVE);
        }
        update.setButton(44, RecipeCreatorCluster.SAVE_AS);
    }

    @Override
    public boolean validToSave(CCCache cache) {
        var recipe = cache.getStonecutterRecipe();
        return !recipe.getResult().isEmpty() && !recipe.getSource().isEmpty();
    }
}
