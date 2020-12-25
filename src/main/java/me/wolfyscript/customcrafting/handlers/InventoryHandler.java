package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;

public class InventoryHandler {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final InventoryAPI<CCCache> invAPI;

    public InventoryHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.invAPI = this.api.getInventoryAPI(CCCache.class);
        this.customCrafting = customCrafting;
    }

    public void init() {
        api.getChat().sendConsoleMessage("$msg.startup.inventories$");
        invAPI.registerCluster(new MainCluster(invAPI, customCrafting));
        invAPI.registerCluster(new RecipeCreatorCluster(invAPI, customCrafting));
        invAPI.registerCluster(new RecipeBookCluster(invAPI, customCrafting));
        invAPI.registerCluster(new EliteCraftingCluster(invAPI, customCrafting));
        invAPI.registerCluster(new ItemCreatorCluster(invAPI, customCrafting));
        invAPI.registerCluster(new ParticleCreatorCluster(invAPI, customCrafting));
        invAPI.registerCluster(new PotionCreatorCluster(invAPI, customCrafting));
        invAPI.registerCluster(new RecipeBookEditorCluster(invAPI, customCrafting));
    }

}
