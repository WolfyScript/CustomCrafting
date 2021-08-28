package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ItemCreatorCluster extends CCCluster {

    public static final String KEY = "item_creator";

    public static final NamespacedKey MAIN_MENU = new NamespacedKey(KEY, "main_menu");

    public ItemCreatorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new ItemCreator(this, customCrafting));
    }
}
