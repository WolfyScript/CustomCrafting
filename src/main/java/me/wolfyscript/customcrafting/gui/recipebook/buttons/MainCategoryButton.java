package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;

public class MainCategoryButton extends Button<TestCache> {

    private final CustomCrafting customCrafting;
    private final Categories categories;

    private final Category category;

    public MainCategoryButton(String categoryId, CustomCrafting customCrafting) {
        super("mainCategory." + categoryId, ButtonType.NORMAL);
        this.customCrafting = customCrafting;
        this.categories = customCrafting.getRecipeHandler().getCategories();
        this.category = categories.getMainCategory(categoryId);
    }

    @Override
    public void render(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        if (category != null) {
            ItemStack itemStack = new ItemStack(category.getIcon());
            ItemMeta itemMeta = itemStack.getItemMeta();

            LanguageAPI languageAPI = WolfyUtilities.get(customCrafting).getLanguageAPI();

            itemMeta.setDisplayName(languageAPI.replaceColoredKeys(category.getName()));
            itemMeta.setLore(languageAPI.replaceColoredKeys(category.getDescription()));

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(slot, itemStack);
        }
    }

    @Override
    public void init(GuiWindow<TestCache> guiWindow) {

    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {

    }

    @Override
    public void postExecute(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int i, InventoryInteractEvent inventoryInteractEvent) throws IOException {

    }

    @Override
    public void prepareRender(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int i, boolean b) {

    }

    @Override
    public boolean execute(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        if (category != null) {
            KnowledgeBook knowledgeBook = guiHandler.getCustomCache().getKnowledgeBook();
            knowledgeBook.setCategory(category);
            knowledgeBook.setRecipeItems(new ArrayList<>());
            guiHandler.changeToInv("recipe_book");
        }
        return true;
    }
}
