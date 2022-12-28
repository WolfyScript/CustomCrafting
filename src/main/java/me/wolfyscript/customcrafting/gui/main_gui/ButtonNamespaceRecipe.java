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

import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.common.gui.GUIClickInteractionDetails;
import java.util.HashMap;
import java.util.Map;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.DisableRecipesHandler;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ButtonNamespaceRecipe {

    private static final String KEY = "recipe_list.namespace_";

    static String key(int slot) {
        return KEY + slot;
    }

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, int slot, CustomCrafting customCrafting) {
        buttonBuilder.action(key(slot)).state(state -> state.key("namespace").icon(Material.ENDER_CHEST).action((holder, cache, btn, btnSlot, details) -> {
            String namespace = cache.getRecipeList().getNamespaceForButtonInSlot(slot);
            if (!namespace.isEmpty() && details instanceof GUIClickInteractionDetails clickEvent) {
                if (!clickEvent.isShiftClick()) {
                    if (holder.getWindow() instanceof MenuListRecipes) {
                        cache.getRecipeList().setNamespace(namespace);
                        cache.getRecipeList().setPage(0);
                    }
                } else {
                    DisableRecipesHandler disableRecipesHandler = customCrafting.getDisableRecipesHandler();
                    if (namespace.equalsIgnoreCase("minecraft")) {
                        if (clickEvent.isLeftClick()) {
                            customCrafting.getDataHandler().getMinecraftRecipes().forEach(recipe -> disableRecipesHandler.disableBukkitRecipe(((Keyed) recipe).getKey()));
                        } else if (clickEvent.isRightClick()) {
                            customCrafting.getDataHandler().getMinecraftRecipes().forEach(recipe -> disableRecipesHandler.enableBukkitRecipe(((Keyed) recipe).getKey()));
                        }
                    } else if (clickEvent.isLeftClick()) {
                        customCrafting.getRegistries().getRecipes().get(namespace).forEach(disableRecipesHandler::disableRecipe);
                    } else if (clickEvent.isRightClick()) {
                        customCrafting.getRegistries().getRecipes().get(namespace).forEach(disableRecipesHandler::enableRecipe);
                    }
                }
            }
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, btnSlot, itemStack) -> {
            return CallbackButtonRender.Result.of(Placeholder.parsed("namespace", cache.getRecipeList().getNamespaceForButtonInSlot(slot)));
        })).register();
    }

}
