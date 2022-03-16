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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeContainer;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MenuRecipeBook extends CCWindow {

    private static final String BACK = "back";
    private static final String NEXT_RECIPE = "next_recipe";
    private static final String PREVIOUS_RECIPE = "previous_recipe";
    private final BukkitTask ingredientTask;
    private final BukkitTask containerTask;

    MenuRecipeBook(ClusterRecipeBook cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeBook.RECIPE_BOOK.getKey(), 54, customCrafting);
        this.ingredientTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
            for (int i = 0; i < 37; i++) {
                Button<CCCache> btn = cluster.getButton("ingredient.container_" + i);
                if (btn instanceof ButtonContainerIngredient cBtn) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> cBtn.getTasks().removeIf(Supplier::get));
                }
            }
        }, 1, 25);
        this.containerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
            for (int i = 0; i < 45; i++) {
                Button<CCCache> mainContainerBtn = cluster.getButton("recipe_book.container_" + i);
                if (mainContainerBtn instanceof ButtonContainerRecipeBook cBtn) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> cBtn.getTasks().removeIf(Supplier::get));
                }
            }
        }, 1, 25);
    }

    public void reset() {
        this.containerTask.cancel();
        this.ingredientTask.cancel();
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(BACK, new ButtonState<>(ClusterMain.BACK_BOTTOM, Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            ButtonContainerIngredient.resetButtons(guiHandler);
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>(NEXT_RECIPE, PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (cache, guiHandler, player, inventory, slot, event) -> {
            var book = cache.getRecipeBookCache();
            ButtonContainerIngredient.resetButtons(guiHandler);
            int nextPage = book.getSubFolderPage() + 1;
            if (nextPage < book.getSubFolderRecipes().size()) {
                book.setSubFolderPage(nextPage);
                book.setPrepareRecipe(true);
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var book = guiHandler.getCustomCache().getRecipeBookCache();
            values.put("%page%", book.getSubFolderPage() + 1);
            values.put("%max_pages%", book.getSubFolderRecipes().size());
            return itemStack;
        }));
        registerButton(new ActionButton<>(PREVIOUS_RECIPE, PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (cache, guiHandler, player, inventory, slot, event) -> {
            var book = cache.getRecipeBookCache();
            ButtonContainerIngredient.resetButtons(guiHandler);
            if (book.getSubFolderPage() > 0) {
                book.setSubFolderPage(book.getSubFolderPage() - 1);
                book.setPrepareRecipe(true);
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var book = guiHandler.getCustomCache().getRecipeBookCache();
            values.put("%page%", book.getSubFolderPage() + 1);
            values.put("%max_pages%", book.getSubFolderRecipes().size());
            return itemStack;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        var configHandler = customCrafting.getConfigHandler();
        var player = event.getPlayer();
        CCPlayerData playerStore = PlayerUtil.getStore(player);
        NamespacedKey grayBtnKey = playerStore.getLightBackground();
        var recipeBookCache = event.getGuiHandler().getCustomCache().getRecipeBookCache();
        if (recipeBookCache.getSubFolder() == 0) {
            for (int i = 0; i < 9; i++) {
                event.setButton(i, playerStore.getDarkBackground());
            }
            List<RecipeContainer> containers = recipeBookCache.getCategory() != null ? recipeBookCache.getCategory().getRecipeList(player, recipeBookCache.getCategoryFilter(), recipeBookCache.getEliteCraftingTable()) : new ArrayList<>();
            int maxPages = containers.size() / 45 + (containers.size() % 45 > 0 ? 1 : 0);
            if (recipeBookCache.getPage() >= maxPages) {
                recipeBookCache.setPage(0);
            }
            for (int item = 0, i = 45 * recipeBookCache.getPage(); item < 45 && i < containers.size(); i++, item++) {
                ButtonContainerRecipeBook button = (ButtonContainerRecipeBook) getCluster().getButton("recipe_book.container_" + item);
                if (button != null) {
                    button.setRecipeContainer(event.getGuiHandler(), containers.get(i));
                    event.setButton(item, ButtonContainerRecipeBook.namespacedKey(item));
                }
            }
            if (configHandler.getRecipeBookConfig().getSortedCategories().size() > 1) {
                event.setButton(45, BACK);
            }
            if (recipeBookCache.getPage() != 0) {
                event.setButton(47, ClusterRecipeBook.PREVIOUS_PAGE);
            }
            event.setButton(49, ClusterRecipeBook.ITEM_CATEGORY);
            if (recipeBookCache.getPage() + 1 < maxPages) {
                event.setButton(51, ClusterRecipeBook.NEXT_PAGE);
            }
        } else {
            for (int i = 1; i < 9; i++) {
                event.setButton(i, grayBtnKey);
            }
            List<CustomRecipe<?>> recipes = recipeBookCache.getSubFolderRecipes();
            for (int i = 1; i < 9; i++) {
                event.setButton(i, grayBtnKey);
            }
            for (int i = 36; i < 45; i++) {
                event.setButton(i, grayBtnKey);
            }
            int maxPages = recipes.size();
            if (recipeBookCache.getSubFolderPage() >= maxPages) {
                recipeBookCache.setSubFolderPage(0);
            }
            if (recipeBookCache.getSubFolderPage() < recipes.size()) {
                CustomRecipe<?> customRecipe = recipes.get(recipeBookCache.getSubFolderPage());
                if (recipeBookCache.isPrepareRecipe()) { //This makes sure we only prepare the recipe once
                    //A new prepare can be queued by using book.setPrepareRecipe(true)
                    recipeBookCache.applyRecipeToButtons(event.getGuiHandler(), customRecipe);
                    recipeBookCache.setPrepareRecipe(false);
                }
                customRecipe.renderMenu(this, event);
                boolean elite = RecipeType.Container.ELITE_CRAFTING.isInstance(customRecipe);
                if (recipeBookCache.getSubFolderPage() > 0) {
                    event.setButton(elite ? 51 : 48, PREVIOUS_RECIPE);
                }
                event.setButton(elite ? 52 : 49, ClusterRecipeBook.BACK_TO_LIST);
                if (recipeBookCache.getSubFolderPage() + 1 < recipes.size()) {
                    event.setButton(elite ? 53 : 50, NEXT_RECIPE);
                }
            }
        }
    }

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        ButtonContainerIngredient.removeTasks(guiHandler);
        ButtonContainerRecipeBook.resetButtons(guiHandler);
        guiHandler.getCustomCache().getRecipeBookCache().setEliteCraftingTable(null);
        return super.onClose(guiHandler, guiInventory, transaction);
    }
}
