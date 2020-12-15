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
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemCategoryButton extends Button<TestCache> {

    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final HashMap<GuiHandler<TestCache>, Integer> categoryMap;

    public ItemCategoryButton(CustomCrafting customCrafting) {
        super("itemCategory", ButtonType.NORMAL);
        this.customCrafting = customCrafting;
        this.categories = customCrafting.getRecipeHandler().getCategories();
        this.categoryMap = new HashMap<>();
    }

    @Override
    public void render(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        Category category = categories.getSwitchCategory(categoryMap.getOrDefault(guiHandler, 0));
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
    public void init(GuiWindow guiWindow) {

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
        if (!categories.getSortedSwitchCategories().isEmpty()) {
            int currentIndex = categoryMap.getOrDefault(guiHandler, 0);
            if (currentIndex < categories.getSortedSwitchCategories().size() - 1) {
                categoryMap.put(guiHandler, currentIndex + 1);
            } else {
                categoryMap.put(guiHandler, 0);
            }
            KnowledgeBook knowledgeBook = guiHandler.getCustomCache().getKnowledgeBook();
            knowledgeBook.setRecipeItems(new ArrayList<>());
        }
        return true;
    }

    @Nullable
    public Category getCategory(GuiHandler<TestCache> guiHandler) {
        return categories.getSwitchCategory(categoryMap.getOrDefault(guiHandler, 0));
    }
}
