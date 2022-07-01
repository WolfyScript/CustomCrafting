/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        setForceSyncUpdate(true);
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
        if (customCrafting.getConfigHandler().getConfig().isGUIDrawBackground()) {
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
            } else {
                for (int i = 0; i < 9; i++) {
                    update.setButton(i, gray);
                }
            }
        }
    }
}
