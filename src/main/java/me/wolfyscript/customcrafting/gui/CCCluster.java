package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public abstract class CCCluster extends GuiCluster<TestCache> {

    protected CustomCrafting customCrafting;

    public CCCluster(InventoryAPI<TestCache> inventoryAPI, String id, CustomCrafting customCrafting) {
        super(inventoryAPI, id);
        this.customCrafting = customCrafting;
    }
}
