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

package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

class ButtonSettingsLanguage extends ActionButton<CCCache> {

    public static final String KEY = "language";

    ButtonSettingsLanguage(List<String> availableLangs, WolfyUtilities api, CustomCrafting customCrafting) {
        super(KEY, new ButtonState<>(KEY, Material.BOOKSHELF, (cache, guiHandler, player, inventory, slot, event) -> {
            int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
            int nextIndex = index;
            if (event instanceof InventoryClickEvent clickEvent) {
                if (clickEvent.isLeftClick() && !clickEvent.isShiftClick()) {
                    nextIndex = (index + 1 < availableLangs.size()) ? index + 1 : 0;
                } else if (clickEvent.isRightClick() && !clickEvent.isShiftClick()) {
                    nextIndex = index - 1 >= 0 ? index - 1 : availableLangs.size() - 1;
                } else if (clickEvent.isShiftClick()) {
                    if (ChatUtils.checkPerm(player, "customcrafting.cmd.reload")) {
                        api.getChat().sendMessage(player, "&eReloading Inventories and Languages!");
                        customCrafting.getApi().getLanguageAPI().unregisterLanguages();
                        customCrafting.getConfigHandler().getConfig().reload();
                        customCrafting.getConfigHandler().loadLang();
                        customCrafting.getApi().getInventoryAPI().reset();
                        api.getChat().sendMessage(player, "&aReload complete! Reloaded GUIs and languages");
                        guiHandler.close();
                        return true;
                    }
                    return true;
                }
            }
            customCrafting.getConfigHandler().getConfig().setLanguage(availableLangs.get(nextIndex));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
            List<String> displayLangs = new ArrayList<>();
            displayLangs.addAll(availableLangs.subList(index, availableLangs.size()));
            displayLangs.addAll(availableLangs.subList(0, index));
            for (int i = 0; i < 5; i++) {
                if (i < displayLangs.size()) {
                    hashMap.put("%lang" + i + "%", displayLangs.get(i));
                } else {
                    hashMap.put("%lang" + i + "%", "");
                }
            }
            return itemStack;
        }));
    }
}
