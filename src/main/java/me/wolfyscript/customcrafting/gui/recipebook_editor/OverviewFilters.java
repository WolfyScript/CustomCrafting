package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook_editor.buttons.FilterButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;

public class OverviewFilters extends Overview {

    public OverviewFilters(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "filters", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        registerButton(new ActionButton<>(ADD, PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().setFilter(new CategoryFilter());
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openWindow("filter");
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        RecipeBookConfig recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
        update.setButton(49, ADD);

        List<String> categories = recipeBook.getCategories().getSortedFilters();
        for (int i = 0; i < categories.size() && i + 9 < 45; i++) {
            String id = categories.get(i);
            registerButton(new FilterButton(id, customCrafting));
            update.setButton(i + 9, "filter_" + id);
        }
    }
}
