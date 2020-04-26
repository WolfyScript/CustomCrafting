package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeListContainerButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeListNamespaceButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Keyed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RecipesList extends ExtendedGuiWindow {

    private HashMap<GuiHandler, Integer> pages = new HashMap<>();
    private HashMap<GuiHandler, String> namespaces = new HashMap<>();
    private static int maxPages = 0;

    public RecipesList(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("recipe_list", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, slot, event) -> {
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
        registerButton(new ActionButton("next_page", new ButtonState("next_page", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (pages.getOrDefault(guiHandler, 0) + 1 < getMaxPages()) {
                pages.put(guiHandler, pages.getOrDefault(guiHandler, 0) + 1);
            }
            return true;
        })));
        registerButton(new ActionButton("previous_page", new ButtonState("previous_page", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (pages.getOrDefault(guiHandler, 0) > 0) {
                pages.put(guiHandler, pages.getOrDefault(guiHandler, 0) - 1);
            }
            return true;
        })));

        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeListContainerButton(i, customCrafting));
            registerButton(new RecipeListNamespaceButton(i, customCrafting));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            GuiHandler guiHandler = event.getGuiHandler();
            KnowledgeBook knowledgeBook = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
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
                if (namespace.equalsIgnoreCase("minecraft")) {
                    List<Recipe> recipes = new ArrayList<>();
                    recipes.addAll(customCrafting.getRecipeHandler().getVanillaRecipes());
                    if (category != null) {
                        Iterator<Recipe> recipeIterator = recipes.iterator();
                        while (recipeIterator.hasNext()) {
                            if (category.isValid(recipeIterator.next().getResult().getType())) {
                                recipeIterator.remove();
                            }
                        }
                    }
                    maxPages = recipes.size() / 45 + (recipes.size() % 45 > 0 ? 1 : 0);
                    if (currentPage >= maxPages) {
                        currentPage = 0;
                    }
                    int item = 0;
                    for (int i = 45 * currentPage; item < 45 && i < recipes.size(); i++) {
                        Recipe recipe = recipes.get(i);
                        RecipeListContainerButton button = (RecipeListContainerButton) event.getGuiWindow().getButton("recipe_list.container_" + item);
                        if (recipe instanceof Keyed) {
                            button.setRecipe(event.getGuiHandler(), recipe);
                        }
                        event.setButton(9 + item, button);
                        item++;
                    }
                } else {
                    List<CustomRecipe> recipes = new ArrayList<>();
                    recipes.addAll(customCrafting.getRecipeHandler().getRecipesByNamespace(namespace));
                    if (category != null) {
                        Iterator<CustomRecipe> recipeIterator = recipes.iterator();
                        while (recipeIterator.hasNext()) {
                            CustomRecipe recipe = recipeIterator.next();
                            if (recipe instanceof CustomRecipe) {
                                if (!category.isValid(recipe)) {
                                    boolean valid = false;
                                    for (CustomItem item : ((CustomRecipe<RecipeConfig>) recipe).getCustomResults()) {
                                        if (category.isValid(item.getType())) {
                                            valid = true;
                                            break;
                                        }
                                    }
                                    if (!valid) {
                                        recipeIterator.remove();
                                    }
                                }
                            }
                        }
                    }
                    maxPages = recipes.size() / 45 + (recipes.size() % 45 > 0 ? 1 : 0);
                    if (currentPage >= maxPages) {
                        currentPage = 0;
                    }
                    int item = 0;
                    for (int i = 45 * currentPage; item < 45 && i < recipes.size(); i++) {
                        CustomRecipe recipe = recipes.get(i);
                        RecipeListContainerButton button = (RecipeListContainerButton) event.getGuiWindow().getButton("recipe_list.container_" + item);
                        button.setCustomRecipe(event.getGuiHandler(), recipe);
                        event.setButton(9 + item, button);
                        item++;
                    }
                }
            }
            if (currentPage != 0) {
                event.setButton(2, "previous_page");
            }
            if (currentPage + 1 < maxPages) {
                event.setButton(6, "next_page");
            }
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
