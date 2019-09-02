package me.wolfyscript.customcrafting.gui.main_gui.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Items;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
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
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
        registerButton(new ActionButton("load_item", new ButtonState("load_item", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            cache.getChatLists().setCurrentPageItems(1);
            api.sendActionMessage(player, new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendItemListExpanded(player1), true), new ClickData(" Item List", null));
            openChat(guiHandler, "$msg.gui.item_editor.input$", (guiHandler1, player1, s, args) -> {
                PlayerCache cache1 = CustomCrafting.getPlayerCache(player1);
                if (args.length > 1) {
                    Items items = CustomCrafting.getPlayerCache(player1).getItems();
                    CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(args[0], args[1], false);
                    if (customItem == null) {
                        api.sendPlayerMessage(guiHandler.getPlayer(), "$msg.gui.item_editor.error$");
                        return true;
                    }
                    CustomCrafting.getPlayerCache(player).getChatLists().setLastUsedItem(customItem.getId());
                    if (items.getType().equals("items")) {
                        Inventory inv = Bukkit.createInventory(player1, 9, ChatColor.translateAlternateColorCodes('&', api.getLanguageAPI().getActiveLanguage().replaceKeys("$msg.gui.item_editor.item_gui_title$")));
                        inv.setItem(2, customItem.getIDItem());
                        inv.setItem(6, customItem);
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> player1.openInventory(inv), 2);
                        Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> player1.openInventory(inv));
                    } else {
                        //TODO: NEW VARIANT SYSTEM
                        if (cache.getItems().getType().equals("variant")) {
                            //Set values to variant cache
                            cache.getVariantsData().putVariant(cache.getItems().getVariantSlot(), customItem);
                            api.sendPlayerMessage(player1, "$msg.gui.item_editor.item_applied$");
                            guiHandler.openPreviousInv();
                        }
                    }
                    return false;
                }
                api.sendPlayerMessage(player1, "$msg.gui.item_editor.no_name$");
                return true;
            });
            return true;
        })));
        registerButton(new ActionButton("create_item", new ButtonState("create_item", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("item_creator");
            return true;
        })));
        registerButton(new ActionButton("edit_item", new ButtonState("edit_item", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Items items = CustomCrafting.getPlayerCache(player).getItems();
            if (!items.getType().equals("items")) {
                if (items.isSaved()) {
                    items.setItem(CustomCrafting.getRecipeHandler().getCustomItem(items.getId()));
                    guiHandler.changeToInv("item_creator");
                } else {
                    if (items.getType().equals("result") || items.getType().equals("ingredient")) {
                        guiHandler.changeToInv("item_creator");
                    }
                }
            } else {
                CustomCrafting.getPlayerCache(player).getChatLists().setCurrentPageItems(1);
                api.sendActionMessage(player, new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendItemListExpanded(player1), true), new ClickData(" Item List", null));
                openChat(guiHandler, "$msg.gui.item_editor.input$", (guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        Items items1 = CustomCrafting.getPlayerCache(player1).getItems();
                        CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(args[0], args[1], false);
                        if (customItem == null) {
                            api.sendPlayerMessage(guiHandler.getPlayer(), "$msg.gui.item_editor.error$");
                            return true;
                        }
                        CustomCrafting.getPlayerCache(player).getChatLists().setLastUsedItem(customItem.getId());
                        items1.setItem("items", customItem);
                        api.sendPlayerMessage(guiHandler1.getPlayer(), "$msg.gui.item_editor.item_editable$");
                        Bukkit.getScheduler().runTask(api.getPlugin(), () -> guiHandler1.changeToInv("item_creator"));
                        return false;
                    }
                    api.sendPlayerMessage(player1, "$msg.gui.item_editor.no_name$");
                    return true;
                });
            }
            return true;
        })));
        registerButton(new ActionButton("delete_item", new ButtonState("delete_item", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getChatLists().setCurrentPageItems(1);
            api.sendActionMessage(player, new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendItemListExpanded(player1), true), new ClickData(" Item List", null));
            openChat(guiHandler, "$msg.gui.item_editor.input$", (guiHandler1, player1, s, args) -> {
                if (args.length > 1) {
                    CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(args[0], args[1], false);
                    if (customItem == null) {
                        api.sendPlayerMessage(guiHandler1.getPlayer(), "$msg.gui.item_editor.error$");
                        return true;
                    }
                    CustomCrafting.getPlayerCache(player1).getChatLists().setLastUsedItem(customItem.getId());
                    CustomCrafting.getRecipeHandler().removeCustomItem(customItem);
                    if (CustomCrafting.hasDataBaseHandler()) {
                        CustomCrafting.getDataBaseHandler().removeItem(customItem.getConfig().getFolder(), customItem.getConfig().getName());
                    } else {
                        customItem.getConfig().getConfigFile().deleteOnExit();
                    }
                    return false;
                }
                api.sendPlayerMessage(player1, "$msg.gui.item_editor.no_name$");
                return true;
            });
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            if (!cache.getItems().getType().equals("items")) {
                event.setButton(20, "load_item");
                event.setButton(22, "create_item");
                event.setButton(24, "edit_item");
            } else {
                event.setButton(20, "create_item");
                event.setButton(22, "edit_item");
                event.setButton(24, "delete_item");
                event.setButton(31, "load_item");
            }
        }
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
            api.sendActionMessage(player, new ClickData((i % 2 == 1 ? "§3" : "§7") + " - ", null), new ClickData(customItem.getId(), null, new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, customItem.getId().split(":")[0] + " " + customItem.getId().split(":")[1]), new HoverEvent(customItem)));
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
        if (!cache.getChatLists().getLastUsedItem().equals("")) {
            api.sendPlayerMessage(player, "§ePreviously used:");
            CustomItem customItem = recipeHdlr.getCustomItem(cache.getChatLists().getLastUsedItem());
            if (customItem != null) {
                api.sendActionMessage(player, new ClickData("§b - ", null), new ClickData(customItem.getId(), null, new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, customItem.getId().split(":")[0] + " " + customItem.getId().split(":")[1]), new HoverEvent(customItem)));
            }
        }
        api.sendPlayerMessage(player, "-------------------------------------------------");

        api.sendPlayerMessage(player, "$msg.gui.item_editor.input$");
    }
}
