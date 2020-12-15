package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.elite_crafting.*;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

import java.util.ArrayList;

public class EliteCraftingCluster extends CCCluster {

    public EliteCraftingCluster(InventoryAPI<TestCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "crafting", customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new CraftingWindow3(this, customCrafting));
        registerGuiWindow(new CraftingWindow4(this, customCrafting));
        registerGuiWindow(new CraftingWindow5(this, customCrafting));
        registerGuiWindow(new CraftingWindow6(this, customCrafting));
        registerGuiWindow(new CraftingRecipeBook(this, customCrafting));
        setEntry(new NamespacedKey("crafting", "crafting_3"));
        registerButton(new ActionButton("knowledge_book", new ButtonState("crafting", "knowledge_book", Material.KNOWLEDGE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            knowledgeBook.setRecipeItems(new ArrayList<>());
            knowledgeBook.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            knowledgeBook.setRecipeItems(new ArrayList<>());
            guiHandler.changeToInv("recipe_book");
            return true;
        })));
    }
}
