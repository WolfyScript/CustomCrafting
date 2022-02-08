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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.DisableRecipesHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class ButtonNamespaceRecipe extends ActionButton<CCCache> {

    private static final String KEY = "recipe_list.namespace_";
    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler<CCCache>, String> namespaces = new HashMap<>();

    public ButtonNamespaceRecipe(int slot, CustomCrafting customCrafting) {
        super(key(slot), new ButtonState<>("namespace", Material.ENDER_CHEST));
        this.customCrafting = customCrafting;
    }

    static String key(int slot) {
        return KEY + slot;
    }

    @Override
    public void init(GuiWindow<CCCache> guiWindow) {
        getState().setAction((cache, guiHandler, player, inventory, slot, event) -> {
            String namespace = getNamespace(guiHandler);
            if (!namespace.isEmpty() && event instanceof InventoryClickEvent clickEvent) {
                if (!clickEvent.isShiftClick()) {
                    if (guiWindow instanceof MenuListRecipes) {
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
            return true;
        });
        getState().setRenderAction((hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%namespace%", getNamespace(guiHandler));
            return itemStack;
        });
        super.init(guiWindow);
    }

    public String getNamespace(GuiHandler<CCCache> guiHandler) {
        return namespaces.getOrDefault(guiHandler, "");
    }

    public void setNamespace(GuiHandler<CCCache> guiHandler, String namespace) {
        namespaces.put(guiHandler, namespace);
    }
}
