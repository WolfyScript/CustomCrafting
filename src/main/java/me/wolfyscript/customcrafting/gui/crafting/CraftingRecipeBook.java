package me.wolfyscript.customcrafting.gui.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.utils.ItemCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CraftingRecipeBook extends ExtendedGuiWindow {

    public CraftingRecipeBook(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("recipe_book", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getSkullViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            book.setRecipeItems(new ArrayList<>());
            if (book.getSubFolder() == 0) {
                guiHandler.openPreviousInv();
            } else {
                book.stopTimerTask();
                book.getResearchItems().remove(book.getSubFolder() - 1);
                book.setSubFolder(book.getSubFolder() - 1);
                if (book.getSubFolder() > 0) {
                    CustomItem item = book.getResearchItem();
                    book.setSubFolderRecipes(CustomCrafting.getRecipeHandler().getRecipes(item));
                    book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(0));
                    return true;
                } else {
                    book.setSubFolderRecipes(new ArrayList<>());
                }
            }
            return true;
        })));
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i));
        }
        registerButton(new DummyButton("workbench.shapeless_on", new ButtonState("workbench.shapeless_on", WolfyUtilities.getSkullViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"))));
        registerButton(new DummyButton("workbench.shapeless_off", new ButtonState("workbench.shapeless_off", WolfyUtilities.getSkullViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"))));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            GuiHandler<TestCache> guiHandler = event.getGuiHandler();
            Player player = event.getPlayer();
            TestCache cache = guiHandler.getCustomCache();
            PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(player);
            EliteWorkbench eliteWorkbenchData = cache.getEliteWorkbench();
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            ItemCategory itemCategory = knowledgeBook.getItemCategory();
            ((ItemCategoryButton) api.getInventoryAPI().getGuiCluster("recipe_book").getButton("itemCategory")).setState(event.getGuiHandler(), itemCategory);
            if (knowledgeBook.getSubFolder() == 0) {
                event.setButton(0, "back");
                event.setButton(2, "recipe_book", "previous_page");
                event.setButton(4, "recipe_book", "itemCategory");
                event.setButton(6, "recipe_book", "next_page");
                if (knowledgeBook.getRecipeItems().isEmpty()) {
                    List<CustomRecipe> recipes = new ArrayList<>();

                    recipes.addAll(CustomCrafting.getRecipeHandler().getAvailableEliteCraftingRecipes(player));

                    Iterator<CustomRecipe> iterator = recipes.iterator();
                    while (iterator.hasNext()) {
                        EliteCraftingRecipe recipe = (EliteCraftingRecipe) iterator.next();
                        if (!recipe.getConditions().getByID("elite_workbench").getOption().equals(Conditions.Option.IGNORE)) {
                            if (!((EliteWorkbenchCondition) recipe.getConditions().getByID("elite_workbench")).getEliteWorkbenches().contains(eliteWorkbenchData.getEliteWorkbenchData().getId())) {
                                iterator.remove();
                                continue;
                            }
                        }
                        if (recipe.isShapeless()) {
                            if (recipe.getIngredients().size() > eliteWorkbenchData.getCurrentGridSize() * eliteWorkbenchData.getCurrentGridSize()) {
                                iterator.remove();
                            }
                        } else {
                            ShapedEliteCraftRecipe recipe1 = (ShapedEliteCraftRecipe) recipe;
                            if (recipe1.getShape().length > eliteWorkbenchData.getCurrentGridSize() || recipe1.getShape()[0].length() > eliteWorkbenchData.getCurrentGridSize()) {
                                iterator.remove();
                            }
                        }
                    }
                    if (knowledgeBook.getSetting().equals(Setting.WORKBENCH)) {
                        recipes.addAll(CustomCrafting.getRecipeHandler().getAvailableAdvancedCraftingRecipes(player));
                    }
                    if (!itemCategory.equals(ItemCategory.SEARCH)) {
                        Iterator<CustomRecipe> recipeIterator = recipes.iterator();
                        while (recipeIterator.hasNext()) {
                            CustomRecipe recipe = recipeIterator.next();
                            List<CustomItem> customItems = recipe.getCustomResults();
                            boolean allowed = false;
                            for (CustomItem customItem : customItems) {
                                if (itemCategory.isValid(customItem.getType())) {
                                    allowed = true;
                                }
                            }
                            if (!allowed) {
                                recipeIterator.remove();
                            }
                        }
                    }
                    List<CustomItem> recipeItems = new ArrayList<>();
                    recipes.stream().map(recipe -> (List<CustomItem>) recipe.getCustomResults()).forEach(items -> recipeItems.addAll(items.stream().filter(item -> !recipeItems.contains(item)).collect(Collectors.toList())));
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
                event.setButton(0, "back");
                int maxPages = recipes.size();
                if (knowledgeBook.getSubFolderPage() >= maxPages) {
                    knowledgeBook.setSubFolderPage(0);
                }
                if (knowledgeBook.getSubFolderPage() > 0) {
                    event.setButton(48, "previous_recipe");
                }
                if (knowledgeBook.getSubFolderPage() + 1 < recipes.size()) {
                    event.setButton(50, "next_recipe");
                }
                CustomRecipe customRecipe = recipes.get(knowledgeBook.getSubFolderPage());
                customRecipe.renderMenu(this, event);
            }
        }
    }
}
