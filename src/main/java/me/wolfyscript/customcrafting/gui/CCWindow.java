package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;

public abstract class CCWindow extends GuiWindow<CCCache> {

    protected final CustomCrafting customCrafting;
    protected final WolfyUtilities api = CustomCrafting.inst().getApi();

    protected CCWindow(GuiCluster<CCCache> guiCluster, String namespace, int size, CustomCrafting customCrafting) {
        super(guiCluster, namespace, size);
        this.customCrafting = customCrafting;
    }

    public CustomCrafting getCustomCrafting() {
        return customCrafting;
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {
        //No need to update sync here.
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        CCPlayerData store = PlayerUtil.getStore(update.getPlayer());
        NamespacedKey gray = store.getDarkBackground();
        if (getSize() > 9) {
            NamespacedKey white = store.getLightBackground();
            for (int i = 0; i < 9; i++) {
                update.setButton(i, white);
            }
            for (int i = 9; i < getSize() - 9; i++) {
                update.setButton(i, gray);
            }
            for (int i = getSize() - 9; i < getSize(); i++) {
                update.setButton(i, white);
            }
            update.setButton(8, new NamespacedKey("none", "gui_help"));
        } else {
            for (int i = 0; i < 9; i++) {
                update.setButton(i, gray);
            }
        }
    }
}
