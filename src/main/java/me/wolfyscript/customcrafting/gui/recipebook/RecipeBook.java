package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeBook extends ExtendedGuiWindow {

    public RecipeBook(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("recipe_book", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i, customCrafting));
        }
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getSkullViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (guiHandler, player, inventory, slot, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            book.setRecipeItems(new ArrayList<>());
            if (book.getSubFolder() == 0) {
                guiHandler.openPreviousInv();
            } else {
                book.getResearchItems().remove(book.getSubFolder() - 1);
                book.setSubFolder(book.getSubFolder() - 1);
                if (book.getSubFolder() > 0) {
                    CustomItem item = book.getResearchItem();
                    book.setSubFolderRecipes(customCrafting.getRecipeHandler().getRecipes(item));
                    book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(0));
                    return true;
                } else {
                    book.setSubFolderRecipes(new ArrayList<>());
                }
            }
            return true;
        })));

        registerButton(new ActionButton("next_recipe", new ButtonState("next_recipe", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                TestCache cache = (TestCache) guiHandler.getCustomCache();
                KnowledgeBook book = cache.getKnowledgeBook();
                book.stopTimerTask();
                IngredientContainerButton.resetButtons(guiHandler);
                int nextPage = book.getSubFolderPage() + 1;
                if (nextPage < book.getSubFolderRecipes().size()) {
                    book.setSubFolderPage(nextPage);
                    book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(nextPage));
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> values, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                KnowledgeBook book = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
                values.put("%page%", book.getSubFolderPage() + 1);
                values.put("%max_pages%", book.getSubFolderRecipes().size());
                return itemStack;
            }
        })));
        registerButton(new ActionButton("previous_recipe", new ButtonState("previous_recipe", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                TestCache cache = (TestCache) guiHandler.getCustomCache();
                KnowledgeBook book = cache.getKnowledgeBook();
                book.stopTimerTask();
                IngredientContainerButton.resetButtons(guiHandler);
                if (book.getSubFolderPage() > 0) {
                    book.setSubFolderPage(book.getSubFolderPage() - 1);
                    book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(book.getSubFolderPage()));
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> values, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                KnowledgeBook book = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
                values.put("%page%", book.getSubFolderPage() + 1);
                values.put("%max_pages%", book.getSubFolderRecipes().size());
                return itemStack;
            }
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
            Player player = event.getPlayer();
            PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(player);
            KnowledgeBook knowledgeBook = ((TestCache) event.getGuiHandler().getCustomCache()).getKnowledgeBook();

            Category category = knowledgeBook.getCategory();
            Category switchCategory = ((ItemCategoryButton) event.getInventoryAPI().getGuiCluster("recipe_book").getButton("itemCategory")).getCategory(event.getGuiHandler());

            for (int i = 1; i < 9; i++) {
                event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
            }
            if (knowledgeBook.getSubFolder() == 0) {
                event.setButton(0, "back");
                event.setButton(4, "recipe_book", "itemCategory");
                if (knowledgeBook.getRecipeItems().isEmpty()) {
                    List<CustomRecipe> recipes = new ArrayList<>();
                    recipes.addAll(customCrafting.getRecipeHandler().getAvailableAdvancedCraftingRecipes(player));
                    recipes.addAll(recipeHandler.getAvailableEliteCraftingRecipes(player));
                    recipes.addAll(recipeHandler.getAvailableAnvilRecipes(player));
                    recipes.addAll(recipeHandler.getAvailableStonecutterRecipes());
                    recipes.addAll(recipeHandler.getAvailableCauldronRecipes());
                    recipes.addAll(recipeHandler.getAvailableFurnaceRecipes());
                    recipes.addAll(recipeHandler.getAvailableBlastRecipes());
                    recipes.addAll(recipeHandler.getAvailableSmokerRecipes());
                    recipes.addAll(recipeHandler.getAvailableCampfireRecipes());
                    recipes.addAll(recipeHandler.getAvailableGrindstoneRecipes(player));
                    recipes.addAll(recipeHandler.getAvailableBrewingRecipes(player));

                    if (switchCategory != null) {
                        Iterator<CustomRecipe> recipeIterator = recipes.iterator();
                        while (recipeIterator.hasNext()) {
                            CustomRecipe customRecipe = recipeIterator.next();
                            List<CustomItem> items = new ArrayList<>();
                            if (customRecipe instanceof CustomAnvilRecipe) {
                                CustomAnvilRecipe anvilRecipe = (CustomAnvilRecipe) customRecipe;
                                if (!anvilRecipe.getInputLeft().isEmpty()) {
                                    items.addAll(anvilRecipe.getInputLeft());
                                } else if (!anvilRecipe.getInputRight().isEmpty()) {
                                    items.addAll(anvilRecipe.getInputRight());
                                } else if (!anvilRecipe.getCustomResults().isEmpty()) {
                                    items.addAll(anvilRecipe.getCustomResults());
                                }
                            } else {
                                items.addAll(((CustomRecipe<RecipeConfig>) customRecipe).getCustomResults());
                            }
                            if (category != null) {
                                if (!category.isValid(customRecipe) && items.stream().noneMatch(customItem -> category.isValid(customItem.getType()))) {
                                    recipeIterator.remove();
                                    continue;
                                }
                            }
                            if (!switchCategory.isValid(customRecipe) && items.stream().noneMatch(customItem -> switchCategory.isValid(customItem.getType()))) {
                                recipeIterator.remove();
                            }
                        }
                    }

                    List<CustomItem> recipeItems = new ArrayList<>();
                    recipes.stream().map(recipe -> {
                        if (recipe instanceof CustomAnvilRecipe) {
                            if (((CustomAnvilRecipe) recipe).getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                                return (List<CustomItem>) recipe.getCustomResults();
                            } else if (((CustomAnvilRecipe) recipe).hasInputLeft()) {
                                return ((CustomAnvilRecipe) recipe).getInputLeft();
                            }
                            return ((CustomAnvilRecipe) recipe).getInputRight();
                        }
                        return (List<CustomItem>) recipe.getCustomResults();
                    }).forEach(items -> recipeItems.addAll(items.stream().filter(item -> !recipeItems.contains(item)).collect(Collectors.toList())));
                    knowledgeBook.setRecipeItems(recipeItems);
                }

                List<CustomItem> recipeItems = knowledgeBook.getRecipeItems();
                int maxPages = recipeItems.size() / 45 + (recipeItems.size() % 45 > 0 ? 1 : 0);
                if (knowledgeBook.getPage() >= maxPages) {
                    knowledgeBook.setPage(0);
                }
                if (knowledgeBook.getPage() != 0) {
                    event.setButton(2, "recipe_book", "previous_page");
                }
                if (knowledgeBook.getPage() + 1 < maxPages) {
                    event.setButton(6, "recipe_book", "next_page");
                }
                int item = 0;
                for (int i = 45 * knowledgeBook.getPage(); item < 45 && i < recipeItems.size(); i++) {
                    RecipeBookContainerButton button = (RecipeBookContainerButton) getButton("recipe_book.container_" + item);
                    button.setRecipeItem(event.getGuiHandler(), recipeItems.get(i));
                    event.setButton(9 + item, button);
                    item++;
                }
            } else {
                List<CustomRecipe> recipes = knowledgeBook.getSubFolderRecipes();
                for (int i = 1; i < 9; i++) {
                    event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                }
                for (int i = 36; i < 45; i++) {
                    event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                }
                event.setButton(0, "back");
                int maxPages = recipes.size();
                if (knowledgeBook.getSubFolderPage() >= maxPages) {
                    knowledgeBook.setSubFolderPage(0);
                }

                CustomRecipe customRecipe = recipes.get(knowledgeBook.getSubFolderPage());

                if (customRecipe instanceof EliteCraftingRecipe) {
                    if (knowledgeBook.getSubFolderPage() > 0) {
                        event.setButton(51, "previous_recipe");
                    }
                    event.setButton(52, "recipe_book", "back_to_list");
                    if (knowledgeBook.getSubFolderPage() + 1 < recipes.size()) {
                        event.setButton(53, "next_recipe");
                    }
                } else {
                    if (knowledgeBook.getSubFolderPage() > 0) {
                        event.setButton(48, "previous_recipe");
                    }
                    event.setButton(49, "recipe_book", "back_to_list");
                    if (knowledgeBook.getSubFolderPage() + 1 < recipes.size()) {
                        event.setButton(50, "next_recipe");
                    }
                }
                customRecipe.renderMenu(this, event);
            }
        }
    }
}
