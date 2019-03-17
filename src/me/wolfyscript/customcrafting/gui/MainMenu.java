package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class MainMenu extends ExtendedGuiWindow {

    private WolfyUtilities api = CustomCrafting.getApi();

    public MainMenu(InventoryAPI inventoryAPI) {
        super("main_menu", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("craft_recipe", Material.CRAFTING_TABLE);
        createItem("furnace_recipe", Material.FURNACE);

        createItem("item_editor", Material.CHEST);
        createItem("recipe_list", Material.WRITTEN_BOOK);

        createItem("create_recipe", Material.ITEM_FRAME);
        createItem("edit_recipe", Material.REDSTONE);
        createItem("delete_recipe", Material.BARRIER);
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            for (int i = 0; i < 54; i++) {
                event.setItem(i, "glass_gray", true);
            }

            if (event.getGuiHandler().isHelpEnabled()) {
                event.setItem(8, "gui_help_on", true);
            } else {
                event.setItem(8, "gui_help_off", true);
            }

            event.setItem(0, "glass_white", true);

            event.setItem(11, "craft_recipe");
            event.setItem(13, "furnace_recipe");

            event.setItem(39, "item_editor");
            event.setItem(41, "recipe_list");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdateGuis(GuiUpdateEvent event) {
        if (event.getWolfyUtilities().equals(CustomCrafting.getApi())) {
            for (int i = 0; i < event.getGuiHandler().getCurrentInv().getSize(); i++) {
                event.setItem(i, "glass_gray", true);
            }
            event.setItem(0, "back", true);
            if (event.getGuiHandler().getCurrentInv().getSize() > 8) {
                if (event.getGuiHandler().isHelpEnabled()) {
                    event.setItem(8, "gui_help_on", true);
                } else {
                    event.setItem(8, "gui_help_off", true);
                }
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        if (!super.onAction(guiAction)) {
            String action = guiAction.getAction();
            PlayerCache playerCache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
            switch (action) {
                case "item_editor":
                    playerCache.setSetting(Setting.ITEMS);
                    playerCache.getItems().setType("items");
                    playerCache.getItems().setSaved(false);
                    playerCache.getItems().setId("");
                    guiAction.getGuiHandler().changeToInv("item_editor");
                    break;
                case "recipe_list":
                    playerCache.setSetting(Setting.LIST);
                    guiAction.getGuiHandler().changeToInv("recipe_list");
                    break;
                case "craft_recipe":
                    playerCache.setSetting(Setting.CRAFT_RECIPE);
                    guiAction.getGuiHandler().changeToInv("recipe_editor");
                    break;
                case "furnace_recipe":
                    playerCache.setSetting(Setting.FURNACE_RECIPE);
                    guiAction.getGuiHandler().changeToInv("recipe_editor");
                    break;
            }
        }

        return true;
    }

}
