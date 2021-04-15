package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.potion_creator.PotionCreator;
import me.wolfyscript.customcrafting.gui.potion_creator.PotionEffectTypeSelection;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;

public class PotionCreatorCluster extends CCCluster {

    public static final String KEY = "potion_creator";

    public static final NamespacedKey POTION_CREATOR = new NamespacedKey(KEY, "potion_creator");
    public static final NamespacedKey POTION_EFFECT_TYPE_SELECTION = new NamespacedKey(KEY, "potion_effect_type_selection");

    public PotionCreatorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new PotionCreator(this, customCrafting));
        registerGuiWindow(new PotionEffectTypeSelection(this, customCrafting));
    }
}
