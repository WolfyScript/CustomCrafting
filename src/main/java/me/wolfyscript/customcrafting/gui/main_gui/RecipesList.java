package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeListContainerButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeListNamespaceButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RecipesList extends ExtendedGuiWindow {

    private final HashMap<GuiHandler, Integer> pages = new HashMap<>();
    private final HashMap<GuiHandler, String> namespaces = new HashMap<>();
    private static int maxPages = 0;

    public RecipesList(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("recipe_list", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, slot, event) -> {
            pages.put(guiHandler, 0);
            for (int i = 0; i < 45; i++) {
                RecipeListContainerButton button = (RecipeListContainerButton) getButton("recipe_list.container_" + i);
                button.setCustomRecipe(guiHandler, null);
                button.setRecipe(guiHandler, null);
            }
            if (namespaces.getOrDefault(guiHandler, "").isEmpty()) {
                guiHandler.openPreviousInv();
                return true;
            }
            namespaces.put(guiHandler, "");
            return true;
        })));
        registerButton(new ActionButton("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (pages.getOrDefault(guiHandler, 0) + 1 < getMaxPages()) {
                pages.put(guiHandler, pages.getOrDefault(guiHandler, 0) + 1);
            }
            return true;
        }));
        registerButton(new ActionButton("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (pages.getOrDefault(guiHandler, 0) > 0) {
                pages.put(guiHandler, pages.getOrDefault(guiHandler, 0) - 1);
            }
            return true;
        }));

        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeListContainerButton(i, customCrafting));
            registerButton(new RecipeListNamespaceButton(i, customCrafting));
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        GuiHandler guiHandler = event.getGuiHandler();
        Category category = ((ItemCategoryButton) event.getInventoryAPI().getGuiCluster("recipe_book").getButton("itemCategory")).getCategory(guiHandler);
        int currentPage = pages.getOrDefault(event.getGuiHandler(), 0);
        event.setButton(0, "back");
        event.setButton(4, "recipe_book", "itemCategory");

        String namespace = namespaces.getOrDefault(guiHandler, "");
        if (namespace.isEmpty()) {
            List<String> namespaceList = new ArrayList<>();
            namespaceList.add("minecraft");
            namespaceList.addAll(customCrafting.getRecipeHandler().getNamespaces());
            maxPages = namespaceList.size() / 45 + (namespaceList.size() % 45 > 0 ? 1 : 0);
            int item = 0;
            for (int i = 45 * currentPage; item < 45 && i < namespaceList.size(); i++) {
                RecipeListNamespaceButton button = (RecipeListNamespaceButton) event.getGuiWindow().getButton("recipe_list.namespace_" + item);
                button.setNamespace(guiHandler, namespaceList.get(i));
                event.setButton(9 + item, button);
                item++;
            }
        } else {
            List<Object> recipes = new ArrayList<>();
            if (category != null) {
                if (namespace.equalsIgnoreCase("minecraft")) {
                    recipes.addAll(customCrafting.getRecipeHandler().getVanillaRecipes().stream().filter(recipe -> category.isValid(recipe.getResult().getType())).collect(Collectors.toList()));
                } else {
                    recipes.addAll(customCrafting.getRecipeHandler().getRecipesByNamespace(namespace).stream().filter(recipe -> category.isValid(recipe) || recipe.getResults().stream().anyMatch(item -> category.isValid(item.getItemStack().getType()))).collect(Collectors.toList()));
                }
            }
            maxPages = recipes.size() / 45 + (recipes.size() % 45 > 0 ? 1 : 0);
            if (currentPage >= maxPages) {
                currentPage = 0;
            }
            int item = 0;
            for (int i = 45 * currentPage; item < 45 && i < recipes.size(); i++) {
                Object recipe = recipes.get(i);
                RecipeListContainerButton button = (RecipeListContainerButton) event.getGuiWindow().getButton("recipe_list.container_" + item);
                if (recipe instanceof Recipe) {
                    button.setRecipe(event.getGuiHandler(), (Recipe) recipe);
                } else if (recipe instanceof ICustomRecipe) {
                    button.setCustomRecipe(event.getGuiHandler(), (ICustomRecipe) recipe);
                }
                event.setButton(9 + item, button);
                item++;
            }
        }
        if (currentPage != 0) {
            event.setButton(2, "previous_page");
        }
        if (currentPage + 1 < maxPages) {
            event.setButton(6, "next_page");
        }
    }

    private int getMaxPages() {
        return maxPages;
    }

    public void setPage(GuiHandler guiHandler, int page) {
        this.pages.put(guiHandler, page);
    }

    public HashMap<GuiHandler, String> getRecipeNamespaces() {
        return namespaces;
    }
}
