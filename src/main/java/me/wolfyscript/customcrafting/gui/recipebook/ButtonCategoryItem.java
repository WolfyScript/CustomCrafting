package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;

class ButtonCategoryItem extends Button<CCCache> {

    private final CustomCrafting customCrafting;
    private final Categories categories;
    private final HashMap<GuiHandler<CCCache>, Integer> categoryMap;

    ButtonCategoryItem(CustomCrafting customCrafting) {
        super(ClusterRecipeBook.ITEM_CATEGORY.getKey(), ButtonType.NORMAL);
        this.customCrafting = customCrafting;
        this.categories = customCrafting.getDataHandler().getCategories();
        this.categoryMap = new HashMap<>();
    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        if (event instanceof InventoryClickEvent clickEvent) {
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            if (!categories.getSortedFilters().isEmpty()) {
                int currentIndex = categoryMap.getOrDefault(guiHandler, 0);
                if (clickEvent.isLeftClick()) {
                    categoryMap.put(guiHandler, currentIndex < categories.getSortedFilters().size() - 1 ? currentIndex + 1 : 0);
                } else {
                    categoryMap.put(guiHandler, currentIndex > 0 ? currentIndex - 1 : categories.getSortedFilters().size() - 1);
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        CategoryFilter category = categories.getFilter(categoryMap.getOrDefault(guiHandler, 0));
        if (category != null) {
            inventory.setItem(slot, category.createItemStack(customCrafting));
        }
    }

    @Override
    public void init(GuiWindow guiWindow) {

    }

    @Override
    public void init(GuiCluster<CCCache> guiCluster) {

    }

    @Override
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {

    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {

    }

    @Nullable
    public CategoryFilter getFilter(GuiHandler<CCCache> guiHandler) {
        return categories.getFilter(categoryMap.getOrDefault(guiHandler, 0));
    }
}
