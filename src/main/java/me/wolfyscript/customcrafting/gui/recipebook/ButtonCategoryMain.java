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

package me.wolfyscript.customcrafting.gui.recipebook;

import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonType;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.io.IOException;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;
import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

class ButtonCategoryMain extends Button<CCCache> {

    private final CustomCrafting customCrafting;
    private final RecipeBookConfig recipeBookConfig;
    private final Category category;

    ButtonCategoryMain(String categoryId, CustomCrafting customCrafting) {
        super("main_category." + categoryId, ButtonType.NORMAL);
        this.customCrafting = customCrafting;
        this.recipeBookConfig = customCrafting.getConfigHandler().getRecipeBookConfig();
        this.category = recipeBookConfig.getCategory(categoryId);
    }

    @Override
    public ButtonInteractionResult execute(GUIHolder<CCCache> holder, int slot) {
        if (category != null) {
            var knowledgeBook = holder.getGuiHandler().getCustomCache().getRecipeBookCache();
            knowledgeBook.setCategory(category);
            holder.getGuiHandler().openWindow(ClusterRecipeBook.CATEGORY_OVERVIEW);
        }
        return ButtonInteractionResult.cancel(true);
    }

    @Override
    public void postExecute(GUIHolder<CCCache> holder, ItemStack itemStack, int slot) throws IOException {

    }

    @Override
    public void preRender(GUIHolder<CCCache> holder, ItemStack itemStack, int slot) {

    }

    @Override
    public void render(GUIHolder<CCCache> holder, Inventory queueInventory, int slot) {
        if (category != null) {
            queueInventory.setItem(slot, category.createItemStack(customCrafting));
        }
    }

    @Override
    public void init(GuiWindow<CCCache> guiWindow) {

    }

    @Override
    public void init(GuiCluster<CCCache> guiCluster) {

    }

}
