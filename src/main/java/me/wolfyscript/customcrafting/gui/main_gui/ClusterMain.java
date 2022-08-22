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
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.event.ClickEvent;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class ClusterMain extends CCCluster {

    public static final String KEY = "none";

    //Button keys
    public static final NamespacedKey BACK = new NamespacedKey(KEY, "back");
    public static final NamespacedKey EMPTY = new NamespacedKey(KEY, "empty");
    public static final NamespacedKey BACK_BOTTOM = new NamespacedKey(KEY, "back_bottom");
    public static final NamespacedKey GUI_HELP = new NamespacedKey(KEY, "gui_help");
    public static final NamespacedKey GLASS_GRAY = new NamespacedKey(KEY, "glass_gray");
    public static final NamespacedKey GLASS_LIGHT_GRAY = new NamespacedKey(KEY, "glass_light_gray");
    public static final NamespacedKey GLASS_WHITE = new NamespacedKey(KEY, "glass_white");
    public static final NamespacedKey GLASS_BLACK = new NamespacedKey(KEY, "glass_black");
    public static final NamespacedKey GLASS_RED = new NamespacedKey(KEY, "glass_red");
    public static final NamespacedKey GLASS_GREEN = new NamespacedKey(KEY, "glass_green");
    public static final NamespacedKey GLASS_PURPLE = new NamespacedKey(KEY, "glass_purple");
    public static final NamespacedKey GLASS_PINK = new NamespacedKey(KEY, "glass_pink");
    public static final NamespacedKey PATREON = new NamespacedKey(KEY, "patreon");
    public static final NamespacedKey YOUTUBE = new NamespacedKey(KEY, "youtube");
    public static final NamespacedKey DISCORD = new NamespacedKey(KEY, "discord");
    public static final NamespacedKey GITHUB = new NamespacedKey(KEY, "github");
    //Language keys
    public static final NamespacedKey BACKGROUND = new NamespacedKey(KEY, "background");
    //Both Button and Window keys
    public static final NamespacedKey RECIPE_LIST = new NamespacedKey(KEY, "recipe_list");
    //Window keys
    public static final NamespacedKey ITEM_LIST = new NamespacedKey(KEY, "item_list");
    //Messages
    private final Component githubLink;
    private final Component youtubeLink;
    private final Component discordLink;

    public ClusterMain(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
        githubLink = getChat().getMiniMessage().deserialize("<gray>[<aqua>Click here to go to GitHub</aqua>]</gray>").clickEvent(ClickEvent.openUrl("https://www.github.com/WolfyScript/"));
        youtubeLink = getChat().getMiniMessage().deserialize("<gray>[<aqua>Click here to go to YouTube</aqua>]</gray>").clickEvent(ClickEvent.openUrl("https://www.youtube.com/channel/UCTlqRLm4PxZuAI4nVN4X74g"));
        discordLink = getChat().getMiniMessage().deserialize("<gray>[<aqua>Click here to join Discord</aqua>]</gray>").clickEvent(ClickEvent.openUrl("https://discord.gg/qGhDTSr"));
    }

    @Override
    public void onInit() {
        ButtonBuilder<CCCache> bb = getButtonBuilder();
        bb.dummy(EMPTY.getKey()).state(s -> s.icon(Material.AIR)).register();
        bb.dummy(GLASS_GRAY.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.GRAY_STAINED_GLASS_PANE)).register();
        bb.dummy(GLASS_LIGHT_GRAY.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.LIGHT_GRAY_STAINED_GLASS_PANE)).register();
        bb.dummy(GLASS_BLACK.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.BLACK_STAINED_GLASS_PANE)).register();
        bb.dummy(GLASS_RED.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.RED_STAINED_GLASS_PANE)).register();
        bb.dummy(GLASS_WHITE.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.WHITE_STAINED_GLASS_PANE)).register();
        bb.dummy(GLASS_GREEN.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.GREEN_STAINED_GLASS_PANE)).register();
        bb.dummy(GLASS_PURPLE.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.PURPLE_STAINED_GLASS_PANE)).register();
        bb.dummy(GLASS_PINK.getKey()).state(state -> state.key(BACKGROUND.getKey()).icon(Material.PINK_STAINED_GLASS_PANE)).register();
        bb.dummy(GUI_HELP.getKey()).state(state -> state.key(GUI_HELP.getKey() + "_on").icon(PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"))
                .render((cache, guiHandler, player, guiInv, stack, slot) -> {
                    guiInv.getWindow().getHelpInformation();
                    var window = guiInv.getWindow();
                    return CallbackButtonRender.UpdateResult.of(TagResolverUtil.entries(guiHandler.getApi().getLanguageAPI().getComponents("inventories." + window.getCluster().getId() + "." + window.getNamespacedKey().getKey() + ".gui_help")));
                })).register();
        final ButtonAction<CCCache> backAction = (cache, guiHandler, player, guiInventory, i, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        };
        bb.action(BACK.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action(backAction)).register();
        bb.action(BACK_BOTTOM.getKey()).state(state -> state.icon(Material.BARRIER).action(backAction)).register();
        bb.action(PATREON.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275")).action((cache, guiHandler, player, guiInventory, i, event) -> {
            guiHandler.openWindow("patrons_menu");
            return true;
        })).register();
        bb.action(GITHUB.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("26e27da12819a8b053da0cc2b62dec4cda91de6eeec21ccf3bfe6dd8d4436a7")).action((cache, guiHandler, player, guiInventory, i, event) -> {
            getChat().sendMessage(player, githubLink);
            return true;
        })).register();
        bb.action(YOUTUBE.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("b4353fd0f86314353876586075b9bdf0c484aab0331b872df11bd564fcb029ed")).action((cache, guiHandler, player, guiInventory, i, event) -> {
            getChat().sendMessage(player, youtubeLink);
            return true;
        })).register();
        bb.action(DISCORD.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("4d42337be0bdca2128097f1c5bb1109e5c633c17926af5fb6fc20000011aeb53")).action((cache, guiHandler, player, guiInventory, i, event) -> {
            getChat().sendMessage(player, discordLink);
            return true;
        })).register();
        bb.action(RECIPE_LIST.getKey()).state(state -> state.icon(Material.WRITTEN_BOOK).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.getCustomCache().setSetting(Setting.RECIPE_LIST);
            guiHandler.openWindow(RECIPE_LIST.getKey());
            return true;
        })).register();
        bb.action(ITEM_LIST.getKey()).state(state -> state.icon(Material.BOOKSHELF).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.getCustomCache().setSetting(Setting.ITEM_LIST);
            guiHandler.openWindow(ITEM_LIST.getKey());
            return true;
        })).register();
        registerGuiWindow(new MenuMain(this, customCrafting));
        registerGuiWindow(new MenuListCustomItem(this, customCrafting));
        registerGuiWindow(new MenuListRecipes(this, customCrafting));
        registerGuiWindow(new MenuSettings(this, customCrafting));
        registerGuiWindow(new MenuPatrons(this, customCrafting));
    }
}
