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

    protected CustomCrafting customCrafting;
    protected WolfyUtilities api = CustomCrafting.inst().getApi();

    protected CCWindow(GuiCluster<CCCache> guiCluster, String namespace, int size, CustomCrafting customCrafting) {
        super(guiCluster, namespace, size);
        this.customCrafting = customCrafting;
    }

    public CustomCrafting getCustomCrafting() {
        return customCrafting;
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        CCPlayerData store = PlayerUtil.getStore(update.getPlayer());
        NamespacedKey white = new NamespacedKey("none", store.isDarkMode() ? "glass_gray" : "glass_white");
        NamespacedKey gray = new NamespacedKey("none", store.isDarkMode() ? "glass_black" : "glass_gray");
        if (getSize() > 9) {
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
