package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook_editor.EditCategories;
import me.wolfyscript.customcrafting.gui.recipebook_editor.EditCategory;
import me.wolfyscript.customcrafting.gui.recipebook_editor.EditorMain;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public class RecipeBookEditorCluster extends CCCluster {

    public RecipeBookEditorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "recipe_book_editor", customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new EditorMain(this, customCrafting));
        registerGuiWindow(new EditCategories(this, customCrafting));
        registerGuiWindow(new EditCategory(this, customCrafting));
    }
}
