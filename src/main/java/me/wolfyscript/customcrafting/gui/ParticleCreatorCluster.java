package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.particle_creator.MainMenu;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public class ParticleCreatorCluster extends CCCluster {

    public static final String KEY = "particle_creator";

    public ParticleCreatorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new MainMenu(this, customCrafting));
    }
}
