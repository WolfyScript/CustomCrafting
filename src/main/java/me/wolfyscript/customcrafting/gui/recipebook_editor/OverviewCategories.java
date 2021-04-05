package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook_editor.buttons.CategoryButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;

public class OverviewCategories extends Overview {

    public OverviewCategories(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "categories", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        registerButton(new ActionButton<>(ADD, PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().setCategory(new Category());
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openWindow("category");
            return true;
        }));

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        RecipeBookConfig recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
        update.setButton(49, ADD);

        List<String> categories = recipeBook.getCategories().getSortedCategories();
        for (int i = 0; i < categories.size() && i + 9 < 45; i++) {
            String categoryID = categories.get(i);
            registerButton(new CategoryButton(categoryID, customCrafting));
            update.setButton(i + 9, "category_" + categoryID);
        }
    }
}
