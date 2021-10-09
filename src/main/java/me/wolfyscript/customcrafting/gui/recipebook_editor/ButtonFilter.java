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

    ButtonFilter(CategoryFilter filter, CustomCrafting customCrafting) {
        super("filter_" + filter.getId(), new ButtonState<>("filter", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent) {
                var recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
                var recipeBook = customCrafting.getConfigHandler().getRecipeBookConfig();
                if (clickEvent.isRightClick() && clickEvent.isShiftClick()) {
                    //Delete Filter
                    recipeBook.getCategories().removeFilter(filter.getId());
                    return true;
                } else if (clickEvent.isLeftClick()) {
                    //Edit Category
                    recipeBookEditor.setCategoryID(filter.getId());
                    recipeBookEditor.setFilter(new CategoryFilter(filter));
                    guiHandler.openWindow("filter");
                    return true;
                }
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            itemStack.setType(filter.getIcon());
            values.put("%name%", filter.getName());
            values.put("%description%", filter.getDescription());
            return itemStack;
        }));
    }
}
