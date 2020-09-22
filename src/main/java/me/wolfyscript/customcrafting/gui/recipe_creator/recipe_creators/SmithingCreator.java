package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.SmithingContainerButton;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;

public class SmithingCreator extends RecipeCreator {

    public SmithingCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("smithing", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new SmithingContainerButton(0, customCrafting));
        registerButton(new SmithingContainerButton(1, customCrafting));
        registerButton(new SmithingContainerButton(2, customCrafting));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
        event.setButton(0, "back");
        CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
        ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), smithingRecipe.isExactMeta());
        ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), smithingRecipe.isHidden());
        event.setButton(1, "hidden");
        event.setButton(3, "recipe_creator", "conditions");
        event.setButton(5, "priority");
        event.setButton(7, "exact_meta");
        event.setButton(19, "container_0");
        event.setButton(22, "container_1");
        event.setButton(25, "container_2");

        if(smithingRecipe.hasNamespacedKey()){
            event.setButton(43, "save");
        }
        event.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(TestCache cache) {
        CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
        return !InventoryUtils.isCustomItemsListEmpty(smithingRecipe.getBase()) && !InventoryUtils.isCustomItemsListEmpty(smithingRecipe.getAddition()) && !InventoryUtils.isCustomItemsListEmpty(smithingRecipe.getResults());
    }
}
