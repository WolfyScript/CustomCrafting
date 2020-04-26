package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;

public class EditorMain extends ExtendedGuiWindow {

    public EditorMain(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("editor_main", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {


    }
}
