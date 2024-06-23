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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

import java.util.function.Consumer;

public class MenuItemEditor extends CCWindow {

    public MenuItemEditor(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeCreator.ITEM_EDITOR.getKey(), 54, customCrafting);
    }

    @Override
    public void onInit() {
        ButtonBuilder<CCCache> btnB = getButtonBuilder();

        getButtonBuilder().action("back").state(s -> s.key(ClusterMain.BACK_BOTTOM).icon(Material.BARRIER).action((cache, guiHandler, player, inv, i, event) -> {
            cache.setApplyItem(null);
            if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                if (!cache.getItems().isRecipeItem()) {
                    cache.getItems().setRecipeItem(true);
                    guiHandler.openCluster(ClusterItemCreator.KEY);
                    return true;
                }
                guiHandler.openPreviousWindow();
            } else {
                if (cache.getSetting().equals(Setting.ITEMS)) {
                    // Move back to the ItemCreator
                    guiHandler.openCluster(ClusterItemCreator.KEY);
                    return true;
                }
                guiHandler.openCluster(ClusterMain.KEY);
            }
            return true;
        })).register();
        getButtonBuilder().action("create_item").state(s -> s.icon(Material.ITEM_FRAME).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getItems().editorWasPreviouslyCancelled(false);
            guiHandler.openWindow(ClusterItemCreator.MAIN_MENU);
            return true;
        })).register();

        getButtonBuilder().action(ClusterMain.ITEM_LIST.getKey()).state(s -> s.key(ClusterMain.ITEM_LIST).icon(Material.BOOKSHELF).action((cache, guiHandler, player, inv, i, event) -> {
            guiHandler.openWindow(ClusterMain.ITEM_LIST);
            return true;
        })).register();

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        NamespacedKey gray = PlayerUtil.getStore(event.getPlayer()).getDarkBackground();
        for (int i = 0; i < 9; i++) {
            event.setButton(i, gray);
        }

        for (StackIdentifierParser<?> parser : wolfyUtilities.getCore().getRegistries().getStackIdentifierParsers().sortedParsers()) {
            String name = "reference." + parser.getNamespacedKey().toString("_");

            if (getButton(name + ".icon") == null) {
                Consumer<ButtonState.Builder<CCCache>> applyIcon = s -> {
                    StackIdentifierParser.DisplayConfiguration.IconSettings iconSettings = parser.displayConfig().icon();
                    if (iconSettings instanceof StackIdentifierParser.DisplayConfiguration.StackIconSettings stackIconSettings) {
                        s.icon(stackIconSettings.stack());
                    } else if (iconSettings instanceof StackIdentifierParser.DisplayConfiguration.MaterialIconSettings materialIconSettings) {
                        s.icon(materialIconSettings.material());
                    }
                    s.render((ccCache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.component("name", parser.displayConfig().name())));
                };

                getButtonBuilder().dummy(name + ".icon")
                        .state(s -> {
                            s.key("reference.icon");
                            applyIcon.accept(s);
                        })
                        .register();
                getButtonBuilder().action(name + ".swap")
                        .state(s -> {
                            s.key("reference.swap");
                            applyIcon.accept(s);
                            s.action((ccCache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                                ccCache.getItems().originalReference().swapParser(parser);
                                return true;
                            });
                        })
                        .register();
            }
        }

        Items items = event.getGuiHandler().getCustomCache().getItems();
        StackReference reference = items.originalReference();

        // Add current Reference Type indicator here in slot 0
        event.setItem(4, reference.referencedStack());
        event.setButton(22, ClusterMain.GLASS_PURPLE);
        event.setButton(3, ClusterMain.GLASS_GREEN);
        if (event.getGuiHandler().getCustomCache().getSetting() == Setting.RECIPE_CREATOR) {
            // Only provide these options when accessing it from the Recipe Creator
            // Items to edit via the Item Creator can be picked from the Item List via the Main Menu!
            event.setButton(6, "create_item");
            event.setButton(2, ClusterMain.ITEM_LIST.getKey());
        }
        event.setButton(5, ClusterMain.GLASS_GREEN);
        event.setButton(13, ClusterMain.GLASS_PURPLE);

        NamespacedKey white = ClusterMain.GLASS_PURPLE;
        for (int i = 18; i < 27; i++) {
            event.setButton(i, white);
        }
        event.setButton(22, "reference." + reference.parser().getNamespacedKey().toString("_") + ".icon");

        // Parser selection
        int slot = 27;
        for (StackIdentifierParser<?> parser : wolfyUtilities.getCore().getRegistries().getStackIdentifierParsers().matchingParsers(reference.originalStack())) {
            if (slot == 45) break;
            NamespacedKey key = parser.getNamespacedKey();
            event.setButton(slot++, "reference." + key.toString("_") + ".swap");
        }
        event.setButton(49, "back");
    }

}
