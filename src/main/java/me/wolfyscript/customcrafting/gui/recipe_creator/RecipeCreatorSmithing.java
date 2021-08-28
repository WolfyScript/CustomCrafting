package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public class RecipeCreatorSmithing extends RecipeCreator {

    public RecipeCreatorSmithing(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
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
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        var smithingRecipe = cache.getRecipeCreatorCache().getSmithingCache();
        event.setButton(1, ClusterRecipeCreator.HIDDEN);
        event.setButton(3, ClusterRecipeCreator.CONDITIONS);
        event.setButton(5, ClusterRecipeCreator.PRIORITY);
        event.setButton(7, ClusterRecipeCreator.EXACT_META);
        event.setButton(19, "recipe.ingredient_0");
        event.setButton(22, "recipe.ingredient_1");
        event.setButton(25, "recipe.result");

        event.setButton(42, ClusterRecipeCreator.GROUP);
        if (smithingRecipe.isSaved()) {
            event.setButton(43, ClusterRecipeCreator.SAVE);
        }
        event.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
