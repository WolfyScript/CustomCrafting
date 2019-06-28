package me.wolfyscript.customcrafting.gui.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Items;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.inventory.*;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;

public class ItemEditor extends ExtendedGuiWindow {

    public ItemEditor(InventoryAPI inventoryAPI) {
        super("item_editor", inventoryAPI, 45);
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
                            cache.getChatLists().setCurrentPageItems(1);
                            api.sendActionMessage(guiAction.getPlayer(), new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendItemListExpanded(player1), true), new ClickData(" Item List", null));
                            runChat(2, "$msg.gui.item_editor.input$", guiAction.getGuiHandler());
                        }
                        break;
                    case "delete_item":
                        cache.getChatLists().setCurrentPageItems(1);
                        api.sendActionMessage(guiAction.getPlayer(), new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendItemListExpanded(player1), true), new ClickData(" Item List", null));
                        runChat(0, "$msg.gui.item_editor.input$", guiAction.getGuiHandler());
                        break;
                    case "load_item":
                        cache.getChatLists().setCurrentPageItems(1);
                        api.sendActionMessage(guiAction.getPlayer(), new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendItemListExpanded(player1), true), new ClickData(" Item List", null));
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
            CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(args[0], args[1], false);
            if (customItem == null) {
                api.sendPlayerMessage(guiHandler.getPlayer(), "$msg.gui.item_editor.error$");
                return true;
            }
            cache.getChatLists().setLastUsedItem(customItem.getId());
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
                        Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> player.openInventory(inv));
                    } else {
                        CustomItem.applyItem(customItem, cache);
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

    private void sendItemListExpanded(Player player) {
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        RecipeHandler recipeHdlr = CustomCrafting.getRecipeHandler();
        for (int i = 0; i < 20; i++) {
            player.sendMessage(" ");
        }
        api.sendActionMessage(player, new ClickData("§7§n[§c§n-§7§n]", (wolfyUtilities1, p) -> {
            for (int i = 0; i < 20; i++) {
                player.sendMessage(" ");
            }
            api.sendActionMessage(p, new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendItemListExpanded(player1), true), new ClickData(" Item List", null));
            api.sendPlayerMessage(player, "$msg.gui.item_editor.input$");
        }, true), new ClickData("§n Items:", null));

        int currentPage = cache.getChatLists().getCurrentPageItems();
        int itemsPerPage = cache.getChatLists().getLastUsedItem().equals("") ? 15 : 13;
        int maxPages = ((recipeHdlr.getCustomItems().size() % itemsPerPage) > 0 ? 1 : 0) + recipeHdlr.getCustomItems().size() / itemsPerPage;

        for (int i = (currentPage - 1) * itemsPerPage; i < (currentPage - 1) * itemsPerPage + itemsPerPage && i < recipeHdlr.getCustomItems().size(); i++) {
            CustomItem customItem = recipeHdlr.getCustomItems().get(i);
            api.sendActionMessage(player, new ClickData((i % 2 == 1 ? "§3" : "§7") +" - ", null), new ClickData(customItem.getId(), null, new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, customItem.getId().split(":")[0] + " " + customItem.getId().split(":")[1]), new HoverEvent(customItem)));
        }

        api.sendActionMessage(player, new ClickData("§7[§6« previous§7]", (wolfyUtilities1, p) -> {
            if (currentPage > 1) {
                cache.getChatLists().setCurrentPageItems(cache.getChatLists().getCurrentPageItems() - 1);
            }
            sendItemListExpanded(p);
        }), new ClickData("  §b" + currentPage + "§7 / §7" + maxPages + "  ", null), new ClickData("§7[§6next »§7]", (wolfyUtilities1, p) -> {
            if (currentPage < maxPages) {
                cache.getChatLists().setCurrentPageItems(cache.getChatLists().getCurrentPageItems() + 1);
            }
            sendItemListExpanded(p);
        }));
        if(!cache.getChatLists().getLastUsedItem().equals("")){
            api.sendPlayerMessage(player, "§ePreviously used:");
            CustomItem customItem = recipeHdlr.getCustomItem(cache.getChatLists().getLastUsedItem());
            if(customItem != null){
                api.sendActionMessage(player, new ClickData("§b - ", null), new ClickData(customItem.getId(), null, new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, customItem.getId().split(":")[0] + " " + customItem.getId().split(":")[1]), new HoverEvent(customItem)));
            }
        }
        api.sendPlayerMessage(player, "-------------------------------------------------");

        api.sendPlayerMessage(player, "$msg.gui.item_editor.input$");
    }
}
