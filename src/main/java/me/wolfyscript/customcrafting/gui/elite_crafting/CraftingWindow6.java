package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public class CraftingWindow6 extends CraftingWindow {

    CraftingWindow6(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "crafting_grid6", 54, customCrafting, 6);
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        super.onUpdateSync(event);

        event.setButton(16, EliteCraftingCluster.RECIPE_BOOK);
        event.setButton(43, RESULT);
    }

    @Override
    public int getGridX() {
        return 0;
    }
}
