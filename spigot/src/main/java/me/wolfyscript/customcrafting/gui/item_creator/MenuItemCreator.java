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

import com.google.common.collect.Lists;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.*;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.ItemsAdderStackIdentifier;
import me.wolfyscript.utilities.compatibility.plugins.mythicmobs.MythicMobsStackIdentifier;
import me.wolfyscript.utilities.compatibility.plugins.oraxen.OraxenStackIdentifier;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuItemCreator extends CCWindow {

    private final List<ItemCreatorTab> tabs = new ArrayList<>();

    private static final String BACK = "back";
    private static final String CANCEL = "cancel";
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
        var btnB = getButtonBuilder();
        btnB.action(BACK).state(s -> s.key(ClusterMain.BACK_BOTTOM).icon(Material.BARRIER).action((cache, guiHandler, player, inventory, i, event) -> {
            guiHandler.openCluster(cache.getItems().isRecipeItem() ? "recipe_creator" : "none");
            return true;
        })).register();
        btnB.action(CANCEL).state(s -> s.icon(Material.BARRIER).action((cache, guiHandler, player, inventory, i, event) -> {
            cache.getItems().editorWasPreviouslyCancelled(true);
            if (cache.getItems().isRecipeItem()) { // Directly jump to the ItemEditor of the RecipeCreator, to not show the pickup window
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        })).register();
        btnB.itemInput(ITEM_INPUT).state(s -> s.icon(Material.AIR).postAction((cache, guiHandler, player, guiInventory, stack, slot, event) -> {
            var items = cache.getItems();
            // Normal interaction, just apply the item
            guiHandler.getWolfyUtils().getRegistries().getStackIdentifierParsers().parseFrom(stack).ifPresentOrElse(reference -> {
                items.editorWasPreviouslyCancelled(false);
                items.setItem(new CustomItem(reference));
            }, () -> /* Should never happen (Bukkit reference would need to fail) */ items.setItem(new CustomItem(Material.AIR)));
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(guiHandler.getCustomCache().getItems().getItem().getItemStack()))).register();
        btnB.action(SAVE_ITEM).state(s -> s.icon(Material.WRITABLE_BOOK).action((cache, guiHandler, player, inventory, i, event) -> {
            var items = cache.getItems();
            if (!ItemUtils.isAirOrNull(items.getItem().getItemStack()) && items.getNamespacedKey() != null) {
                saveItem(items, player, items.getNamespacedKey());
            }
            return true;
        })).register();
        btnB.action(SAVE_ITEM_AS).state(s -> s.icon(Material.WRITABLE_BOOK).action((cache, guiHandler, player, guiInventory, i, event) -> {
            var items = cache.getItems();
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                List<String[]> namespacedKeys = api.getRegistries().getCustomItems().get(NamespacedKeyUtils.NAMESPACE).stream().map(customItem -> customItem.getNamespacedKey().getKey().split("/")).toList();
                List<String> namespaces = namespacedKeys.stream().filter(strings -> strings.length > 0).map(strings -> strings[0]).toList();
                List<String> keys = namespacedKeys.stream().filter(strings -> strings.length > 1).map(strings -> strings[1]).toList();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    if (args.length == 2) {
                        return StringUtil.copyPartialMatches(args[1], keys, Lists.newArrayList("<key>"));
                    }
                    if (args.length >= 1) {
                        return StringUtil.copyPartialMatches(args[0], namespaces, Lists.newArrayList("<namespace>"));
                    }
                    return Collections.emptyList();
                });
                openChat(guiHandler, translatedMsgKey("save.input.line1"), (guiHandler1, player1, s1, args) -> !saveItem(items, player1, ChatUtils.getNamespacedKey(player1, s1, args)));
                getChat().sendMessage(player, getChat().translated("msg.input.wui_command"));
            }
            return true;
        })).register();
        btnB.action(APPLY_ITEM).state(s -> s.icon(Material.GREEN_CONCRETE).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                var customItem = cache.getItems().getItem();
                if (items.isSaved()) {
                    ItemLoader.saveItem(items.getNamespacedKey(), customItem);
                    customItem = api.getRegistries().getCustomItems().get(items.getNamespacedKey());
                }
                cache.applyItem(customItem);
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        })).register();
        // Displays the stack in the ItemCreator
        btnB.action("item_display").state(s -> s.icon(Material.AIR).action((cache, guiHandler, player, guiInventory, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                if (cache.getSetting() == Setting.RECIPE_CREATOR) {
                    cache.getItems().setRecipeItem(false); // Prevent to move back to the ingredient/result menu
                }
                // Allow editing the stack reference like in the Recipe Creator
                Bukkit.getScheduler().runTask(CustomCrafting.inst(), () -> guiHandler.openWindow(ClusterRecipeCreator.ITEM_EDITOR));
                return true;
            }
            return true;
        }).render((ccCache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(ccCache.getItems().getItem().stackReference().referencedStack()))).register();

        btnB.action(PAGE_NEXT).state(s -> s.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.setPage(items.getPage() + 1);
            return true;
        })).register();
        btnB.action(PAGE_PREVIOUS).state(s -> s.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getPage() > 0) {
                items.setPage(items.getPage() - 1);
            }
            return true;
        })).register();
        registerReferences(btnB);
        tabs.clear();
        customCrafting.getRegistries().getItemCreatorTabs().forEach(tab -> tab.register(this, api));
    }

    private void registerReferences(ButtonBuilder<CCCache> btnB) {
        btnB.dummy(REFERENCE_WOLFYUTILITIES).state(s -> s.icon(Material.CRAFTING_TABLE)
                .render((cache, guiHandler, player, inv, stack, i) ->
                        CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("item_key", guiHandler.getCustomCache().getItems().getItem().stackReference().identifier()
                                .map(identifier -> ((WolfyUtilsStackIdentifier) identifier).itemKey().toString()).orElse("null")))
                )
        ).register();
        btnB.dummy(REFERENCE_ORAXEN).state(s -> s.icon(Material.DIAMOND)
                .render((cache, guiHandler, player, inv, stack, i) ->
                        CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("item_key", guiHandler.getCustomCache().getItems().getItem().stackReference().identifier()
                                .map(identifier -> ((OraxenStackIdentifier) identifier).itemId()).orElse("null")))
                )
        ).register();
        btnB.dummy(REFERENCE_ITEMSADDER).state(s -> s.icon(Material.GRASS_BLOCK)
                .render((cache, guiHandler, player, inv, stack, i) ->
                        CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("item_key", guiHandler.getCustomCache().getItems().getItem().stackReference().identifier()
                                .map(identifier -> ((ItemsAdderStackIdentifier) identifier).itemId()).orElse("null")))
                )
        ).register();
        btnB.dummy(REFERENCE_MYTHICMOBS).state(s -> s.icon(Material.WITHER_SKELETON_SKULL)).register();
    }

    private void orderTabs() {
        var registry = customCrafting.getRegistries().getItemCreatorTabs();
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabDisplayName.KEY)));
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
        if(ServerVersion.isAfterOrEq(MinecraftVersion.of(1,21,4))) {
            tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabNewCustomModelData.KEY)));
            tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE,TabItemModel.KEY)));
        }
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabArmorSlots.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabParticleEffects.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabEliteCraftingTable.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabRecipeBook.KEY)));
        tabs.add(registry.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, TabVanilla.KEY)));
        registry.values().forEach(tab -> {
            if (!tabs.contains(tab)) {
                tabs.add(tab);
            }
        });
    }

    private boolean saveItem(Items items, Player player, NamespacedKey namespacedKey) {
        if (namespacedKey != null && !namespacedKey.getNamespace().equalsIgnoreCase("minecraft")) {
            var customItem = items.getItem();
            if (customItem.stackReference().identifier().orElse(null) instanceof WolfyUtilsStackIdentifier identifier && identifier.getNamespacedKey().equals(namespacedKey)) {
                getChat().sendMessage(player, Component.text("Error saving item! Cannot override original CustomItem ", NamedTextColor.RED).append(Component.text(namespacedKey.toString(), NamedTextColor.DARK_RED)).append(Component.text("! Save it under another NamespacedKey or Edit the original!", NamedTextColor.RED)));
                return false;
            }
            ItemLoader.saveItem(namespacedKey, items.getItem());
            items.setSaved(true);
            items.setNamespacedKey(namespacedKey);
            getChat().sendMessage(player, translatedMsgKey("save.success"));
            var internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
            getChat().sendMessage(player, Component.text(internalKey.getNamespace() + "/items/" + internalKey.getKey(), NamedTextColor.YELLOW));
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

        if (ItemUtils.isAirOrNull(item) || items.editorWasPreviouslyCancelled()) {
            event.setButton(12, ClusterMain.GLASS_GREEN);
            event.setButton(13, ClusterMain.GLASS_GREEN);
            event.setButton(14, ClusterMain.GLASS_GREEN);
            event.setButton(21, ClusterMain.GLASS_GREEN);
            event.setButton(22, ITEM_INPUT);
            event.setButton(23, ClusterMain.GLASS_GREEN);
            event.setButton(30, ClusterMain.GLASS_GREEN);
            event.setButton(31, ClusterMain.GLASS_GREEN);
            event.setButton(32, ClusterMain.GLASS_GREEN);
            event.setButton(49, BACK);
            return;
        }

        event.setButton(45, CANCEL);
        event.setButton(4, "item_display");
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        var gray = data.getLightBackground();
        if (customCrafting.getConfigHandler().getConfig().isGUIDrawBackground()) event.setButton(13, gray);
        if (items.isRecipeItem()) {
            event.setButton(51, APPLY_ITEM);
        }
        if (items.getNamespacedKey() != null) {
            event.setButton(52, SAVE_ITEM);
        }
        event.setButton(53, SAVE_ITEM_AS);

        customItem.stackReference().identifier().ifPresent(identifier -> {
            if (identifier instanceof WolfyUtilsStackIdentifier) {
                event.setButton(49, REFERENCE_WOLFYUTILITIES);
            } else if (identifier instanceof OraxenStackIdentifier) {
                event.setButton(49, REFERENCE_ORAXEN);
            } else if (identifier instanceof ItemsAdderStackIdentifier) {
                event.setButton(49, REFERENCE_ITEMSADDER);
            } else if (identifier instanceof MythicMobsStackIdentifier) {
                event.setButton(49, REFERENCE_MYTHICMOBS);
            }
        });

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
            } else if (customCrafting.getConfigHandler().getConfig().isGUIDrawBackground()) {
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
        if (tabs.size() != customCrafting.getRegistries().getItemCreatorTabs().values().size()) {
            if (tabs.isEmpty()) {
                orderTabs();
            } else {
                customCrafting.getRegistries().getItemCreatorTabs().values().forEach(tab -> {
                    if (!tabs.contains(tab)) {
                        tabs.add(tab);
                    }
                });
            }
        }
        return tabs.stream().filter(tab -> tab != null && tab.shouldRender(update, cache, items, customItem, item)).toList();
    }
}