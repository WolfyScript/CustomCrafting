package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class MainCategoryButton extends Button {

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
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        if (category != null) {
            ItemStack itemStack = new ItemStack(category.getIcon());
            ItemMeta itemMeta = itemStack.getItemMeta();

            LanguageAPI languageAPI = WolfyUtilities.getAPI(customCrafting).getLanguageAPI();

            itemMeta.setDisplayName(languageAPI.replaceColoredKeys(category.getName()));
            itemMeta.setLore(languageAPI.replaceColoredKeys(category.getDescription()));

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(slot, itemStack);
        }
    }

    @Override
    public void init(GuiWindow guiWindow) {

    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {

    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        if (category != null) {
            KnowledgeBook knowledgeBook = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
            knowledgeBook.setCategory(category);
            knowledgeBook.setRecipeItems(new ArrayList<>());
            guiHandler.changeToInv("recipe_book");
        }
        return true;
    }
}
