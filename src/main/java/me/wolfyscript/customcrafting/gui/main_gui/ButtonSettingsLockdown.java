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

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

class ButtonSettingsLockdown {

    public static final String KEY = "lockdown";

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, WolfyUtilsBukkit api, CustomCrafting customCrafting) {
        buttonBuilder.toggle(KEY).stateFunction((holder, cache, slot) -> customCrafting.getConfigHandler().getConfig().isLockedDown())
                .enabledState(state -> buildState(state.subKey("enabled"), true, api, customCrafting, Component.text("Are you sure you want to disable LockDown mode?", NamedTextColor.RED), Component.text("This will enable all the custom recipes!", NamedTextColor.RED, TextDecoration.BOLD)))
                .disabledState(state -> buildState(state.subKey("disabled"), false, api, customCrafting, Component.text("Are you sure you want to enable LockDown mode?", NamedTextColor.RED), Component.text("This will disable all the custom recipes!", NamedTextColor.RED, TextDecoration.BOLD)))
                .register();
    }

    private static void buildState(ButtonState.Builder<CCCache> state, boolean enabled, WolfyUtilsBukkit api, CustomCrafting customCrafting, Component... components){
        state.icon(Material.BARRIER).action((holder, cache, btn, slot, details) -> {
            if (ChatUtils.checkPerm(holder.getPlayer(), "customcrafting.cmd.lockdown")) {
                holder.getGuiHandler().close();
                api.getChat().sendMessages(holder.getPlayer(), components);
                holder.getWindow().sendMessage(holder.getGuiHandler(),
                        Component.text().append(
                                Component.text().append(Component.text("[", NamedTextColor.DARK_GRAY), Component.text("Yes", NamedTextColor.GREEN), Component.text("]", NamedTextColor.DARK_GRAY)).hoverEvent(HoverEvent.showText(Component.text("Yes, " + (enabled ? "disable" : "enable") + " lockdown mode!", NamedTextColor.GREEN))).clickEvent(api.getChat().executable(holder.getPlayer(), true, (wolfyUtilities, player1) -> {
                                    customCrafting.getConfigHandler().getConfig().setLockDown(!enabled);
                                    customCrafting.getConfigHandler().getConfig().save();
                                    wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster();
                                })),
                                Component.text(" -- ", NamedTextColor.GRAY),
                                Component.text().append(Component.text("[", NamedTextColor.DARK_GRAY), Component.text("No", NamedTextColor.RED), Component.text("]", NamedTextColor.DARK_GRAY)).hoverEvent(HoverEvent.showText(Component.text("No, leave lockdown mode " + (enabled ? "enabled" : "disabled") + "!", NamedTextColor.RED))).clickEvent(api.getChat().executable(holder.getPlayer(), true, (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster()))
                        ).build()
                );
            }
            return ButtonInteractionResult.cancel(true);
        });
    }

}
