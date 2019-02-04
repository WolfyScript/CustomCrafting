package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class MainMenu extends GuiWindow {

    public MainMenu(InventoryAPI inventoryAPI) {
        super("main_menu", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("craft_recipe", Material.CRAFTING_TABLE);
        createItem("furnace_recipe", Material.FURNACE);
        createItem("item_creator", Material.CHEST);

        createItem("create_recipe", Material.ITEM_FRAME);
        createItem("edit_recipe", Material.REDSTONE);
        createItem("delete_recipe", Material.BARRIER);
        createItem("recipe_list", Material.WRITTEN_BOOK);
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            for (int i = 0; i < 54; i++) {
                event.setItem(i, "glass_gray", true);
            }
            event.setItem(0, "gui_help", true);
            event.setItem(1, "glass_gray", true);
            event.setItem(7, "minimize", true);
            event.setItem(8, "close", true);

            event.setItem(11, "craft_recipe");
            event.setItem(13, "furnace_recipe");
            event.setItem(15, "item_creator");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdateGuis(GuiUpdateEvent event){
        if(event.getWolfyUtilities().equals(CustomCrafting.getApi())){
            for (int i = 0; i < event.getGuiHandler().getCurrentInv().getSize(); i++) {
                event.setItem(i, "glass_gray", true);
            }
            event.setItem(0, "back", true);
            event.setItem(1, "gui_help", true);
            event.setItem(7, "minimize", true);
            event.setItem(8, "close", true);
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        String action = guiAction.getAction();
        PlayerSettings playerSettings = CustomCrafting.getPlayerSettings(guiAction.getPlayer());
        switch (action){
            case "craft_recipe":
                playerSettings.setSetting(Setting.CRAFT_RECIPE);
                guiAction.getGuiHandler().changeToInv("recipe_mainmenu");
                break;
            case "furnace_recipe":
                playerSettings.setSetting(Setting.FURNACE_RECIPE);
                guiAction.getGuiHandler().changeToInv("furnace_mainmenu");
                break;
            case "item_creator":
                playerSettings.setSetting(Setting.ITEMS);
                guiAction.getGuiHandler().changeToInv("item_mainmenu");
                break;
        }
        return true;
    }

}
