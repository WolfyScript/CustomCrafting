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

package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;

public class ExtensionPackLoader extends ResourceLoader {

    public ExtensionPackLoader(CustomCrafting customCrafting) {
        super(customCrafting, new NamespacedKey(customCrafting, "extension_loader"));
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public boolean save(CustomRecipe<?> recipe) {
        return false;
    }

    @Override
    public boolean save(CustomItem item) {
        return false;
    }

    @Override
    public boolean delete(CustomRecipe<?> recipe) {
        return false;
    }

    @Override
    public boolean delete(CustomItem item) {
        return false;
    }
}
