package me.wolfyscript.customcrafting.gui.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Items;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;

public class ItemEditor extends ExtendedGuiWindow {

    public ItemEditor(InventoryAPI inventoryAPI) {
        super("item_editor", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("load_item", Material.ITEM_FRAME);
        createItem("create_item", Material.ITEM_FRAME);
        createItem("edit_item", Material.REDSTONE);
        createItem("delete_item", Material.BARRIER);
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            if (!cache.getItems().getType().equals("items")) {
                event.setItem(20, "load_item");
                event.setItem(22, "create_item");
                event.setItem(24, "edit_item");
            } else {
                event.setItem(20, "create_item");
                event.setItem(22, "edit_item");
                event.setItem(24, "delete_item");
                event.setItem(31, "load_item");
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        if (!super.onAction(guiAction)) {
            String action = guiAction.getAction();
            if (action.equals("back")) {
                guiAction.getGuiHandler().openLastInv();
            } else {
                PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
                Items items = cache.getItems();
                switch (action) {
                    case "edit_item":
                        if (!items.getType().equals("items")) {
                            if (items.isSaved()) {
                                items.setItem(CustomCrafting.getRecipeHandler().getCustomItem(items.getId()));
                                guiAction.getGuiHandler().changeToInv("item_creator");
                            } else {
                                if (items.getType().equals("result") || items.getType().equals("ingredient")) {
                                    guiAction.getGuiHandler().changeToInv("item_creator");
                                }
                            }
                        } else {
                            runChat(2, "$msg.gui.item_editor.input$", guiAction.getGuiHandler());
                        }
                        break;
                    case "delete_item":
                        runChat(0, "$msg.gui.item_editor.input$", guiAction.getGuiHandler());
                        break;
                    case "load_item":
                        runChat(1, "$msg.gui.item_editor.input$", guiAction.getGuiHandler());
                        break;
                    case "create_item":
                        guiAction.getGuiHandler().changeToInv("item_creator");
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        return false;
    }

    @Override
    public boolean parseChatMessage(int id, String message, GuiHandler guiHandler) {
        String[] args = message.split(" ");
        Player player = guiHandler.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(guiHandler.getPlayer());
        Items items = cache.getItems();
        if (args.length > 1) {
            CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(args[0], args[1]);
            if (customItem == null) {
                api.sendPlayerMessage(guiHandler.getPlayer(), "$msg.gui.item_editor.error$");
                return true;
            }
            switch (id) {
                case 0:
                    CustomCrafting.getRecipeHandler().removeCustomItem(customItem);
                    customItem.getConfig().getConfigFile().deleteOnExit();
                    break;
                case 1:
                    if (items.getType().equals("items")) {
                        Inventory inv = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&', api.getLanguageAPI().getActiveLanguage().replaceKeys("$msg.gui.item_editor.item_gui_title$")));
                        inv.setItem(2, customItem.getIDItem());
                        inv.setItem(6, customItem);
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> player.openInventory(inv), 2);
                        player.openInventory(inv);
                    } else {
                        ItemUtils.applyItem(customItem, cache);
                        api.sendPlayerMessage(guiHandler.getPlayer(), "$msg.gui.item_editor.item_applied$");
                        guiHandler.changeToInv("recipe_creator");
                    }
                    break;
                case 2:
                    items.setItem("items", customItem);
                    api.sendPlayerMessage(guiHandler.getPlayer(), "$msg.gui.item_editor.item_editable$");
                    Bukkit.getScheduler().runTask(api.getPlugin(), () -> guiHandler.changeToInv("item_creator"));
                    break;
            }
        } else {
            api.sendPlayerMessage(guiHandler.getPlayer(), "$msg.gui.item_editor.no_name$");
            return true;
        }
        return false;
    }
}
