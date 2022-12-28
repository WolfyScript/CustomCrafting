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

import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.common.gui.GUIClickInteractionDetails;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuSettings extends CCWindow {

    static final List<String> availableLangs = new ArrayList<>();

    private static final String DARK_MODE = "darkMode";
    private static final String LANGUAGE = "language";
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
        ButtonBuilder<CCCache> bb = getButtonBuilder();
        ButtonSettingsLockdown.register(bb, api, customCrafting);
        registerLanguageButton(bb, availableLangs, api, customCrafting);
        bb.toggle(DARK_MODE).stateFunction((holder, cache, slot) -> PlayerUtil.getStore(holder.getPlayer()).isDarkMode())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.BLACK_CONCRETE).action((holder, cache, btn, slot, details) -> {
                    PlayerUtil.getStore(holder.getPlayer()).setDarkMode(false);
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.WHITE_CONCRETE).action((holder, cache, btn, slot, details) -> {
                    PlayerUtil.getStore(holder.getPlayer()).setDarkMode(true);
                    return ButtonInteractionResult.cancel(true);
                })).register();
        bb.toggle(PRETTY_PRINTING).stateFunction((holder, cache, slot) -> customCrafting.getConfigHandler().getConfig().isPrettyPrinting())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.WRITABLE_BOOK).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setPrettyPrinting(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.WRITABLE_BOOK).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setPrettyPrinting(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).register();
        bb.toggle(ADVANCED_CRAFTING_TABLE).stateFunction((holder, cache, slot) -> customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.CRAFTING_TABLE).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.CRAFTING_TABLE).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setAdvancedWorkbenchEnabled(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).register();
        bb.toggle(DEBUG).stateFunction((holder, cache, slot) -> customCrafting.getConfigHandler().getConfig().getBoolean("debug", false))
                .enabledState(state -> state.subKey(ENABLED).icon(Material.REDSTONE).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().set("debug", false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.REDSTONE).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().set("debug", true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).register();
        bb.toggle("creator.reset_after_save").stateFunction((holder, cache, slot) -> customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave())
                .enabledState(state -> state.subKey(ENABLED).icon(PlayerHeadUtils.getViaURL("c65cb185c641cbe74e70bce6e6a1ed90a180ec1a42034d5c4aed57af560fc83a")).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey(DISABLED).icon(PlayerHeadUtils.getViaURL("e551153a1519357b6241ab1ddcae831dff080079c0b2960797c702dd92266835")).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setResetCreatorAfterSave(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).register();
        bb.toggle(DRAW_BACKGROUND).stateFunction((holder, cache, slot) -> customCrafting.getConfigHandler().getConfig().isGUIDrawBackground())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.BLACK_STAINED_GLASS).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setGUIDrawBackground(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.BLACK_STAINED_GLASS).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setGUIDrawBackground(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).register();
        bb.toggle(RECIPE_BOOK_KEEP_LAST).stateFunction((holder, cache, slot) -> customCrafting.getConfigHandler().getConfig().isRecipeBookKeepLastOpen())
                .enabledState(state -> state.subKey(ENABLED).icon(Material.KNOWLEDGE_BOOK).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setRecipeBookKeepLastOpen(false);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey(DISABLED).icon(Material.KNOWLEDGE_BOOK).action((holder, cache, btn, slot, details) -> {
                    customCrafting.getConfigHandler().getConfig().setRecipeBookKeepLastOpen(true);
                    customCrafting.getConfigHandler().getConfig().save();
                    return ButtonInteractionResult.cancel(true);
                })).register();
    }

    static void registerLanguageButton(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, List<String> availableLangs, WolfyUtilsBukkit api, CustomCrafting customCrafting) {
        buttonBuilder.action(LANGUAGE).state(state -> state.icon(Material.BOOKSHELF).action((holder, cache, btn, slot, details) -> {
            int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
            int nextIndex = index;
            if (details instanceof GUIClickInteractionDetails clickEvent) {
                if (clickEvent.isLeftClick() && !clickEvent.isShiftClick()) {
                    nextIndex = (index + 1 < availableLangs.size()) ? index + 1 : 0;
                } else if (clickEvent.isRightClick() && !clickEvent.isShiftClick()) {
                    nextIndex = index - 1 >= 0 ? index - 1 : availableLangs.size() - 1;
                } else if (clickEvent.isShiftClick()) {
                    if (ChatUtils.checkPerm(holder.getPlayer(), "customcrafting.cmd.reload")) {
                        api.getChat().sendMessage(holder.getPlayer(), "&eReloading Inventories and Languages!");
                        customCrafting.getApi().getLanguageAPI().unregisterLanguages();
                        customCrafting.getConfigHandler().getConfig().reload();
                        customCrafting.getConfigHandler().loadLang();
                        customCrafting.getApi().getInventoryAPI().reset();
                        api.getChat().sendMessage(holder.getPlayer(), "&aReload complete! Reloaded GUIs and languages");
                        holder.getGuiHandler().close();
                        return ButtonInteractionResult.cancel(true);
                    }
                    return ButtonInteractionResult.cancel(true);
                }
            }
            customCrafting.getConfigHandler().getConfig().setLanguage(availableLangs.get(nextIndex));
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> {
            int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
            return CallbackButtonRender.Result.of(TagResolverUtil.entries(availableLangs.stream().map(s -> Component.text(s).asComponent()).toList(), Component.empty(), index));
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
            event.setButton(13, LANGUAGE);
            event.setButton(14, "creator.reset_after_save");
            event.setButton(15, DRAW_BACKGROUND);
            event.setButton(16, RECIPE_BOOK_KEEP_LAST);
        }
    }
}
