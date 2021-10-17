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
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

class ButtonSettingsLockdown extends ToggleButton<CCCache> {

    public static final String KEY = "lockdown";

    ButtonSettingsLockdown(WolfyUtilities api, CustomCrafting customCrafting) {
        super(KEY, (ccCache, guiHandler, player, guiInventory, i) -> customCrafting.getConfigHandler().getConfig().isLockedDown(),
                new State(true, api, customCrafting, "&cAre you sure you want to disable LockDown mode?", "&c&lThis will enable all the custom recipes!"),
                new State(false, api, customCrafting, "&cAre you sure you want to enable LockDown mode?", "&c&lThis will disable all the custom recipes!")
        );
    }

    private static class State extends ButtonState<CCCache> {

        public State(boolean enabled, WolfyUtilities api, CustomCrafting customCrafting, String... messages) {
            super(KEY + (enabled ? ".enabled" : ".disabled"), Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
                if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
                    guiHandler.close();
                    api.getChat().sendMessages(player, messages);
                    api.getChat().sendActionMessage(player, new ClickData("&7[&aYES&7]", (wolfyUtilities, player1) -> {
                        customCrafting.getConfigHandler().getConfig().setLockDown(!enabled);
                        customCrafting.getConfigHandler().getConfig().save();
                        wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster();
                    }, true), new ClickData("&7 -- ", null), new ClickData("&7[&cNO&7]", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
                }
                return true;
            });
        }
    }
}
