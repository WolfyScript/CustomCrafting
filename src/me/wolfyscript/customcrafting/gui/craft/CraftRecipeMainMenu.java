package me.wolfyscript.customcrafting.gui.craft;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.event.EventHandler;

public class CraftRecipeMainMenu extends GuiWindow {

    public CraftRecipeMainMenu(InventoryAPI inventoryAPI) {
        super("recipe_mainmenu", inventoryAPI, 54);
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
        String action = guiAction.getAction();
        if(action.equals("recipe_list")){
            guiAction.getGuiHandler().changeToInv("recipe_list");
        }else if(action.equals("create_recipe")){
            guiAction.getGuiHandler().changeToInv("craft_editor_main");
        }
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {

        return true;
    }
}
