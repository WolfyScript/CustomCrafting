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
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.common.gui.GUIClickInteractionDetails;
import java.util.UUID;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class TabPlayerHead extends ItemCreatorTabVanilla {

    public static final String KEY = "player_head";

    public TabPlayerHead() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.PLAYER_HEAD, this);
        creator.getButtonBuilder().itemInput("player_head.texture.input").state(state -> state.icon(Material.AIR).action((holder, cache, btn, slot, details) -> {
            if (details instanceof GUIClickInteractionDetails clickDetails) {
                // TODO: Item Type Check return ((InventoryClickEvent) event).getCurrentItem().getType().equals(Material.PLAYER_HEAD);
            }
            return ButtonInteractionResult.cancel(false);
        })).register();
        creator.getButtonBuilder().action("player_head.texture.apply").state(state -> state.icon(Material.GREEN_CONCRETE).action((holder, cache, btn, slot, details) -> {
            var items = cache.getItems();
            if (holder.getInventory().getItem(38) != null && holder.getInventory().getItem(38).getType().equals(Material.PLAYER_HEAD)) {
                items.getItem().setPlayerHeadValue(new ItemBuilder(creator.getWolfyUtils(), holder.getInventory().getItem(38)).getPlayerHeadValue());
            }
            return ButtonInteractionResult.cancel(true);
        })).register();
        creator.getButtonBuilder().chatInput("player_head.owner").state(state -> state.icon(Material.NAME_TAG)).inputAction((guiHandler, player, s, args) -> {
            var itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            if (!(itemMeta instanceof SkullMeta)) {
                return true;
            }
            try {
                var uuid = UUID.fromString(args[0]);
                ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
            } catch (IllegalArgumentException e) {
                return true;
            }
            return false;
        }).register();
    }

    @Override
    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return items.getItem() != null && item.getType().equals(Material.PLAYER_HEAD);
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(29, "player_head.texture.input");
        update.setButton(30, "player_head.texture.apply");
        update.setButton(32, "player_head.owner");
    }
}
