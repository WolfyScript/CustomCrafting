package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.particle_creator.MainMenu;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public class ParticleCreatorCluster extends CCCluster {

    public ParticleCreatorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "particle_creator", customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new MainMenu(this, customCrafting));
    }
}
