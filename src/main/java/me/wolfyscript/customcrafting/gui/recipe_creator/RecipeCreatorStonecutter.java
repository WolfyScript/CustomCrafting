package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public class RecipeCreatorStonecutter extends RecipeCreator {

    public RecipeCreatorStonecutter(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
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
        update.setButton(4, ClusterRecipeCreator.HIDDEN);
        update.setButton(20, "recipe.ingredient_0");
        update.setButton(24, "recipe.result");

        update.setButton(42, ClusterRecipeCreator.GROUP);
        if (update.getGuiHandler().getCustomCache().getRecipeCreatorCache().getStonecuttingCache().isSaved()) {
            update.setButton(43, ClusterRecipeCreator.SAVE);
        }
        update.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
