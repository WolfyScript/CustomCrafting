package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipebook_editor.buttons.CategoryButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;

public class EditCategories extends CCWindow {

    public EditCategories(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "categories", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>("previous", new ButtonState<>("previous", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (cache, guiHandler, player, inventory, slot, event) -> {

            return true;
        })));
        registerButton(new ActionButton<>("next", new ButtonState<>("next", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (cache, guiHandler, player, inventory, slot, event) -> {

            return true;
        })));
        registerButton(new ActionButton<>("add_category", PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().setCategory(new CategorySettings());
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openWindow("category");
            return true;
        }));

    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        GuiHandler<CCCache> guiHandler = update.getGuiHandler();
        CCCache cache = guiHandler.getCustomCache();
        RecipeBookEditor recipeBookEditor = cache.getRecipeBookEditor();
        RecipeBookConfig recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
        update.setButton(0, "back");
        update.setButton(45, "previous");
        update.setButton(49, "add_category");
        update.setButton(53, "next");

        List<String> categories = recipeBookEditor.isSwitchCategories() ? recipeBook.getCategories().getSortedFilters() : recipeBook.getCategories().getSortedCategories();
        for (int i = 0; i < categories.size() && i + 9 < 45; i++) {
            String categoryID = categories.get(i);
            registerButton(new CategoryButton(categoryID, customCrafting));
            update.setButton(i + 9, "category_" + categoryID);
        }
    }
}
