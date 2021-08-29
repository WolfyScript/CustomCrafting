package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public class RecipeCreatorCraftingEliteSettings extends RecipeCreator {

    public static final String KEY = "elite_crafting_settings";

    public RecipeCreatorCraftingEliteSettings(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "elite_crafting_settings", 27, customCrafting);
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, ClusterMain.BACK);
        update.setButton(9, ClusterRecipeCreator.HIDDEN);
        update.setButton(11, ClusterRecipeCreator.GROUP);
        update.setButton(13, ClusterRecipeCreator.CONDITIONS);
        update.setButton(15, ClusterRecipeCreator.EXACT_META);
        update.setButton(17, ClusterRecipeCreator.PRIORITY);
    }

}
