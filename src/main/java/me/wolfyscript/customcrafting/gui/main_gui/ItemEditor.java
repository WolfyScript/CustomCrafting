package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.api.chat.HoverEvent;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class ItemEditor extends CCWindow {

    public ItemEditor(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "item_editor", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        })));
        registerButton(new ActionButton<>("create_item", Material.ITEM_FRAME, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow(new NamespacedKey("item_creator", "main_menu"));
            return true;
        }));
        registerButton(new ActionButton<>("delete_item", Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
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
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        CCCache cache = event.getGuiHandler().getCustomCache();
        if (cache.getItems().isRecipeItem()) {
            event.setButton(21, new NamespacedKey("none", "item_list"));
            event.setButton(23, "create_item");
        } else {
            event.setButton(20, "create_item");
            event.setButton(22, new NamespacedKey("none", "item_list"));
            event.setButton(24, "delete_item");
        }
    }

    private void sendItemListExpanded(Player player) {
        CCCache cache = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache());
        for (int i = 0; i < 20; i++) {
            player.sendMessage(" ");
        }

        int currentPage = cache.getChatLists().getCurrentPageItems();
        int itemsPerPage = cache.getChatLists().getLastUsedItem() != null ? 16 : 14;
        int maxPages = ((Registry.CUSTOM_ITEMS.keySet().size() % itemsPerPage) > 0 ? 1 : 0) + Registry.CUSTOM_ITEMS.keySet().size() / itemsPerPage;

        api.getChat().sendActionMessage(player,
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
        api.getChat().sendMessage(player, "&8-------------------------------------------------");

        int i = (currentPage - 1) * itemsPerPage;
        for (Map.Entry<NamespacedKey, CustomItem> entry : Registry.CUSTOM_ITEMS.entrySet()) {
            NamespacedKey namespacedKey = entry.getKey();
            CustomItem customItem = entry.getValue();
            if (customItem != null) {
                if (customItem.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey().equals(namespacedKey)) {
                    api.getChat().sendActionMessage(player, new ClickData("§7" + " -&7[&c!&7] &4" + namespacedKey.toString(), null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "&cThis Item is corrupted! Delete and recreate it! Do not load it into the GUI!")));
                } else {
                    api.getChat().sendActionMessage(player, new ClickData("§7" + " - ", null), new ClickData(namespacedKey.toString(), null, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, namespacedKey.getNamespace() + " " + namespacedKey.getKey()), new HoverEvent(customItem.create())));
                }
                if (i < (currentPage - 1) * itemsPerPage + itemsPerPage) {
                    i++;
                } else {
                    break;
                }
            }
        }
        if (cache.getChatLists().getLastUsedItem() != null) {
            api.getChat().sendMessage(player, "§ePreviously used:");
            NamespacedKey namespacedKey = cache.getChatLists().getLastUsedItem();
            CustomItem customItem = Registry.CUSTOM_ITEMS.get(namespacedKey);
            if (customItem != null) {
                if (customItem.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey().equals(namespacedKey)) {
                    api.getChat().sendActionMessage(player, new ClickData("§b -&7[&c!&7] &4" + namespacedKey.toString(), null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "&cThis Item is corrupted! Delete and recreate it! Do not load it into the GUI!")));
                } else {
                    api.getChat().sendActionMessage(player, new ClickData("§b - ", null), new ClickData(namespacedKey.toString(), null, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, namespacedKey.getNamespace() + " " + namespacedKey.getKey()), new HoverEvent(customItem.create())));
                }
            }
        }
        api.getChat().sendMessage(player, "&8-------------------------------------------------");
        sendMessage(player, "input");
    }
}
