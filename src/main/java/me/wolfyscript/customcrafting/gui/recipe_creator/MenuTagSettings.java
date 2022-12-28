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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuTagSettings extends CCWindow {

    public MenuTagSettings(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, "tag_settings", 54, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action("add_tag_list").state(s -> s.icon(Material.NAME_TAG).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().openWindow("tag_list");
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().action("next_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((holder, cache, btn, slot, details) -> {
            int page = cache.getRecipeCreatorCache().getTagSettingsCache().getListPage();
            cache.getRecipeCreatorCache().getTagSettingsCache().setListPage(++page);
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().action("previous_page").state(s -> s.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((holder, cache, btn, slot, details) -> {
            int page = cache.getRecipeCreatorCache().getTagSettingsCache().getListPage();
            if (page > 0) {
                cache.getRecipeCreatorCache().getTagSettingsCache().setListPage(--page);
            }
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var tagsCache = update.getGuiHandler().getCustomCache().getRecipeCreatorCache().getTagSettingsCache();
        var recipeItemStack = tagsCache.getRecipeItemStack();
        update.setButton(0, ClusterMain.BACK);
        update.setButton(8, ClusterMain.GUI_HELP);
        if (recipeItemStack != null) {
            NamespacedKey[] tags = recipeItemStack.getTags().toArray(new NamespacedKey[0]);
            int page = tagsCache.getListPage();
            int maxPages = tags.length / 45 + (tags.length % 45 > 0 ? 1 : 0);
            if (page > maxPages) {
                tagsCache.setListPage(maxPages);
            }
            if (page > 0) {
                update.setButton(2, "previous_page");
            }
            if (page + 1 < maxPages) {
                update.setButton(4, "next_page");
            }
            for (int i = 45 * page, invSlot = 9; i < tags.length && invSlot < getSize() - 9; i++, invSlot++) {
                final var key = tags[i];
                if (getButton("tag."+key.toString(".")) == null) {
                    getButtonBuilder().action("tag." + key.toString(".")).state(state -> state.key("tag_container").icon(Material.NAME_TAG).action((holder, cache, btn, slot, details) -> {
                        if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                            var currentRecipeStack = cache.getRecipeCreatorCache().getTagSettingsCache().getRecipeItemStack();
                            if (currentRecipeStack != null) {
                                currentRecipeStack.getTags().remove(key);
                            }
                        }
                        return true;
                    }).render((holder, cache, btn, slot, itemStack) -> {
                        return CallbackButtonRender.Result.of(Placeholder.parsed("namespaced_key", key.toString()));
                    })).register();
                }
                update.setButton(invSlot, "tag."+key.toString("."));
            }
        }
        update.setButton(49, "add_tag_list");
    }
}
