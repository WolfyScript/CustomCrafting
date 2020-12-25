package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.potion_creator.PotionCreator;
import me.wolfyscript.customcrafting.gui.potion_creator.PotionEffectTypeSelection;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public class PotionCreatorCluster extends CCCluster {

    public PotionCreatorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "potion_creator", customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new PotionCreator(this, customCrafting));
        registerGuiWindow(new PotionEffectTypeSelection(this, customCrafting));
    }
}
