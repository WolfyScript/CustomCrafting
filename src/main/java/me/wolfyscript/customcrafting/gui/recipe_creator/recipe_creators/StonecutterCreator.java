package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.StonecutterContainerButton;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;

public class StonecutterCreator extends RecipeCreator {

    public StonecutterCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "stonecutter", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new StonecutterContainerButton(0, customCrafting));
        registerButton(new StonecutterContainerButton(1, customCrafting));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        ((ToggleButton<CCCache>) getButton("hidden")).setState(update.getGuiHandler(), update.getGuiHandler().getCustomCache().getStonecutterRecipe().isHidden());
        update.setButton(0, "back");
        update.setButton(4, "hidden");
        update.setButton(20, "stonecutter.container_0");
        update.setButton(24, "stonecutter.container_1");

        if (update.getGuiHandler().getCustomCache().getStonecutterRecipe().hasNamespacedKey()) {
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(CCCache cache) {
        CustomStonecutterRecipe recipe = cache.getStonecutterRecipe();
        return !InventoryUtils.isCustomItemsListEmpty(recipe.getResults()) && !InventoryUtils.isCustomItemsListEmpty(recipe.getSource());
    }
}
