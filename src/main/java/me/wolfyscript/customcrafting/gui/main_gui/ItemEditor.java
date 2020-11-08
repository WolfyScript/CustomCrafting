package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CacheButtonAction;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemEditor extends ExtendedGuiWindow {

    public ItemEditor(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("item_editor", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (CacheButtonAction) (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            if (!cache.getSetting().equals(Setting.ITEMS)) {
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        })));

        registerButton(new ActionButton("load_item", Material.ITEM_FRAME, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            cache.getChatLists().setCurrentPageItems(1);
            sendItemListExpanded(player);
            guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                if (args.length > 1) {
                    NamespacedKey namespacedKey = new NamespacedKey(args[0], args[1]);
                    if (!CustomItems.hasCustomItem(namespacedKey)) {
                        sendMessage(player1, "error");
                        return true;
                    }
                    CustomItem customItem = CustomItems.getCustomItem(namespacedKey);
                    ((TestCache) guiHandler1.getCustomCache()).getChatLists().setLastUsedItem(namespacedKey);

                    if (items.isRecipeItem()) {
                        cache.applyItem(customItem);
                        sendMessage(player1, "item_applied");
                        if (!cache.getSetting().equals(Setting.ITEMS)) {
                            guiHandler.openCluster("recipe_creator");
                        } else {
                            guiHandler.openPreviousInv();
                        }
                        return false;
                    }
                    ItemStack itemStack = customItem.create();
                    if (InventoryUtils.hasInventorySpace(player1, itemStack)) {
                        player1.getInventory().addItem(itemStack);
                        api.sendPlayerMessage(player1, "$commands.give.success$", new String[]{"%PLAYER%", player1.getDisplayName()}, new String[]{"%ITEM%", namespacedKey.toString()});
                    } else {
                        api.sendPlayerMessage(player1, "$commands.give.no_inv_space$");
                    }
                    return false;
                }
                sendMessage(player1, "no_name");
                return true;
            });
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }));
        registerButton(new ActionButton("create_item", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("item_creator", "main_menu");
            return true;
        }));
        registerButton(new ActionButton("edit_item", Material.REDSTONE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (items.isRecipeItem()) {
                if (items.isSaved()) {
                    items.setItem(CustomItems.getCustomItem(items.getNamespacedKey()));
                }
                guiHandler.changeToInv("item_creator", "main_menu");
            } else {
                cache.getChatLists().setCurrentPageItems(1);
                sendItemListExpanded(player);
                guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        NamespacedKey namespacedKey = new NamespacedKey(args[0], args[1]);
                        if (!CustomItems.hasCustomItem(namespacedKey)) {
                            sendMessage(player1, "error");
                            return true;
                        }
                        cache.getChatLists().setLastUsedItem(namespacedKey);
                        items.setItem(false, CustomItems.getCustomItem(namespacedKey));
                        sendMessage(player1, "item_editable");
                        Bukkit.getScheduler().runTask(api.getPlugin(), () -> guiHandler1.changeToInv("item_creator", "main_menu"));
                        return false;
                    }
                    sendMessage(player1, "no_name");
                    return true;
                });
                Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            }
            return true;
        }));
        registerButton(new ActionButton("delete_item", Material.BARRIER, (CacheButtonAction) (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            cache.getChatLists().setCurrentPageItems(1);
            sendItemListExpanded(player);
            guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                if (args.length > 1) {
                    NamespacedKey namespacedKey = new NamespacedKey(args[0], args[1]);
                    return !customCrafting.deleteItem(namespacedKey, player1);
                }
                sendMessage(player1, "no_name");
                return true;
            });
            Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
        if (cache.getItems().isRecipeItem()) {
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

    private void sendItemListExpanded(Player player) {
        TestCache cache = ((TestCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache());
        for (int i = 0; i < 20; i++) {
            player.sendMessage(" ");
        }

        int currentPage = cache.getChatLists().getCurrentPageItems();
        int itemsPerPage = cache.getChatLists().getLastUsedItem() != null ? 16 : 14;
        int maxPages = ((CustomItems.getCustomItems().size() % itemsPerPage) > 0 ? 1 : 0) + CustomItems.getCustomItems().size() / itemsPerPage;

        api.sendActionMessage(player,
                new ClickData("[&3« Back&7]", null, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cc")),
                new ClickData("                   &7&lItems            ", null),
                new ClickData("&7[&e&l«&7]", (wolfyUtilities, p) -> {
                    if (currentPage > 1) {
                        cache.getChatLists().setCurrentPageItems(cache.getChatLists().getCurrentPageItems() - 1);
                    }
                    sendItemListExpanded(p);
                }, true),
                new ClickData(" &e" + currentPage + "&7/&6" + maxPages + "", null),
                new ClickData(" &7[&e&l»&7]", (wolfyUtilities, p) -> {
                    if (currentPage < maxPages) {
                        cache.getChatLists().setCurrentPageItems(cache.getChatLists().getCurrentPageItems() + 1);
                    }
                    sendItemListExpanded(p);
                }, true));
        api.sendPlayerMessage(player, "&8-------------------------------------------------");

        int i = (currentPage - 1) * itemsPerPage;
        for (Map.Entry<NamespacedKey, CustomItem> entry : CustomItems.getCustomItems().entrySet()) {
            NamespacedKey namespacedKey = entry.getKey();
            CustomItem customItem = entry.getValue();
            if (customItem != null) {
                if (customItem.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey().equals(namespacedKey)) {
                    api.sendActionMessage(player, new ClickData("§7" + " -&7[&c!&7] &4" + namespacedKey.toString(), null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "&cThis Item is corrupted! Delete and recreate it! Do not load it into the GUI!")));
                } else {
                    api.sendActionMessage(player, new ClickData("§7" + " - ", null), new ClickData(namespacedKey.toString(), null, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, namespacedKey.getNamespace() + " " + namespacedKey.getKey()), new HoverEvent(customItem.create())));
                }
                if (i < (currentPage - 1) * itemsPerPage + itemsPerPage) {
                    i++;
                } else {
                    break;
                }
            }
        }
        if (cache.getChatLists().getLastUsedItem() != null) {
            api.sendPlayerMessage(player, "§ePreviously used:");
            NamespacedKey namespacedKey = cache.getChatLists().getLastUsedItem();
            CustomItem customItem = CustomItems.getCustomItem(namespacedKey);
            if (customItem != null) {
                if (customItem.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey().equals(namespacedKey)) {
                    api.sendActionMessage(player, new ClickData("§b -&7[&c!&7] &4" + namespacedKey.toString(), null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "&cThis Item is corrupted! Delete and recreate it! Do not load it into the GUI!")));
                } else {
                    api.sendActionMessage(player, new ClickData("§b - ", null), new ClickData(namespacedKey.toString(), null, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, namespacedKey.getNamespace() + " " + namespacedKey.getKey()), new HoverEvent(customItem.create())));
                }
            }
        }
        api.sendPlayerMessage(player, "&8-------------------------------------------------");
        sendMessage(player, "input");
    }
}
