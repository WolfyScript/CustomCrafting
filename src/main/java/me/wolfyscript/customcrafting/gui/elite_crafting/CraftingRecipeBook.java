package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CraftingRecipeBook extends CCWindow {

    public CraftingRecipeBook(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "recipe_book", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            if (book.getSubFolder() == 0) {
                guiHandler.openPreviousWindow();
            } else {
                book.stopTimerTask();
                book.removePreviousResearchItem();
                if (book.getSubFolder() > 0) {
                    CustomItem item = book.getResearchItem();
                    book.setSubFolderRecipes(item, Registry.RECIPES.get(item));
                    book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(0));
                    return true;
                }
            }
            return true;
        })));
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i, customCrafting));
        }
        registerButton(new DummyButton<>("workbench.shapeless_on", PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813")));
        registerButton(new DummyButton<>("workbench.shapeless_off", PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312")));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        GuiHandler<CCCache> guiHandler = event.getGuiHandler();
        Player player = event.getPlayer();
        CCCache cache = guiHandler.getCustomCache();
        CCPlayerData data = PlayerUtil.getStore(player);
        EliteWorkbench eliteWorkbenchData = cache.getEliteWorkbench();
        KnowledgeBook knowledgeBook = cache.getKnowledgeBook();

        CategorySettings category = ((ItemCategoryButton) api.getInventoryAPI().getGuiCluster("recipe_book").getButton("item_category")).getFilter(guiHandler);
        if (knowledgeBook.getSubFolder() == 0) {
            event.setButton(0, "back");
            event.setButton(2, new NamespacedKey("recipe_book", "previous_page"));
            event.setButton(4, new NamespacedKey("recipe_book", "item_category"));
            event.setButton(6, new NamespacedKey("recipe_book", "next_page"));
            if (knowledgeBook.getEliteRecipeItems(category).isEmpty()) {
                List<ICustomRecipe<?, ?>> recipes = new ArrayList<>(Registry.RECIPES.getAvailable(Types.ELITE_WORKBENCH, player));
                Iterator<ICustomRecipe<?, ?>> iterator = recipes.iterator();
                while (iterator.hasNext()) {
                    EliteCraftingRecipe recipe = (EliteCraftingRecipe) iterator.next();
                    if (!recipe.getConditions().getEliteCraftingTableCondition().getOption().equals(Conditions.Option.IGNORE)) {
                        if (!recipe.getConditions().getEliteCraftingTableCondition().getEliteWorkbenches().contains(eliteWorkbenchData.getEliteWorkbenchData().getNamespacedKey())) {
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
                        if (recipe1.getHeight() > eliteWorkbenchData.getCurrentGridSize() || recipe1.getWidth() > eliteWorkbenchData.getCurrentGridSize()) {
                            iterator.remove();
                        }
                    }
                }
                EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
                if (eliteWorkbench.getEliteWorkbenchData().isAdvancedRecipes()) {
                    recipes.addAll(Registry.RECIPES.getAvailable(Types.WORKBENCH, player));
                }
                if (category != null) {
                    Iterator<ICustomRecipe<?,?>> recipeIterator = recipes.iterator();
                    while (recipeIterator.hasNext()) {
                        ICustomRecipe<?,?> recipe = recipeIterator.next();
                        Result<?> result = recipe.getResult();
                        if (!category.isValid(recipe)) {
                            recipeIterator.remove();
                        }
                    }
                }
                List<CustomItem> recipeItems = new ArrayList<>();
                recipes.stream().map(ICustomRecipe::getResult).forEach(result -> recipeItems.addAll(result.getChoices().stream().filter(item -> !recipeItems.contains(item)).collect(Collectors.toList())));
                knowledgeBook.setEliteRecipeItems(category, recipeItems);
            }

            List<CustomItem> recipeItems = knowledgeBook.getEliteRecipeItems(category);
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
            List<ICustomRecipe<?,?>> recipes = knowledgeBook.getSubFolderRecipes();
            for (int i = 1; i < 9; i++) {
                event.setButton(i, data.getLightBackground());
            }
            for (int i = 36; i < 45; i++) {
                event.setButton(i, data.getLightBackground());
            }
            event.setButton(0, "back");
            int maxPages = recipes.size();
            if (knowledgeBook.getSubFolderPage() >= maxPages) {
                knowledgeBook.setSubFolderPage(0);
            }
            NamespacedKey backToList = new NamespacedKey("recipe_book", "back_to_list");
            ICustomRecipe<?,?> customRecipe = recipes.get(knowledgeBook.getSubFolderPage());
            if (customRecipe instanceof EliteCraftingRecipe) {
                if (knowledgeBook.getSubFolderPage() > 0) {
                    event.setButton(51, "previous_recipe");
                }
                event.setButton(52, backToList);
                if (knowledgeBook.getSubFolderPage() + 1 < recipes.size()) {
                    event.setButton(53, "next_recipe");
                }
            } else {
                if (knowledgeBook.getSubFolderPage() > 0) {
                    event.setButton(48, "previous_recipe");
                }
                event.setButton(49, backToList);
                if (knowledgeBook.getSubFolderPage() + 1 < recipes.size()) {
                    event.setButton(50, "next_recipe");
                }
            }
            customRecipe.renderMenu(this, event);
        }
    }
}
