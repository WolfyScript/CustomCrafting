package me.wolfyscript.customcrafting.gui.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.crafting.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
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

public class CraftingRecipeBook extends ExtendedGuiWindow {

    public CraftingRecipeBook(InventoryAPI inventoryAPI) {
        super("crafting_recipe_book", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getSkullViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            if (book.getCustomRecipe() == null) {
                guiHandler.openPreviousInv();
            } else {
                book.setCustomRecipe(null);
            }
            return true;
        })));
        registerButton(new ActionButton("next_page", new ButtonState("next_page", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            KnowledgeBook book = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
            book.setPage(book.getPage() + 1);
            return true;
        })));
        registerButton(new ActionButton("previous_page", new ButtonState("previous_page", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            KnowledgeBook book = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
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
            Player player = event.getPlayer();

            KnowledgeBook knowledgeBook = CustomCrafting.getPlayerCache(player).getKnowledgeBook();

            ItemCategory itemCategory = knowledgeBook.getItemCategory();

            ((ItemCategoryButton) api.getInventoryAPI().getGuiCluster("recipe_book").getButton("itemCategory")).setState(event.getGuiHandler(), itemCategory);

            event.setButton(0, "back");
            if (knowledgeBook.getCustomRecipe() == null) {
                event.setButton(2, "previous_page");
                event.setButton(4, "recipe_book", "itemCategory");
                event.setButton(6, "next_page");
                List<CustomRecipe> recipes = new ArrayList<>();
                recipes.addAll(CustomCrafting.getRecipeHandler().getAvailableEliteCraftingRecipes(player));
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

                int maxPages = recipes.size() / 45 + (recipes.size() % 45 > 0 ? 1 : 0);
                if (knowledgeBook.getPage() >= maxPages) {
                    knowledgeBook.setPage(0);
                }
                int item = 0;
                for (int i = 45 * knowledgeBook.getPage(); item < 45 && i < recipes.size(); i++) {
                    RecipeBookContainerButton button = (RecipeBookContainerButton) getButton("recipe_book.container_" + item);
                    button.setRecipe(event.getGuiHandler(), recipes.get(i));
                    event.setButton(9 + item, button);
                    item++;
                }
            }


        }
    }
}
