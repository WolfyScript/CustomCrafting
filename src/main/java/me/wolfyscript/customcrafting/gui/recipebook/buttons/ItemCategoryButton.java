package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemCategoryButton extends Button<CCCache> {

    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final HashMap<GuiHandler<CCCache>, Integer> categoryMap;

    public ItemCategoryButton(CustomCrafting customCrafting) {
        super("itemCategory", ButtonType.NORMAL);
        this.customCrafting = customCrafting;
        this.categories = customCrafting.getRecipeHandler().getCategories();
        this.categoryMap = new HashMap<>();
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        Category category = categories.getSwitchCategory(categoryMap.getOrDefault(guiHandler, 0));
        if (category != null) {
            ItemStack categoryItem = new ItemStack(category.getIcon());
            ItemMeta itemMeta = categoryItem.getItemMeta();

            LanguageAPI languageAPI = WolfyUtilities.get(customCrafting).getLanguageAPI();

            itemMeta.setDisplayName(languageAPI.replaceColoredKeys(category.getName()));
            itemMeta.setLore(languageAPI.replaceColoredKeys(category.getDescription()));

            categoryItem.setItemMeta(itemMeta);
            inventory.setItem(slot, categoryItem);
        }
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
    public Category getCategory(GuiHandler<CCCache> guiHandler) {
        return categories.getSwitchCategory(categoryMap.getOrDefault(guiHandler, 0));
    }
}
