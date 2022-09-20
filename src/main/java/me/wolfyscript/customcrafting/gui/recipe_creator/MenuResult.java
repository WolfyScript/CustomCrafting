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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class MenuResult extends CCWindow {

    public MenuResult(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "result", 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new ButtonContainerItemResult(i));
        }
        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((cache, guiHandler, player, inv, i, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().getResult().buildChoices();
            guiHandler.openPreviousWindow();
            return true;
        })).register();
        getButtonBuilder().action("tags").state(s -> s.key(ClusterRecipeCreator.TAGS).icon(Material.NAME_TAG).action((cache, guiHandler, player, inv, i, event) -> {
            cache.getRecipeCreatorCache().getTagSettingsCache().setRecipeItemStack(cache.getRecipeCreatorCache().getRecipeCache().getResult());
            guiHandler.openWindow("tag_settings");
            return true;
        })).register();
        getButtonBuilder().dummy("target").state(s -> s.icon(Material.ARROW)).register();
        getButtonBuilder().dummy("extensions").state(s -> s.icon(Material.COMMAND_BLOCK)).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        update.setButton(8, ClusterMain.GUI_HELP);
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "variant_container_" + i);
        }
        update.setButton(47, "target");
        update.setButton(49, "extensions");
        update.setButton(51, "tags");


    }
}
