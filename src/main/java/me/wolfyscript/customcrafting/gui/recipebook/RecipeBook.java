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
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class RecipeBook extends ExtendedGuiWindow {

    private final BukkitTask tickTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
        for (int i = 0; i < 54; i++) {
            Button btn = getButton("ingredient.container_" + i);
            if (btn instanceof IngredientContainerButton) {
                IngredientContainerButton cBtn = (IngredientContainerButton) btn;
                cBtn.getTasks().forEach(runnable -> Bukkit.getScheduler().runTask(customCrafting, runnable));
                cBtn.updateTasks();
            }
        }
    }, 1, 20);

    public RecipeBook(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("recipe_book", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i, customCrafting));
        }
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (guiHandler, player, inventory, slot, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            if (book.getSubFolder() == 0) {
                book.setRecipeItems(new ArrayList<>());
                guiHandler.openPreviousInv();
            } else {
                book.getResearchItems().remove(book.getSubFolder() - 1);
                book.setSubFolder(book.getSubFolder() - 1);
                if (book.getSubFolder() > 0) {
                    CustomItem item = book.getResearchItem();
                    book.setSubFolderRecipes(customCrafting.getRecipeHandler().getRecipes(item));
                    if (book.getSubFolderRecipes().size() > 0) {
                        book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(0));
                    }
                    return true;
                } else {
                    book.setSubFolderRecipes(new ArrayList<>());
                }
            }
            return true;
        })));

        registerButton(new ActionButton("next_recipe", new ButtonState("next_recipe", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"),(guiHandler, player, inventory, i, inventoryClickEvent) -> {
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
        },(values, guiHandler, player, itemStack, i, b) -> {
            KnowledgeBook book = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
            values.put("%page%", book.getSubFolderPage() + 1);
            values.put("%max_pages%", book.getSubFolderRecipes().size());
            return itemStack;
        })));
        registerButton(new ActionButton("previous_recipe", new ButtonState("previous_recipe", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            if (book.getSubFolderPage() > 0) {
                book.setSubFolderPage(book.getSubFolderPage() - 1);
                book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(book.getSubFolderPage()));
            }
            return true;
        }, (values, guiHandler, player, itemStack, i, b) -> {
            KnowledgeBook book = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
            values.put("%page%", book.getSubFolderPage() + 1);
            values.put("%max_pages%", book.getSubFolderRecipes().size());
            return itemStack;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        super.onUpdateAsync(event);
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
            if (recipeHandler.getCategories().getSortedMainCategories().size() > 1) {
                event.setButton(0, "back");
            }
            event.setButton(4, "recipe_book", "itemCategory");

            if (knowledgeBook.getRecipeItems().isEmpty()) {
                List<CustomItem> recipeItems = new ArrayList<>();
                recipeHandler.getAvailableRecipes(player).stream().filter(customRecipe -> {
                    if (switchCategory != null) {
                        List<CustomItem> items;
                        if (customRecipe instanceof CustomAnvilRecipe) {
                            items = ((CustomAnvilRecipe) customRecipe).getMode().equals(CustomAnvilRecipe.Mode.RESULT) ? customRecipe.getResults() : ((CustomAnvilRecipe) customRecipe).hasInputLeft() ? ((CustomAnvilRecipe) customRecipe).getInputLeft() : ((CustomAnvilRecipe) customRecipe).getInputRight();
                        } else {
                            items = customRecipe.getResults();
                        }
                        if (category != null) {
                            if (!category.isValid(customRecipe) && items.stream().noneMatch(customItem -> category.isValid(customItem.getItemStack().getType()))) {
                                return false;
                            }
                        }
                        return switchCategory.isValid(customRecipe) || items.stream().anyMatch(customItem -> switchCategory.isValid(customItem.getItemStack().getType()));
                    }
                    return true;
                }).map(recipe -> {
                    if (recipe instanceof CustomAnvilRecipe) {
                        return ((CustomAnvilRecipe) recipe).getMode().equals(CustomAnvilRecipe.Mode.RESULT) ? recipe.getResults() : ((CustomAnvilRecipe) recipe).hasInputLeft() ? ((CustomAnvilRecipe) recipe).getInputLeft() : ((CustomAnvilRecipe) recipe).getInputRight();
                    }
                    return recipe.getResults();
                }).forEach(customItems -> customItems.stream().filter(item -> recipeItems.stream().noneMatch(rItem -> rItem.create().isSimilar(item.create()))).forEach(recipeItems::add));
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
            List<ICustomRecipe<?>> recipes = knowledgeBook.getSubFolderRecipes();
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

            if (knowledgeBook.getSubFolderPage() < recipes.size()) {
                ICustomRecipe<?> customRecipe = recipes.get(knowledgeBook.getSubFolderPage());
                if (customRecipe.getRecipeType().equals(RecipeType.ELITE_WORKBENCH)) {
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
