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

package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeList;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MenuListRecipes extends CCWindow {

    private static final String FILTER = "workstation_filter";

    public MenuListRecipes(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "recipe_list", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>(ClusterMain.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeList().setPage(0);
            for (int i = 0; i < 45; i++) {
                ButtonContainerRecipeList button = (ButtonContainerRecipeList) getButton(ButtonContainerRecipeList.key(i));
                button.setCustomRecipe(guiHandler, null);
                button.setRecipe(guiHandler, null);
            }
            if (cache.getRecipeList().getNamespace() == null) {
                guiHandler.openPreviousWindow();
                return true;
            }
            cache.getRecipeList().setNamespace(null);
            return true;
        })));
        registerButton(new ActionButton<>("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeList().setPage(cache.getRecipeList().getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton<>("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (cache, guiHandler, player, inventory, slot, event) -> {
            int page = cache.getRecipeList().getPage();
            if (page > 0) {
                cache.getRecipeList().setPage(--page);
            }
            return true;
        }));

        registerButton(new ButtonRecipeListWorkstationFilter());

        for (int i = 0; i < 45; i++) {
            registerButton(new ButtonContainerRecipeList(i, customCrafting));
            registerButton(new ButtonNamespaceRecipe(i, customCrafting));
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        GuiHandler<CCCache> guiHandler = event.getGuiHandler();
        event.setButton(0, "back");

        RecipeList recipeList = event.getGuiHandler().getCustomCache().getRecipeList();

        int maxPages;
        int page;

        String namespace = recipeList.getNamespace();
        if (namespace == null) {
            List<String> namespaceList = new ArrayList<>(customCrafting.getRegistries().getRecipes().namespaces());
            namespaceList.add("minecraft");
            namespaceList.sort(String::compareToIgnoreCase);
            maxPages = recipeList.getMaxPages(namespaceList.size());
            page = recipeList.getPage(maxPages);
            for (int i = 45 * page, slot = 0; slot < 45 && i < namespaceList.size(); i++, slot++) {
                ButtonNamespaceRecipe button = (ButtonNamespaceRecipe) getButton(ButtonNamespaceRecipe.key(slot));
                button.setNamespace(guiHandler, namespaceList.get(i));
                event.setButton(9 + slot, button);
            }
        } else if (namespace.equalsIgnoreCase("minecraft")) {
            List<Recipe> recipes = customCrafting.getDataHandler().getMinecraftRecipes().stream().sorted(Comparator.comparing(o -> ((Keyed) o).getKey().getKey())).collect(Collectors.toList());
            recipes.addAll(customCrafting.getDisableRecipesHandler().getCachedVanillaRecipes());
            recipeList.filterVanillaRecipes(recipes);
            maxPages = recipeList.getMaxPages(recipes.size());
            page = recipeList.getPage(maxPages);
            for (int i = 45 * page, slot = 0; slot < 45 && i < recipes.size(); i++, slot++) {
                ButtonContainerRecipeList button = (ButtonContainerRecipeList) getButton(ButtonContainerRecipeList.key(slot));
                button.setRecipe(event.getGuiHandler(), recipes.get(i));
                event.setButton(9 + slot, button);
            }
        } else {
            List<CustomRecipe<?>> recipes = customCrafting.getRegistries().getRecipes().get(namespace).stream().filter(Objects::nonNull).sorted(Comparator.comparing(o -> o.getNamespacedKey().getKey())).collect(Collectors.toList());
            recipeList.filterCustomRecipes(recipes);
            maxPages = recipeList.getMaxPages(recipes.size());
            page = recipeList.getPage(maxPages);
            for (int i = 45 * page, slot = 0; slot < 45 && i < recipes.size(); i++, slot++) {
                ButtonContainerRecipeList button = (ButtonContainerRecipeList) getButton(ButtonContainerRecipeList.key(slot));
                button.setCustomRecipe(event.getGuiHandler(), recipes.get(i));
                event.setButton(9 + slot, button);
            }
        }
        if (page != 0) {
            event.setButton(2, "previous_page");
        }
        event.setButton(4, ButtonRecipeListWorkstationFilter.KEY);
        if (page + 1 < maxPages) {
            event.setButton(6, "next_page");
        }
    }
}
