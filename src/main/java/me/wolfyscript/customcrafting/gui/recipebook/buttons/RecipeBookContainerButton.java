package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class RecipeBookContainerButton extends Button<CCCache> {

    private final HashMap<GuiHandler<?>, CustomItem> recipes = new HashMap<>();
    private final CustomCrafting customCrafting;

    public RecipeBookContainerButton(int slot, CustomCrafting customCrafting) {
        super("recipe_book.container_" + slot, null);
        this.customCrafting = customCrafting;
    }

    @Override
    public void init(GuiWindow guiWindow) {
    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {
    }

    @Override
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {

    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {

    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        CCCache cache = guiHandler.getCustomCache();
        DataHandler dataHandler = customCrafting.getRecipeHandler();
        KnowledgeBook book = cache.getKnowledgeBook();
        CustomItem customItem = getRecipeItem(guiHandler);
        List<ICustomRecipe<?>> recipes = dataHandler.getAvailableRecipesBySimilarResult(customItem.create(), player);
        if (!recipes.isEmpty()) {
            book.setSubFolderPage(0);
            book.addResearchItem(customItem);
            book.setSubFolderRecipes(customItem, recipes);
            book.applyRecipeToButtons(guiHandler, recipes.get(0));
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        inventory.setItem(slot, getRecipeItem(guiHandler).create(1));
    }

    public CustomItem getRecipeItem(GuiHandler<CCCache> guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipeItem(GuiHandler<CCCache> guiHandler, CustomItem item) {
        recipes.put(guiHandler, item);
    }
}
