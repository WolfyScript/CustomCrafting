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
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabPermission extends ItemCreatorTab {

    public static final String KEY = "permission";

    public TabPermission() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        GuiMenuComponent.ButtonBuilder<CCCache> bB = creator.getButtonBuilder();
        ButtonOption.register(bB, Material.BARRIER, this);
        bB.chatInput("permission.set").state(state -> state.icon(Material.GREEN_CONCRETE).render((cache, guiHandler, player, guiInventory, button, itemStack, i) -> {
            String perm = guiHandler.getCustomCache().getItems().getItem().getPermission();
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("var", perm.isEmpty() ? "none" : perm));
        })).inputAction((guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().setPermission(s.replace(" ", "."));
            return false;
        }).register();
        bB.action("permission.remove").state(state -> state.icon(Material.RED_CONCRETE_POWDER).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            guiHandler.getCustomCache().getItems().getItem().setPermission("");
            return true;
        })).register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "permission.set");
        update.setButton(32, "permission.remove");
    }
}
