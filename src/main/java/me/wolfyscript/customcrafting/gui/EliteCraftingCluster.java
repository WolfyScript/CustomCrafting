package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.elite_crafting.CraftingWindow3;
import me.wolfyscript.customcrafting.gui.elite_crafting.CraftingWindow4;
import me.wolfyscript.customcrafting.gui.elite_crafting.CraftingWindow5;
import me.wolfyscript.customcrafting.gui.elite_crafting.CraftingWindow6;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

public class EliteCraftingCluster extends CCCluster {

    public static final String KEY = "crafting";
    public static final NamespacedKey RECIPE_BOOK = new NamespacedKey(KEY, "recipe_book");

    public EliteCraftingCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new CraftingWindow3(this, customCrafting));
        registerGuiWindow(new CraftingWindow4(this, customCrafting));
        registerGuiWindow(new CraftingWindow5(this, customCrafting));
        registerGuiWindow(new CraftingWindow6(this, customCrafting));
        setEntry(new NamespacedKey(KEY, "crafting_3"));

        registerButton(new ActionButton<>(RECIPE_BOOK.getKey(), Material.KNOWLEDGE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            ButtonContainerIngredient.resetButtons(guiHandler);
            cache.getKnowledgeBook().setEliteCraftingTable(cache.getEliteWorkbench());
            guiHandler.openCluster("recipe_book");
            return true;
        }));
    }
}
