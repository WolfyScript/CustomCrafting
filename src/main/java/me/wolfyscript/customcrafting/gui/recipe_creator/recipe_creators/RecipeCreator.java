package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;

public abstract class RecipeCreator extends ExtendedGuiWindow {

    public RecipeCreator(String namespace, InventoryAPI inventoryAPI, int size, CustomCrafting customCrafting) {
        super(namespace, inventoryAPI, size, customCrafting);
    }

    abstract public boolean validToSave(TestCache cache);
}
