package me.wolfyscript.customcrafting.gui.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;

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
}
