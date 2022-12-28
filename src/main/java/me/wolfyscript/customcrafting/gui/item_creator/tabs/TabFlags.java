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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TabFlags extends ItemCreatorTabVanilla {

    public static final String KEY = "flags";

    public TabFlags() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        var bB = creator.getButtonBuilder();
        ButtonOption.register(bB, Material.WRITTEN_BOOK, this);
        register(bB, "enchants", ItemFlag.HIDE_ENCHANTS, Material.ENCHANTING_TABLE);
        register(bB, "attributes", ItemFlag.HIDE_ATTRIBUTES, Material.ENCHANTED_GOLDEN_APPLE);
        register(bB, "unbreakable", ItemFlag.HIDE_UNBREAKABLE, Material.BEDROCK);
        register(bB, "destroys", ItemFlag.HIDE_DESTROYS, Material.TNT);
        register(bB, "placed_on", ItemFlag.HIDE_PLACED_ON, Material.GRASS_BLOCK);
        register(bB, "potion_effects", ItemFlag.HIDE_POTION_EFFECTS, Material.POTION);
        register(bB, "dye", ItemFlag.HIDE_DYE, Material.YELLOW_DYE);
    }

    public static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, String flagId, ItemFlag itemFlag, Material material) {
        buttonBuilder.toggle("flags." + flagId).stateFunction((holder, cache, slot) -> {
            CustomItem item = cache.getItems().getItem();
            return !ItemUtils.isAirOrNull(item) && item.getItemMeta().hasItemFlag(itemFlag);
        }).enabledState(state -> state.subKey("flags." + flagId + ".enabled").icon(material).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().getCustomCache().getItems().getItem().removeItemFlags(itemFlag);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(state -> state.subKey("flags." + flagId + ".disabled").icon(material).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().getCustomCache().getItems().getItem().addItemFlags(itemFlag);
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(28, "flags.attributes");
        update.setButton(30, "flags.unbreakable");
        update.setButton(32, "flags.destroys");
        update.setButton(34, "flags.placed_on");
        update.setButton(38, "flags.potion_effects");
        update.setButton(40, "flags.dye");
        update.setButton(42, "flags.enchants");
    }
}
