package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;

public abstract class ExtendedGuiWindow extends GuiWindow {

    protected CustomCrafting customCrafting;
    protected WolfyUtilities api = CustomCrafting.getApi();

    public ExtendedGuiWindow(String namespace, InventoryAPI inventoryAPI, int size, CustomCrafting customCrafting) {
        super(namespace, inventoryAPI, size);
        this.customCrafting = customCrafting;
    }

    public CustomCrafting getCustomCrafting() {
        return customCrafting;
    }
}
