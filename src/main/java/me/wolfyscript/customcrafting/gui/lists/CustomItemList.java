package me.wolfyscript.customcrafting.gui.lists;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.lists.buttons.CustomItemSelectButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.ItemNamespaceButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
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
        registerButton(new ActionButton<>("back", new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getListNamespace() != null) {
                items.setListNamespace(null);
            } else if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                List<? extends GuiWindow<?>> history = guiHandler.getClusterHistory().get(guiHandler.getCluster());
                history.remove(history.size() - 1);
                guiHandler.openCluster(RecipeCreatorCluster.KEY);
            } else {
                guiHandler.openPreviousWindow();
            }
            return true;
        })));
        registerButton(new ActionButton<>("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.setListPage(cache.getItems().getListPage() + 1);
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
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        CCCache cache = update.getGuiHandler().getCustomCache();
        Items items = cache.getItems();
        int maxPages;
        int page;
        String namespace = items.getListNamespace();
        if (namespace == null) {
            List<String> namespaceList = Registry.CUSTOM_ITEMS.keySet().parallelStream().filter(key -> key.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)).map(NamespacedKeyUtils::getInternalNamespace).distinct().filter(Objects::nonNull).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            maxPages = namespaceList.size() / 45 + (namespaceList.size() % 45 > 0 ? 1 : 0);
            page = items.getListPage(maxPages);

            for (int i = 45 * page, item = 9; item < 54 && i < namespaceList.size(); i++, item++) {
                String btnID = "namespace_" + namespaceList.get(i);
                if (!hasButton("namespace_" + namespaceList.get(i))) {
                    registerButton(new ItemNamespaceButton(namespaceList.get(i)));
                }
                update.setButton(item, btnID);
            }
        } else {
            List<CustomItem> customItems = Registry.CUSTOM_ITEMS.entrySet().parallelStream().filter(entry -> entry.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE) && namespace.equals(NamespacedKeyUtils.getInternalNamespace(entry.getKey()))).map(Map.Entry::getValue).collect(Collectors.toList());
            maxPages = customItems.size() / 45 + (customItems.size() % 45 > 0 ? 1 : 0);
            page = items.getListPage(maxPages);

            for (int i = items.getListPage() * 45, s = 9; i < customItems.size() && s < 54; i++, s++) {
                NamespacedKey namespacedKey = customItems.get(i).getNamespacedKey();
                String id = "item_" + namespacedKey.toString("__");
                if (!hasButton(id)) {
                    registerButton(new CustomItemSelectButton(customCrafting, namespacedKey));
                }
                update.setButton(s, id);
            }
        }
        if (page > 0) {
            update.setButton(3, "previous_page");
        }
        if (page + 1 < maxPages) {
            update.setButton(6, "next_page");
        }
    }
}
