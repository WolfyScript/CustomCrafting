package me.wolfyscript.customcrafting.gui.items;

import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.event.EventHandler;

public class ItemCreatorGui extends GuiWindow {

    public ItemCreatorGui(InventoryAPI inventoryAPI) {
        super("item_mainmenu", inventoryAPI, 54);
    }

    @Override
    public void onInit() {

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {

            event.setItem(20, "main_menu", "create_recipe");
            event.setItem(22, "main_menu", "edit_recipe");
            event.setItem(24, "main_menu", "delete_recipe");
            event.setItem(40, "main_menu", "recipe_list");


        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {

        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {

        return true;
    }
}
