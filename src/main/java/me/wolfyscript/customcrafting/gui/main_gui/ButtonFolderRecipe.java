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

import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.DisableRecipesHandler;
import me.wolfyscript.customcrafting.registry.RegistryRecipes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ButtonFolderRecipe {

    private static final String KEY = "recipe_list.folder_";

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, int slot, String namespace, String folder, CustomCrafting customCrafting) {
        buttonBuilder.action(key(slot, namespace, folder)).state(state -> state.key("folder").icon(Material.CHEST).action((cache, guiHandler, player, guiInventory, btn, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent) {
                if (!clickEvent.isShiftClick()) {
                    if (guiInventory.getWindow() instanceof MenuListRecipes) {
                        cache.getRecipeList().setFolder(folder);
                        cache.getRecipeList().setPage(0);
                    }
                } else {
                    DisableRecipesHandler disableRecipesHandler = customCrafting.getDisableRecipesHandler();
                    RegistryRecipes recipes = customCrafting.getRegistries().getRecipes();
                    if (clickEvent.isLeftClick()) {
                        recipes.get(namespace, folder).forEach(disableRecipesHandler::disableRecipe);
                    } else if (clickEvent.isRightClick()) {
                        recipes.get(namespace, folder).forEach(disableRecipesHandler::enableRecipe);
                    }
                }
            }
            return true;
        }).render((cache, guiHandler, player, inventory, itemStack, invSlot, help) -> CallbackButtonRender.UpdateResult.of(Placeholder.parsed("folder", folder)))).register();
    }

    static String key(int slot, String namespace, String folder) {
        return KEY + slot + "." + namespace + "." + folder;
    }
}
