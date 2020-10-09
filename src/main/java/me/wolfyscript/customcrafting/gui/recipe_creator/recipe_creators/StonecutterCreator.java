package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.StonecutterContainerButton;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;

public class StonecutterCreator extends RecipeCreator {

    public StonecutterCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("stonecutter", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new StonecutterContainerButton(0, customCrafting));
        registerButton(new StonecutterContainerButton(1, customCrafting));
    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        super.onUpdateAsync(update);
        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), ((TestCache) update.getGuiHandler().getCustomCache()).getStonecutterRecipe().isHidden());
        update.setButton(0, "back");
        update.setButton(4, "hidden");
        update.setButton(20, "stonecutter.container_0");
        update.setButton(24, "stonecutter.container_1");

        if (((TestCache) update.getGuiHandler().getCustomCache()).getStonecutterRecipe().hasNamespacedKey()) {
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(TestCache cache) {
        CustomStonecutterRecipe recipe = cache.getStonecutterRecipe();
        return !InventoryUtils.isCustomItemsListEmpty(recipe.getResults()) && !InventoryUtils.isCustomItemsListEmpty(recipe.getSource());
    }
}
