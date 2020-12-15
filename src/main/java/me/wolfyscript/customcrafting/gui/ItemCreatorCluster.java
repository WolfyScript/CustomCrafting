package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public class ItemCreatorCluster extends CCCluster {

    public ItemCreatorCluster(InventoryAPI<TestCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "item_creator", customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new ItemCreator(this, customCrafting));
    }
}
