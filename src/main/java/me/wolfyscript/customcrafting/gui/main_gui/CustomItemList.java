package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CacheButtonAction;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.CustomItemSelectButton;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;

public class CustomItemList extends ExtendedGuiWindow {

    public CustomItemList(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("item_list", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (CacheButtonAction) (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
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

        NamespacedKey[] namespacedKeys = CustomItems.getCustomItems().keySet().toArray(new NamespacedKey[0]);
        for (int i = items.getListPage() * 45, s = 9; i < namespacedKeys.length && s < 45; i++, s++) {
            NamespacedKey namespacedKey = namespacedKeys[i];
            String id = namespacedKey.toString().replace(":", "__");
            if (!hasButton(id)) {
                CustomItemSelectButton btn = new CustomItemSelectButton(namespacedKey);
                registerButton(btn);
            }
            update.setButton(s, "item_" + id);
        }

        if (page > 0) {
            update.setButton(3, "previous_page");
        }
        if (maxPages > 0) {
            update.setButton(6, "next_page");
        }
    }
}
