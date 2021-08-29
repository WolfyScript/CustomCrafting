package me.wolfyscript.customcrafting.gui.particle_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public class ClusterParticleCreator extends CCCluster {

    public static final String KEY = "particle_creator";

    public ClusterParticleCreator(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new MenuMain(this, customCrafting));
    }
}
