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

package me.wolfyscript.customcrafting.configs.customitem;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.items.CustomItemData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;

@KeyedStaticId(RecipeBookSettings.ID)
public class RecipeBookSettings extends CustomItemData {

    protected static final String ID = NamespacedKeyUtils.NAMESPACE + ":" + "recipebook";

    private boolean enabled;

    public RecipeBookSettings() {
        super(NamespacedKey.of(ID));
        this.enabled = false;
    }

    public RecipeBookSettings(RecipeBookSettings other) {
        super(NamespacedKey.of(ID));
        this.enabled = other.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public CustomItemData copy() {
        return new RecipeBookSettings(this);
    }
}
