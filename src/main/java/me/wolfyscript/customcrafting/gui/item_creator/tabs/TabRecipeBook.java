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
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.configs.customitem.RecipeBookSettings;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabRecipeBook extends ItemCreatorTab {

    public static final String KEY = "knowledge_book";

    public TabRecipeBook() {
        super(new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilsBukkit api) {
        ButtonOption.register(creator.getButtonBuilder(), Material.KNOWLEDGE_BOOK, this);
        creator.getButtonBuilder().toggle("knowledge_book.toggle").stateFunction((cache, guiHandler, player, guiInventory, i) ->
                cache.getItems().getItem().getData(RecipeBookSettings.class).map(RecipeBookSettings::isEnabled)
                        // Get old recipe book settings
                        .orElse(((RecipeBookData) cache.getItems().getItem().getCustomData(CustomCrafting.RECIPE_BOOK_DATA)).isEnabled())).enabledState(state -> state.subKey("knowledge_book.toggle.enabled").icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, inventory, btn, i, event) -> {
            var items = cache.getItems();
            items.getItem().computeDataIfAbsent(RecipeBookSettings.class, id -> new RecipeBookSettings(creator.getCustomCrafting())).setEnabled(false);
            return true;
        })).disabledState(state -> state.subKey("knowledge_book.toggle.disabled").icon(Material.RED_CONCRETE).action((cache, guiHandler, player, inventory, btn, i, event) -> {
            var items = cache.getItems();
            items.getItem().computeDataIfAbsent(RecipeBookSettings.class, id -> new RecipeBookSettings(creator.getCustomCrafting())).setEnabled(true);
            return true;
        })).register();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(31, "knowledge_book.toggle");
    }
}
