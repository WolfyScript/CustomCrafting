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

package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import org.bukkit.util.StringUtil;

public class TabEnchants extends ItemCreatorTabVanilla {

    public static final String KEY = "enchantments";
    private static final List<String> NUMBERS = new ArrayList<>();
    static {
        for (int i = 0; i < 256; i++) {
            NUMBERS.add(String.valueOf(i));
        }
    }

    public TabEnchants() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.ENCHANTED_BOOK, this));

        new ChatInputButton.Builder<>(creator, KEY + ".add")
                .state(state -> state.icon(Material.ENCHANTED_BOOK).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                    var chat = guiInventory.getWindow().getChat();
                    chat.sendMessage(player, chat.translated("msg.input.wui_command"));
                    return true;
                }))
                .tabComplete((guiHandler, player, args) -> {
                    if (args.length == 1) {
                        return StringUtil.copyPartialMatches(args[0], Arrays.stream(Enchantment.values()).map(enchantment -> enchantment.getKey().toString()).toList(), new ArrayList<>());
                    } else if (args.length == 2) {
                        return StringUtil.copyPartialMatches(args[1], NUMBERS, new ArrayList<>());
                    }
                    return null;
                })
                .inputAction((guiHandler, player, s, args) -> {
                    if (args.length > 1) {
                        int level;
                        try {
                            level = Integer.parseInt(args[args.length - 1]);
                        } catch (NumberFormatException ex) {
                            creator.sendMessage(guiHandler, creator.translatedMsgKey("enchant.invalid_lvl"));
                            return true;
                        }
                        var enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(args[0]));
                        if (enchantment != null) {
                            guiHandler.getCustomCache().getItems().getItem().addUnsafeEnchantment(enchantment, level);
                        } else {
                            creator.sendMessage(guiHandler, creator.translatedMsgKey("enchant.invalid_enchant", Placeholder.unparsed("enchant", args[0])));
                            return true;
                        }
                    } else {
                        creator.sendMessage(guiHandler, creator.translatedMsgKey("enchant.no_lvl"));
                        return true;
                    }
                    return false;
                })
                .register();
        new ChatInputButton.Builder<>(creator, KEY + ".remove")
                .state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                    var chat = guiInventory.getWindow().getChat();
                    chat.sendMessage(player, chat.translated("msg.input.wui_command"));
                    return true;
                }))
                .tabComplete((guiHandler, player, args) -> {
                    if (args.length > 0) {
                        return StringUtil.copyPartialMatches(args[0],guiHandler.getCustomCache().getItems().getItem().getItemMeta().getEnchants().keySet().stream().map(enchantment -> enchantment.getKey().toString()).toList(), new ArrayList<>());
                    }
                    return null;
                })
                .inputAction((guiHandler, player, s, args) -> {
                    var enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(args[0]));
                    if (enchantment != null) {
                        guiHandler.getCustomCache().getItems().getItem().removeEnchantment(enchantment);
                    } else {
                        creator.sendMessage(guiHandler, creator.translatedMsgKey("enchant.invalid_enchant", Placeholder.unparsed("enchant", args[0])));
                        return true;
                    }
                    return false;
                })
                .register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".add");
        update.setButton(32, KEY + ".remove");
    }
}
