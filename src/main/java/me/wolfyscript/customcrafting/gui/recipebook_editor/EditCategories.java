package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.data.CacheButtonAction;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipebook_editor.buttons.CategoryButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;

public class EditCategories extends ExtendedGuiWindow {

    public EditCategories(GuiCluster<TestCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "categories", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (CacheButtonAction) (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
        registerButton(new ActionButton("previous", new ButtonState("previous", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (CacheButtonAction) (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {

            return true;
        })));
        registerButton(new ActionButton("next", new ButtonState("next", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (CacheButtonAction) (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {

            return true;
        })));
        registerButton(new ActionButton("add_category", PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (CacheButtonAction) (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            cache.getRecipeBookEditor().setCategory(new Category());
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.changeToInv("category");
            return true;
        }));

    }

    @Override
    public void onUpdateAsync(GuiUpdate<TestCache> update) {
        super.onUpdateAsync(update);
        GuiHandler<TestCache> guiHandler = update.getGuiHandler();
        TestCache cache = guiHandler.getCustomCache();
        RecipeBookEditor recipeBookEditor = cache.getRecipeBookEditor();
        RecipeBook recipeBook = customCrafting.getConfigHandler().getRecipeBook();
        update.setButton(0, "back");
        update.setButton(45, "previous");
        update.setButton(49, "add_category");
        update.setButton(53, "next");

        List<String> categories = recipeBookEditor.isSwitchCategories() ? recipeBook.getCategories().getSortedSwitchCategories() : recipeBook.getCategories().getSortedMainCategories();
        for (int i = 0; i < categories.size() && i + 9 < 45; i++) {
            String categoryID = categories.get(i);
            registerButton(new CategoryButton(categoryID, customCrafting));
            update.setButton(i + 9, "category_" + categoryID);
        }
    }
}
