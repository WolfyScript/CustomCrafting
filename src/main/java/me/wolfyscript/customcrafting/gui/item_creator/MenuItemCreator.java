/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.*;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.ItemsAdderRef;
import me.wolfyscript.utilities.compatibility.plugins.mythicmobs.MythicMobsRef;
import me.wolfyscript.utilities.compatibility.plugins.oraxen.OraxenRef;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuItemCreator extends CCWindow {

    private final List<ItemCreatorTab> tabs = new ArrayList<>();

    private static final String BACK = "back";
    private static final String SAVE_ITEM = "save_item";
    private static final String SAVE_ITEM_AS = "save_item_as";
    private static final String APPLY_ITEM = "apply_item";
    private static final String ITEM_INPUT = "item_input";
    private static final String PAGE_NEXT = "page_next";
    private static final String PAGE_PREVIOUS = "page_previous";

    private static final String REFERENCE_WOLFYUTILITIES = "reference.wolfyutilities";
    private static final String REFERENCE_ORAXEN = "reference.oraxen";
    private static final String REFERENCE_ITEMSADDER = "reference.itemsadder";
    private static final String REFERENCE_MYTHICMOBS = "reference.mythicmobs";

    public MenuItemCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(BACK, new ButtonState<>(ClusterMain.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, i, event) -> {
            if (cache.getItems().isRecipeItem()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("none");
            }
            return true;
        })));
        registerButton(new ItemInputButton<>(ITEM_INPUT, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> false, (cache, guiHandler, player, inventory, item, slot, event) -> {
            var items = cache.getItems();
            items.setItem(CustomItem.getReferenceByItemStack(item != null ? item : ItemUtils.AIR));
        }, null, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> guiHandler.getCustomCache().getItems().getItem().getItemStack())));

        registerButton(new ActionButton<>(SAVE_ITEM, Material.WRITABLE_BOOK, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            var items = cache.getItems();
            if (!ItemUtils.isAirOrNull(items.getItem().getItemStack()) && items.getNamespacedKey() != null) {
                saveItem(items, player, items.getNamespacedKey());
            }
            return true;
        }));
        registerButton(new ActionButton<>(SAVE_ITEM_AS, Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, i, event) -> {
            var items = cache.getItems();
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                sendMessage(player, "save.input.line1");
                List<String[]> namespacedKeys = Registry.CUSTOM_ITEMS.get(NamespacedKeyUtils.NAMESPACE).stream().map(customItem -> customItem.getNamespacedKey().getKey().split("/")).toList();
                List<String> namespaces = namespacedKeys.stream().filter(strings -> strings.length > 0).map(strings -> strings[0]).toList();
                List<String> keys = namespacedKeys.stream().filter(strings -> strings.length > 1).map(strings -> strings[1]).toList();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    if (args.length > 0) {
                        if (args.length == 1) {
                            results.add("<namespace>");
                            StringUtil.copyPartialMatches(args[0], namespaces, results);
                        } else if (args.length == 2) {
                            results.add("<key>");
                            StringUtil.copyPartialMatches(args[1], keys, results);
                        }
                    }
                    Collections.sort(results);
                    return results;
                });

                openChat("save.input.line2", guiHandler, (guiHandler1, player1, s, args) -> !saveItem(items, player1, ChatUtils.getNamespacedKey(player1, s, args)));
            }
            return true;
        }));
        registerButton(new ActionButton<>(APPLY_ITEM, Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                var customItem = cache.getItems().getItem();
                if (items.isSaved()) {
                    ItemLoader.saveItem(items.getNamespacedKey(), customItem);
                    customItem = Registry.CUSTOM_ITEMS.get(items.getNamespacedKey());
                }
                cache.applyItem(customItem);
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        }));
        registerButton(new ActionButton<>(PAGE_NEXT, PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.setPage(items.getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton<>(PAGE_PREVIOUS, PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getPage() > 0) {
                items.setPage(items.getPage() - 1);
            }
            return true;
        }));
        registerReferences();
        tabs.clear();
        CCRegistry.ITEM_CREATOR_TABS.forEach(tab -> tab.register(this, api));
    }

    private void registerReferences() {
        registerButton(new DummyButton<>(REFERENCE_WOLFYUTILITIES, Material.CRAFTING_TABLE, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((WolfyUtilitiesRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getNamespacedKey().toString());
            return itemStack;
        }));
        registerButton(new DummyButton<>(REFERENCE_ORAXEN, Material.DIAMOND, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((OraxenRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getItemID());
            return itemStack;
        }));
        registerButton(new DummyButton<>(REFERENCE_ITEMSADDER, Material.GRASS_BLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((ItemsAdderRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getItemID());
            return itemStack;
        }));
        registerButton(new DummyButton<>(REFERENCE_MYTHICMOBS, Material.WITHER_SKELETON_SKULL));
    }

    private void orderTabs() {
        CCRegistry.ItemCreatorTabRegistry registry = CCRegistry.ITEM_CREATOR_TABS;
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabDisplayName.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabLocalizedName.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabLore.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabEnchants.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabFlags.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabAttributes.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabUnbreakable.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabRepairCost.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabPotion.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabDamage.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabPlayerHead.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabFuel.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabConsume.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabPermission.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabRarity.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabCustomDurability.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabCustomModelData.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabArmorSlots.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabParticleEffects.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabEliteCraftingTable.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabRecipeBook.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabVanilla.KEY)));
        CCRegistry.ITEM_CREATOR_TABS.values().forEach(tab -> {
            if (!tabs.contains(tab)) {
                tabs.add(tab);
            }
        });
    }

    private boolean saveItem(Items items, Player player, NamespacedKey namespacedKey) {
        if (namespacedKey != null) {
            var customItem = items.getItem();
            if (customItem.getApiReference() instanceof WolfyUtilitiesRef wolfyUtilitiesRef && wolfyUtilitiesRef.getNamespacedKey().equals(namespacedKey)) {
                api.getChat().sendMessage(player, "&cError saving item! Cannot override original CustomItem &4" + namespacedKey + "&c! Save it under another NamespacedKey or Edit the original!");
                return false;
            }
            ItemLoader.saveItem(namespacedKey, items.getItem());
            items.setSaved(true);
            items.setNamespacedKey(namespacedKey);
            sendMessage(player, "save.success");
            var internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
            api.getChat().sendMessage(player, "&6" + internalKey.getNamespace() + "/items/" + internalKey.getKey());
        }
        return true;
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        GuiHandler<CCCache> guiHandler = event.getGuiHandler();
        CCCache cache = guiHandler.getCustomCache();
        var items = cache.getItems();
        var customItem = items.getItem();
        var item = customItem.create();

        event.setButton(45, BACK);
        event.setButton(4, ITEM_INPUT);
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        var gray = data.getLightBackground();
        event.setButton(13, gray);
        if (items.isRecipeItem()) {
            event.setButton(51, APPLY_ITEM);
        }
        if (items.getNamespacedKey() != null) {
            event.setButton(52, SAVE_ITEM);
        }
        event.setButton(53, SAVE_ITEM_AS);

        if (customItem.getApiReference() instanceof WolfyUtilitiesRef) {
            event.setButton(49, REFERENCE_WOLFYUTILITIES);
        } else if (customItem.getApiReference() instanceof OraxenRef) {
            event.setButton(49, REFERENCE_ORAXEN);
        } else if (customItem.getApiReference() instanceof ItemsAdderRef) {
            event.setButton(49, REFERENCE_ITEMSADDER);
        } else if (customItem.getApiReference() instanceof MythicMobsRef) {
            event.setButton(49, REFERENCE_MYTHICMOBS);
        }

        List<ItemCreatorTab> options = constructOptions(event, cache, items, customItem, item);
        int maxPages = options.size() / 14 + (options.size() % 14 > 0 ? 1 : 0);
        if (items.getPage() >= maxPages) {
            items.setPage(maxPages - 1);
        }
        if (items.getPage() > 0) {
            event.setButton(5, PAGE_PREVIOUS);
        }
        if (items.getPage() + 1 < maxPages) {
            event.setButton(5, PAGE_NEXT);
        }
        int slot = 0;
        int j = 14 * items.getPage();
        for (int i = 0; i < 14; i++) {
            if (i == 3) {
                slot = 3;
            } else if (i == 10) {
                slot = 4;
            }
            if (j < options.size()) {
                ItemCreatorTab tab = options.get(j);
                if (tab != null && tab.shouldRender(event, cache, items, customItem, item)) {
                    event.setButton(slot + i, tab.getOptionButton());
                } else {
                    i--;
                }
                j++;
            } else {
                event.setButton(slot + i, gray);
            }
        }
        if (!item.getType().equals(Material.AIR)) {
            ItemCreatorTab tab = items.getCurrentTab();
            if (tab != null) {
                tab.render(event, cache, items, customItem, item);
            }
        }
    }

    private List<ItemCreatorTab> constructOptions(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        if (tabs.size() != CCRegistry.ITEM_CREATOR_TABS.values().size()) {
            if (tabs.isEmpty()) {
                orderTabs();
            } else {
                CCRegistry.ITEM_CREATOR_TABS.values().forEach(tab -> {
                    if (!tabs.contains(tab)) {
                        tabs.add(tab);
                    }
                });
            }
        }
        return tabs.stream().filter(tab -> tab != null && tab.shouldRender(update, cache, items, customItem, item)).toList();
    }
}