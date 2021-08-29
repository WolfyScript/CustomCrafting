package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

public abstract class EditCategorySetting extends CCWindow {

    protected EditCategorySetting(GuiCluster<CCCache> guiCluster, String namespace, CustomCrafting customCrafting) {
        super(guiCluster, namespace, 54, customCrafting);
    }

    @Override
    public void onInit() {


    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, ClusterRecipeBookEditor.BACK);
        update.setButton(11, ClusterRecipeBookEditor.NAME);
        update.setButton(13, ClusterRecipeBookEditor.ICON);
        update.setButton(15, ClusterRecipeBookEditor.DESCRIPTION_ADD);
        update.setButton(16, ClusterRecipeBookEditor.DESCRIPTION_REMOVE);
        if (update.getGuiHandler().getCustomCache().getRecipeBookEditor().hasCategoryID()) {
            update.setButton(52, ClusterRecipeBookEditor.SAVE);
        }
        update.setButton(53, ClusterRecipeBookEditor.SAVE_AS);
    }
}
