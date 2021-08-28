package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

public abstract class Overview extends CCWindow {

    protected static final String PREVIOUS = "previous";
    protected static final String NEXT = "next";
    protected static final String ADD = "add";

    protected Overview(GuiCluster<CCCache> guiCluster, String namespace, CustomCrafting customCrafting) {
        super(guiCluster, namespace, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(PREVIOUS, PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (cache, guiHandler, player, inventory, slot, event) -> true));
        registerButton(new ActionButton<>(NEXT, PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (cache, guiHandler, player, inventory, slot, event) -> true));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, ClusterMain.BACK);
        update.setButton(47, PREVIOUS);
        update.setButton(51, NEXT);
    }
}
