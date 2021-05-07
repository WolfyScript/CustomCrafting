package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.EliteCraftingCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public class CraftingWindow3 extends CraftingWindow {

    public CraftingWindow3(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "crafting_grid3", 45, customCrafting, 3);
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        super.onUpdateSync(event);

        event.setButton(9, EliteCraftingCluster.RECIPE_BOOK);
        event.setButton(16, RESULT);
    }

    @Override
    public int getGridX() {
        return 2;
    }

}
