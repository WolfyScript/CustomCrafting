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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.world.items.CustomItemData;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;

@KeyedStaticId(RecipeBookSettings.ID)
public class RecipeBookSettings extends CustomItemData {

    protected static final String ID = NamespacedKeyUtils.NAMESPACE + ":" + "recipe_book";

    private CustomCrafting customCrafting;
    private boolean enabled;

    public RecipeBookSettings(@JacksonInject CustomCrafting customCrafting) {
        super(customCrafting.getApi().getIdentifiers().getNamespaced(ID));
        this.customCrafting = customCrafting;
        this.enabled = false;
    }

    public RecipeBookSettings(RecipeBookSettings other) {
        super(other.customCrafting.getApi().getIdentifiers().getNamespaced(ID));
        this.enabled = other.enabled;
        this.customCrafting = other.customCrafting;
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
