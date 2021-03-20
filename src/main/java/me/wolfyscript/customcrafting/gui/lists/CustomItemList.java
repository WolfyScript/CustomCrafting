package me.wolfyscript.customcrafting.gui.lists;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.lists.buttons.CustomItemSelectButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.ItemNamespaceButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomItemList extends CCWindow {

    public CustomItemList(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "item_list", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getListNamespace() != null) {
                items.setListNamespace(null);
            } else {
                guiHandler.openPreviousWindow();
            }
            return true;
        })));
        registerButton(new ActionButton<>("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getItems().getListPage();
            int maxPages = Registry.CUSTOM_ITEMS.keySet().size() / 45 + Registry.CUSTOM_ITEMS.keySet().size() % 45 > 0 ? 1 : 0;
            if (page < maxPages) {
                items.setListPage(++page);
            }
            return true;
        }));
        registerButton(new ActionButton<>("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getItems().getListPage();
            if (page > 0) {
                items.setListPage(--page);
            }
            return true;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        CCCache cache = update.getGuiHandler().getCustomCache();
        Items items = cache.getItems();
        int page = items.getListPage();
        int maxPages = Registry.CUSTOM_ITEMS.keySet().size() / 45;
        if (page > maxPages) {
            items.setListPage(maxPages);
        }
        String namespace = items.getListNamespace();
        if (namespace == null) {
            List<String> namespaceList = Registry.CUSTOM_ITEMS.keySet().parallelStream().filter(key -> key.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)).map(NamespacedKeyUtils::getInternalNamespace).distinct().filter(Objects::nonNull).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            maxPages = namespaceList.size() / 45;
            for (int i = 45 * page, item = 0; item < 45 && i < namespaceList.size(); i++, item++) {
                Button<CCCache> btn = new ItemNamespaceButton(namespaceList.get(i));
                registerButton(btn);
                update.setButton(9 + item, btn);
            }
        } else {
            List<CustomItem> customItems = Registry.CUSTOM_ITEMS.entrySet().parallelStream().filter(entry -> entry.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE) && namespace.equals(NamespacedKeyUtils.getInternalNamespace(entry.getKey()))).map(Map.Entry::getValue).collect(Collectors.toList());
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
