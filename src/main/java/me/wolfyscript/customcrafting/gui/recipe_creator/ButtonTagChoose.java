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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import me.wolfyscript.customcrafting.data.CCCache;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.Tag;

class ButtonTagChoose {

    static String key(Tag<Material> tag) {
        return "tag." + BukkitNamespacedKey.fromBukkit(tag.getKey()).toString(".");
    }

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, Tag<Material> tag) {
        buttonBuilder.action(key(tag)).state(state -> state.key("tag").icon(Material.NAME_TAG).action((cache, guiHandler, player, guiInventory, btn, slot, event) -> {
            var recipeItemStack = cache.getRecipeCreatorCache().getTagSettingsCache().getRecipeItemStack();
            if (recipeItemStack != null) {
                recipeItemStack.getTags().add(BukkitNamespacedKey.fromBukkit(tag.getKey()));
            }
            guiHandler.openPreviousWindow();
            return true;
        }).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> {
            return CallbackButtonRender.UpdateResult.of(Placeholder.parsed("namespaced_key", BukkitNamespacedKey.fromBukkit(tag.getKey()).toString()));
        })).register();
    }
}
