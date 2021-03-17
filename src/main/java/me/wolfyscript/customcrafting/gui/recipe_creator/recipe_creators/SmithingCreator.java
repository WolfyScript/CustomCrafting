package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.SmithingContainerButton;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;

public class SmithingCreator extends RecipeCreator {

    public SmithingCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "smithing", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new SmithingContainerButton(0));
        registerButton(new SmithingContainerButton(1));
        registerButton(new SmithingContainerButton(2));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, "back");
        CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
        ((ToggleButton<CCCache>) getButton("exact_meta")).setState(event.getGuiHandler(), smithingRecipe.isExactMeta());
        ((ToggleButton<CCCache>) getButton("hidden")).setState(event.getGuiHandler(), smithingRecipe.isHidden());
        event.setButton(1, "hidden");
        event.setButton(3, "recipe_creator", "conditions");
        event.setButton(5, "priority");
        event.setButton(7, "exact_meta");
        event.setButton(19, "container_0");
        event.setButton(22, "container_1");
        event.setButton(25, "container_2");

        if (smithingRecipe.hasNamespacedKey()) {
            event.setButton(43, "save");
        }
        event.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(CCCache cache) {
        CustomSmithingRecipe smithingRecipe = cache.getSmithingRecipe();
        return !InventoryUtils.isCustomItemsListEmpty(smithingRecipe.getBase()) && !InventoryUtils.isCustomItemsListEmpty(smithingRecipe.getAddition()) && !InventoryUtils.isCustomItemsListEmpty(smithingRecipe.getResults());
    }
}
