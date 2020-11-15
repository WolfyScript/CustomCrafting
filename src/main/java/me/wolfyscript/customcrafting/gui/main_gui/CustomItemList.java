package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.CustomItemSelectButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.ItemNamespaceButton;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;

import java.util.List;

public class CustomItemList extends ExtendedGuiWindow {

    public CustomItemList(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("item_list", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getListNamespace() != null) {
                items.setListNamespace(null);
            } else {
                guiHandler.openPreviousInv();
            }
            return true;
        })));
        registerButton(new ActionButton("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getItems().getListPage();
            int maxPages = CustomItems.getCustomItems().size() / 45 + CustomItems.getCustomItems().size() % 45 > 0 ? 1 : 0;
            if (page < maxPages) {
                items.setListPage(++page);
            }
            return true;
        }));
        registerButton(new ActionButton("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getItems().getListPage();
            if (page > 0) {
                items.setListPage(--page);
            }
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        TestCache cache = (TestCache) update.getGuiHandler().getCustomCache();
        Items items = cache.getItems();

        int page = items.getListPage();
        int maxPages = CustomItems.getCustomItems().size() / 45;
        if (page > maxPages) {
            items.setListPage(maxPages);
        }

        String namespace = items.getListNamespace();
        if (namespace == null) {
            List<String> namespaceList = CustomItems.getNamespaces();
            maxPages = namespaceList.size() / 45;
            for (int i = 45 * page, item = 0; item < 45 && i < namespaceList.size(); i++, item++) {
                Button btn = new ItemNamespaceButton(namespaceList.get(i));
                registerButton(btn);
                update.setButton(9 + item, btn);
            }
        } else {
            List<CustomItem> customItems = CustomItems.getCustomItems(namespace);
            for (int i = items.getListPage() * 45, s = 9; i < customItems.size() && s < 45; i++, s++) {
                NamespacedKey namespacedKey = customItems.get(i).getNamespacedKey();
                String id = "item_" + namespacedKey.toString().replace(":", "__");
                if (!hasButton(id)) {
                    CustomItemSelectButton btn = new CustomItemSelectButton(namespacedKey);
                    registerButton(btn);
                }
                update.setButton(s, id);
            }
        }

        if (page > 0) {
            update.setButton(3, "previous_page");
        }
        if (maxPages > 0 && page < maxPages) {
            update.setButton(6, "next_page");
        }
    }
}
