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

package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import org.bukkit.Material;

public class EditCategory extends EditCategorySetting {

    private static final String AUTO = "auto";
    private static final String DELETE = "delete";

    public EditCategory(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "category", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        getButtonBuilder().toggle(AUTO)
                .stateFunction((cache, guiHandler, player, guiInventory, i) -> cache.getRecipeBookEditorCache().getCategory().isAuto())
                .enabledState(s -> s.subKey("enabled").icon(Material.COMMAND_BLOCK).action((cache, guiHandler, player, inventory, slot, event) -> {
                    cache.getRecipeBookEditorCache().getCategory().setAuto(false);
                    return true;
                })).disabledState(s -> s.subKey("disabled").icon(Material.PLAYER_HEAD).action((cache, guiHandler, player, inventory, slot, event) -> {
                    cache.getRecipeBookEditorCache().getCategory().setAuto(true);
                    return true;
                })).register();

        getButtonBuilder().action(DELETE).state(builder -> builder.icon(Material.TNT)
                .render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("id", cache.getRecipeBookEditorCache().getCategoryID())))
                .action((cache, guiHandler, player, guiInventory, i, event) -> {
                    cache.getRecipeBookEditorCache().getEditorConfigCopy().removeCategory(cache.getRecipeBookEditorCache().getCategoryID());
                    cache.getRecipeBookEditorCache().setCategory(null);
                    cache.getRecipeBookEditorCache().setCategoryID("");
                    guiHandler.openPreviousWindow();
                    return true;
                })
        ).register();

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        if (update.getGuiHandler().getCustomCache().getRecipeBookEditorCache().getCategoryID() != null) {
            update.setButton(0, DELETE);
        }
        update.setButton(22, AUTO);
        if (!update.getGuiHandler().getCustomCache().getRecipeBookEditorCache().getCategory().isAuto()) {
            update.setButton(29, ClusterRecipeBookEditor.RECIPES);
            update.setButton(33, ClusterRecipeBookEditor.FOLDERS);
            update.setButton(40, ClusterRecipeBookEditor.GROUPS);
        }
    }
}
