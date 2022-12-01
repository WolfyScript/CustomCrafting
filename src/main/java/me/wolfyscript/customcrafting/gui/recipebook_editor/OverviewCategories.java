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

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OverviewCategories extends Overview {

    public OverviewCategories(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "categories", customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        getButtonBuilder().action(ADD).state(state -> state.icon(PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777")).action((cache, guiHandler, player, inventory, btn, slot, event) -> {
            cache.getRecipeBookEditor().setCategory(new Category());
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openWindow("category");
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var recipeBookConfig = customCrafting.getConfigHandler().getRecipeBookConfig();
        update.setButton(49, ADD);

        List<String> categories = recipeBookConfig.getSortedCategories();
        for (int i = 0; i < categories.size() && i + 9 < 45; i++) {
            var category = recipeBookConfig.getCategory(categories.get(i));
            if (category != null) {
                String id = "category_" + category.getId();
                getButtonBuilder()
                        .action(id)
                        .state(state -> state.key("category").icon(Material.AIR)
                                .render((cache, guiHandler, player, guiInventory, btn, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(category.createItemStack(customCrafting)))
                                .action((cache, guiHandler, player, guiInventory, btn, i1, event) -> {
                                    if (event instanceof InventoryClickEvent clickEvent) {
                                        var recipeBookEditor = cache.getRecipeBookEditor();
                                        var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
                                        if (clickEvent.isRightClick() && clickEvent.isShiftClick()) {
                                            //Delete Category
                                            recipeBook.removeCategory(category.getId());
                                            return true;
                                        } else if (clickEvent.isLeftClick()) {
                                            //Edit Category
                                            recipeBookEditor.setCategoryID(category.getId());
                                            recipeBookEditor.setCategory(new Category(category));
                                            guiHandler.openWindow("category");
                                            return true;
                                        }
                                    }
                                    return true;
                                })
                        )
                        .register();
                update.setButton(i + 9, "category_" + category.getId());
            }
        }
    }
}
