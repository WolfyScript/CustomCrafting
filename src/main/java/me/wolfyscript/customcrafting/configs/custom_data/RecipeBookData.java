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

package me.wolfyscript.customcrafting.configs.custom_data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.world.items.CustomData;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import java.io.IOException;
import java.util.Objects;
import me.wolfyscript.customcrafting.CustomCrafting;

public class RecipeBookData extends CustomData implements Cloneable {

    private boolean enabled;

    protected RecipeBookData(NamespacedKey namespacedKey) {
        super(namespacedKey);
        this.enabled = false;
    }

    protected RecipeBookData(RecipeBookData recipeBookData) {
        super(recipeBookData);
        this.enabled = recipeBookData.enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void writeToJson(CustomItem customItem, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeBooleanField("enabled", enabled);
    }

    @Override
    protected void readFromJson(CustomItem customItem, JsonNode node, DeserializationContext deserializationContext) throws IOException {
        setEnabled(node.get("enabled").asBoolean(false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeBookData that)) return false;
        if (!super.equals(o)) return false;
        return enabled == that.enabled;
    }

    @Override
    public RecipeBookData clone() {
        return new RecipeBookData(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled);
    }

    public static class Provider extends CustomData.Provider<RecipeBookData> {

        public Provider() {
            super(CustomCrafting.RECIPE_BOOK_DATA, RecipeBookData.class);
        }

    }
}
