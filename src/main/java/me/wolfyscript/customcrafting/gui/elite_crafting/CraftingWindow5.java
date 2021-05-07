package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.EliteCraftingCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public class CraftingWindow5 extends CraftingWindow {

    public CraftingWindow5(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "crafting_grid5", 45, customCrafting, 5);
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        super.onUpdateSync(event);

        event.setButton(18, EliteCraftingCluster.RECIPE_BOOK);
        event.setButton(25, RESULT);
    }

    @Override
    public int getGridX() {
        return 1;
    }

}
