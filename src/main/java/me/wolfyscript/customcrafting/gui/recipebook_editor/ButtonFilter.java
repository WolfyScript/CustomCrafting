package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.CategoryFilter;
import me.wolfyscript.customcrafting.configs.recipebook.CategorySettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

class ButtonFilter extends ActionButton<CCCache> {

    ButtonFilter(String id, CustomCrafting customCrafting) {
        super("filter_" + id, new ButtonState<>("filter", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            if (!id.isEmpty() && event instanceof InventoryClickEvent clickEvent) {
                var recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
                var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
                if (clickEvent.isRightClick() && clickEvent.isShiftClick()) {
                    //Delete Filter
                    recipeBook.getCategories().removeFilter(id);
                    return true;
                } else if (clickEvent.isLeftClick()) {
                    //Edit Category
                    CategoryFilter category = recipeBook.getCategories().getFilter(id);
                    recipeBookEditor.setCategoryID(id);
                    recipeBookEditor.setFilter(new CategoryFilter(category));
                    guiHandler.openWindow("filter");
                    return true;
                }
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CategorySettings category = customCrafting.getConfigHandler().getRecipeBookConfig().getCategories().getFilter(id);
            itemStack.setType(category.getIcon());
            values.put("%name%", category.getName());
            values.put("%description%", category.getDescription());
            return itemStack;
        }));
    }
}
