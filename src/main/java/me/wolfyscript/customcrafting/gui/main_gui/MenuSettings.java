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
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuSettings extends CCWindow {

    static final List<String> availableLangs = new ArrayList<>();

    private static final String DARK_MODE = "darkMode";
    private static final String PRETTY_PRINTING = "pretty_printing";
    private static final String ADVANCED_CRAFTING_TABLE = "advanced_workbench";
    private static final String DEBUG = "debug";
    private static final String DRAW_BACKGROUND = "draw_background";
    private static final String RECIPE_BOOK_KEEP_LAST = "recipe_book_keep_last_open";

    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    public MenuSettings(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "settings", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ButtonSettingsLockdown(api, customCrafting));
        registerButton(new ButtonSettingsLanguage(availableLangs, api, customCrafting));
        ButtonBuilder<CCCache> bb = getButtonBuilder();
        bb.toggle(DARK_MODE).stateFunction((cache, guiHandler, player, guiInventory, i) -> PlayerUtil.getStore(player).isDarkMode())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.BLACK_CONCRETE).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                    PlayerUtil.getStore(player).setDarkMode(false);
                    return true;
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.WHITE_CONCRETE).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                    PlayerUtil.getStore(player).setDarkMode(true);
                    return true;
                })).register();
        bb.toggle(PRETTY_PRINTING).stateFunction((ccCache, guiHandler, player, guiInventory, i) -> customCrafting.getConfigHandler().getConfig().isPrettyPrinting())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.WRITABLE_BOOK).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setPrettyPrinting(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.WRITABLE_BOOK).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setPrettyPrinting(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).register();
        bb.toggle(ADVANCED_CRAFTING_TABLE).stateFunction((ccCache, guiHandler, player, guiInventory, i) -> customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.CRAFTING_TABLE).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.CRAFTING_TABLE).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).register();
        bb.toggle(DEBUG).stateFunction((ccCache, guiHandler, player, guiInventory, i) -> api.hasDebuggingMode())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.REDSTONE).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().set("debug", false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.REDSTONE).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().set("debug", true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).register();
        bb.toggle("creator.reset_after_save").stateFunction((ccCache, guiHandler, player, guiInventory, i) -> customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave())
                .enabledState(state -> state.subKey(ENABLED).icon(PlayerHeadUtils.getViaURL("c65cb185c641cbe74e70bce6e6a1ed90a180ec1a42034d5c4aed57af560fc83a")).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).disabledState(state -> state.subKey(DISABLED).icon(PlayerHeadUtils.getViaURL("e551153a1519357b6241ab1ddcae831dff080079c0b2960797c702dd92266835")).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).register();
        bb.toggle(DRAW_BACKGROUND).stateFunction((ccCache, guiHandler, player, guiInventory, i) -> customCrafting.getConfigHandler().getConfig().isGUIDrawBackground())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.BLACK_STAINED_GLASS).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setGUIDrawBackground(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.BLACK_STAINED_GLASS).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setGUIDrawBackground(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).register();
        bb.toggle(RECIPE_BOOK_KEEP_LAST).stateFunction((ccCache, guiHandler, player, guiInventory, i) -> customCrafting.getConfigHandler().getConfig().isRecipeBookKeepLastOpen())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.KNOWLEDGE_BOOK).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setRecipeBookKeepLastOpen(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.KNOWLEDGE_BOOK).action((cache, guiHandler, player, inventory, slot, event) -> {
                    customCrafting.getConfigHandler().getConfig().setRecipeBookKeepLastOpen(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return true;
                })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        availableLangs.clear();
        File langFolder = new File(customCrafting.getDataFolder() + File.separator + "lang");
        String[] filenames = langFolder.list((dir, name) -> name.endsWith(".json"));
        if (filenames != null) {
            availableLangs.addAll(Arrays.stream(filenames).map(s -> s.replace(".json", "")).distinct().toList());
        }
        Player player = event.getPlayer();
        event.setButton(0, ClusterMain.BACK);
        if (ChatUtils.checkPerm(player, "customcrafting.cmd.lockdown")) {
            event.setButton(9, ButtonSettingsLockdown.KEY);
        }
        if (ChatUtils.checkPerm(player, "customcrafting.cmd.darkmode")) {
            event.setButton(10, DARK_MODE);
        }
        if (ChatUtils.checkPerm(player, "customcrafting.cmd.settings")) {
            event.setButton(11, PRETTY_PRINTING);
            event.setButton(12, ADVANCED_CRAFTING_TABLE);
            event.setButton(13, ButtonSettingsLanguage.KEY);
            event.setButton(14, "creator.reset_after_save");
            event.setButton(15, DRAW_BACKGROUND);
            event.setButton(16, RECIPE_BOOK_KEEP_LAST);
        }
    }
}
