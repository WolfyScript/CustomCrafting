package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public abstract class CCCluster extends GuiCluster<CCCache> {

    protected final CustomCrafting customCrafting;

    protected CCCluster(InventoryAPI<CCCache> inventoryAPI, String id, CustomCrafting customCrafting) {
        super(inventoryAPI, id);
        this.customCrafting = customCrafting;
    }
}
