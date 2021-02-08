package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.elite_crafting.*;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

public class EliteCraftingCluster extends CCCluster {

    public EliteCraftingCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
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
        registerButton(new ActionButton<>("recipe_book", new ButtonState<>("crafting", "recipe_book", Material.KNOWLEDGE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            knowledgeBook.stopTimerTask();
            IngredientContainerButton.resetButtons(guiHandler);
            guiHandler.openWindow("recipe_book");
            return true;
        })));
    }
}
