package me.wolfyscript.customcrafting.gui.lists;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeList;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.lists.buttons.RecipeListContainerButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeListNamespaceButton;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RecipesList extends CCWindow {

    public RecipesList(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "recipe_list", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeList().setPage(0);
            for (int i = 0; i < 45; i++) {
                RecipeListContainerButton button = (RecipeListContainerButton) getButton("recipe_list.container_" + i);
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

        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeListContainerButton(i, customCrafting));
            registerButton(new RecipeListNamespaceButton(i, customCrafting));
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
            List<String> namespaceList = new ArrayList<>(Registry.RECIPES.namespaces());
            namespaceList.add("minecraft");
            namespaceList.sort(String::compareToIgnoreCase);
            maxPages = recipeList.getMaxPages(namespaceList.size());
            page = recipeList.getPage(maxPages);
            for (int i = 45 * page, slot = 0; slot < 45 && i < namespaceList.size(); i++, slot++) {
                RecipeListNamespaceButton button = (RecipeListNamespaceButton) getButton("recipe_list.namespace_" + slot);
                button.setNamespace(guiHandler, namespaceList.get(i));
                event.setButton(9 + slot, button);
            }
        } else {
            if (namespace.equalsIgnoreCase("minecraft")) {
                List<Recipe> recipes = customCrafting.getDataHandler().getMinecraftRecipes().stream().sorted(Comparator.comparing(o -> ((Keyed) o).getKey().getKey())).toList();
                maxPages = recipeList.getMaxPages(recipes.size());
                page = recipeList.getPage(maxPages);
                for (int i = 45 * page, slot = 0; slot < 45 && i < recipes.size(); i++, slot++) {
                    RecipeListContainerButton button = (RecipeListContainerButton) getButton("recipe_list.container_" + slot);
                    button.setRecipe(event.getGuiHandler(), recipes.get(i));
                    event.setButton(9 + slot, button);
                }
            } else {
                List<ICustomRecipe<?, ?>> recipes = Registry.RECIPES.get(namespace).stream().filter(Objects::nonNull).sorted(Comparator.comparing(o -> o.getNamespacedKey().getKey())).toList();
                maxPages = recipeList.getMaxPages(recipes.size());
                page = recipeList.getPage(maxPages);
                for (int i = 45 * page, slot = 0; slot < 45 && i < recipes.size(); i++, slot++) {
                    RecipeListContainerButton button = (RecipeListContainerButton) getButton("recipe_list.container_" + slot);
                    button.setCustomRecipe(event.getGuiHandler(), recipes.get(i));
                    event.setButton(9 + slot, button);
                }
            }
        }
        if (page != 0) {
            event.setButton(2, "previous_page");
        }
        if (page + 1 < maxPages) {
            event.setButton(6, "next_page");
        }
    }
}
