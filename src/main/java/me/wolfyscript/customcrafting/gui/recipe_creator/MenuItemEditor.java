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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.ItemsAdderRef;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItemEditor extends CCWindow {

    private static final String REFERENCE_WOLFYUTILITIES = "reference.wolfyutils";
    private static final String REFERENCE_BUKKIT = "reference.bukkit";
    private static final String REFERENCE_ORAXEN = "reference.oraxen";
    private static final String REFERENCE_ITEMSADDER = "reference.itemsadder";
    private static final String REFERENCE_MYTHICMOBS = "reference.mythicmobs";
    private static final String REFERENCE_FANCYBAGS = "reference.fancybags";

    public MenuItemEditor(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeCreator.ITEM_EDITOR.getKey(), 54, customCrafting);
    }

    @Override
    public void onInit() {
        ButtonBuilder<CCCache> btnB = getButtonBuilder();

        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((cache, guiHandler, player, inv, i, event) -> {
            if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                cache.getItems().setRecipeItem(false);
                cache.setApplyItem(null);
                guiHandler.openPreviousWindow();
            } else {
                guiHandler.openCluster(ClusterMain.KEY);
            }
            return true;
        })).register();
        getButtonBuilder().action("create_item").state(s -> s.icon(Material.ITEM_FRAME).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow(ClusterItemCreator.MAIN_MENU);
            return true;
        })).register();

        getButtonBuilder().action(ClusterMain.ITEM_LIST.getKey()).state(s -> s.key(ClusterMain.ITEM_LIST).icon(Material.BOOKSHELF).action((cache, guiHandler, player, inv, i, event) -> {
            guiHandler.openWindow(ClusterMain.ITEM_LIST);
            return true;
        })).register();

        // Reference parser choose buttons
        btnB.dummy(REFERENCE_WOLFYUTILITIES)
                .state(s -> s.icon(Material.CRAFTING_TABLE)
                        .render((cache, guiHandler, player, inv, stack, i) ->
                                CallbackButtonRender.UpdateResult.of(
                                        Placeholder.unparsed(
                                                "item_key",
                                                cache.getItems().originalReference().identifier() instanceof WolfyUtilsStackIdentifier sI ? sI.customItem().map(customItem -> customItem.getNamespacedKey().toString()).orElse("null") : "null"
                                        ))))
                .register();
        btnB.dummy(REFERENCE_ORAXEN)
                .state(s -> s.icon(Material.DIAMOND)
                        .render((cache, guiHandler, player, inv, stack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("item_key", "")))).register();
        btnB.dummy(REFERENCE_ITEMSADDER)
                .state(s -> s.icon(Material.GRASS_BLOCK)
                        .render((cache, guiHandler, player, inv, stack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("item_key", "")))).register();
        btnB.dummy(REFERENCE_MYTHICMOBS)
                .state(s -> s.icon(Material.WITHER_SKELETON_SKULL)).register();

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        NamespacedKey gray = PlayerUtil.getStore(event.getPlayer()).getDarkBackground();
        for (int i = 0; i < 9; i++) {
            event.setButton(i, gray);
        }

        // Add current Reference Type indicator here in slot 0
        event.setButton(1, ClusterMain.GLASS_GREEN);
        event.setItem(2, event.getGuiHandler().getCustomCache().getItems().originalReference().identifier().item());
        event.setButton(3, ClusterMain.GLASS_GREEN);
        event.setButton(4, "create_item");
        event.setButton(5, ClusterMain.GLASS_GREEN);
        event.setButton(6, ClusterMain.ITEM_LIST.getKey());

        StackReference reference = event.getGuiHandler().getCustomCache().getItems().originalReference();

        // Parser selection
        List<StackIdentifierParser<?>> parsers = wolfyUtilities.getCore().getRegistries().getStackIdentifierParsers().matchingParsers(reference.stack());

        int slot = 18;
        for (StackIdentifierParser<?> parser : parsers) {
            if (slot == 45) break;
            NamespacedKey key = parser.getNamespacedKey();
            if (key.getNamespace().equals(NamespacedKey.WOLFYUTILITIES)) {
                event.setButton(slot++, "reference." + key.getKey());
            } else {
                // TODO: Looks like a third-party parser
            }
        }
        event.setButton(49, ClusterMain.BACK_BOTTOM);
    }

    private static String getParserBtnID(StackIdentifierParser<?> parser) {
        if (parser instanceof BukkitStackIdentifier.Parser) {
            return REFERENCE_BUKKIT;
        } else if (parser instanceof WolfyUtilsStackIdentifier.Parser) {
            return REFERENCE_MYTHICMOBS;
        } else if (parser instanceof ItemsAdderRef)
    }
}
