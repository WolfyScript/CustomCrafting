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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.DeserializationContext;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.util.Objects;

public class EliteWorkbenchData extends CustomData implements Cloneable {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "elite_crafting_table");

    private boolean advancedRecipes;
    private boolean enabled;
    private boolean allowHoppers;
    private boolean keepItems;
    private int gridSize;

    protected EliteWorkbenchData(NamespacedKey namespacedKey) {
        super(namespacedKey);
        this.enabled = false;
        this.gridSize = 3;
        this.allowHoppers = false;
        this.keepItems = false;
    }

    protected EliteWorkbenchData(EliteWorkbenchData eliteWorkbenchData) {
        super(eliteWorkbenchData);
        this.enabled = eliteWorkbenchData.enabled;
        this.gridSize = eliteWorkbenchData.gridSize;
        this.allowHoppers = eliteWorkbenchData.allowHoppers;
        this.keepItems = eliteWorkbenchData.keepItems;
        this.advancedRecipes = eliteWorkbenchData.advancedRecipes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAdvancedRecipes() {
        return advancedRecipes;
    }

    public void setAdvancedRecipes(boolean advancedRecipes) {
        this.advancedRecipes = advancedRecipes;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void setAllowHoppers(boolean allowHoppers) {
        this.allowHoppers = allowHoppers;
    }

    public boolean isAllowHoppers() {
        return allowHoppers;
    }

    public void setKeepItems(boolean keepItems) {
        this.keepItems = keepItems;
    }

    public boolean isKeepItems() {
        return keepItems;
    }

    @Override
    public void writeToJson(CustomItem customItem, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeBooleanField("enabled", enabled);
        gen.writeNumberField("gridSize", gridSize);
        gen.writeBooleanField("advancedRecipes", advancedRecipes);
        gen.writeBooleanField("allowHoppers", allowHoppers);
        gen.writeBooleanField("keepItems", keepItems);
    }

    @Override
    protected void readFromJson(CustomItem customItem, JsonNode node, DeserializationContext deserializationContext) throws IOException {
        setEnabled(node.get("enabled").asBoolean(false));
        setGridSize(node.get("gridSize").asInt(3));
        setAdvancedRecipes(node.get("advancedRecipes").asBoolean(false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EliteWorkbenchData that)) return false;
        if (!super.equals(o)) return false;
        return advancedRecipes == that.advancedRecipes &&
                enabled == that.enabled &&
                allowHoppers == that.allowHoppers &&
                keepItems == that.keepItems &&
                gridSize == that.gridSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), advancedRecipes, enabled, allowHoppers, keepItems, gridSize);
    }

    @Override
    public String toString() {
        return "EliteWorkbenchData{" +
                "advancedRecipes=" + advancedRecipes +
                ", enabled=" + enabled +
                ", allowHoppers=" + allowHoppers +
                ", keepItems=" + keepItems +
                ", gridSize=" + gridSize +
                "} " + super.toString();
    }

    @Override
    public EliteWorkbenchData clone() {
        return new EliteWorkbenchData(this);
    }

    public static class Provider extends CustomData.Provider<EliteWorkbenchData> {

        public Provider() {
            super(CustomCrafting.ELITE_CRAFTING_TABLE_DATA, EliteWorkbenchData.class);
        }

    }
}
