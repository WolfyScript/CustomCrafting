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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeList;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.registry.RegistryRecipes;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MenuListRecipes extends CCWindow {

    public MenuListRecipes(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "recipe_list", 54, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action("back").state(state -> state.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"))
                .action((cache, guiHandler, player, guiInventory, slot, inventoryInteractEvent) -> {
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
                    if (cache.getRecipeList().getFolder() == null) {
                        cache.getRecipeList().setNamespace(null);
                    } else {
                        cache.getRecipeList().setFolder(null);
                    }
                    return true;
                })).register();
        getButtonBuilder().action("next_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((cache, guiHandler, player, guiInv, i, event) -> {
            cache.getRecipeList().setPage(cache.getRecipeList().getPage() + 1);
            return true;
        })).register();
        getButtonBuilder().action("previous_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((cache, guiHandler, player, guiInv, i, event) -> {
            int page = cache.getRecipeList().getPage();
            if (page > 0) {
                cache.getRecipeList().setPage(--page);
            }
            return true;
        })).register();
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
        event.setButton(8, ClusterMain.GUI_HELP);

        RegistryRecipes customRecipes = customCrafting.getRegistries().getRecipes();
        RecipeList recipeListCache = event.getGuiHandler().getCustomCache().getRecipeList();

        int maxPages;
        int page;

        String namespace = recipeListCache.getNamespace();
        String folder = recipeListCache.getFolder();
        if (namespace == null) {
            List<String> namespaceList = customRecipes.namespaces();
            namespaceList.add("minecraft");
            namespaceList.sort(String::compareToIgnoreCase);
            maxPages = recipeListCache.getMaxPages(namespaceList.size());
            page = recipeListCache.getPage(maxPages);
            for (int i = 45 * page, slot = 0; slot < 45 && i < namespaceList.size(); i++, slot++) {
                ButtonNamespaceRecipe button = (ButtonNamespaceRecipe) getButton(ButtonNamespaceRecipe.key(slot));
                button.setNamespace(guiHandler, namespaceList.get(i));
                event.setButton(9 + slot, button);
            }
        } else if (namespace.equalsIgnoreCase("minecraft")) {
            List<Recipe> recipes = customCrafting.getDataHandler().getMinecraftRecipes().stream().sorted(Comparator.comparing(o -> ((Keyed) o).getKey().getKey())).collect(Collectors.toList());
            recipes.addAll(customCrafting.getDisableRecipesHandler().getCachedVanillaRecipes());
            recipeListCache.filterVanillaRecipes(recipes);
            maxPages = recipeListCache.getMaxPages(recipes.size());
            page = recipeListCache.getPage(maxPages);
            for (int i = 45 * page, slot = 0; slot < 45 && i < recipes.size(); i++, slot++) {
                ButtonContainerRecipeList button = (ButtonContainerRecipeList) getButton(ButtonContainerRecipeList.key(slot));
                button.setRecipe(event.getGuiHandler(), recipes.get(i));
                event.setButton(9 + slot, button);
            }
        } else if (folder == null) {
            List<String> folders = customRecipes.folders(namespace);
            folders.remove("");
            maxPages = recipeListCache.getMaxPages(folders.size());
            page = recipeListCache.getPage(maxPages);
            for (int i = 45 * page, slot = 0; slot < 45 && i < folders.size(); i++, slot++) {
                String key = ButtonFolderRecipe.key(slot, namespace, folders.get(i));
                if (getButton(key) == null) {
                    ButtonFolderRecipe button = new ButtonFolderRecipe(slot, namespace, folders.get(i), customCrafting);
                    registerButton(button);
                }
                event.setButton(9 + slot, getButton(key));
            }
        } else {
            List<CustomRecipe<?>> recipes = customCrafting.getRegistries().getRecipes().get(namespace, folder).stream().filter(Objects::nonNull).sorted(Comparator.comparing(o -> o.getNamespacedKey().getKey())).collect(Collectors.toList());
            recipeListCache.filterCustomRecipes(recipes);
            maxPages = recipeListCache.getMaxPages(recipes.size());
            page = recipeListCache.getPage(maxPages);
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
