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

package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAlias;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPropertyOrder({"id", "icon", "name", "description"})
public class CategoryFilter extends CategorySettings {

    @JsonIgnore
    private final Set<Material> totalMaterials;
    protected Set<CreativeModeTab> creativeModeTabs;
    protected Set<Material> materials;

    public CategoryFilter() {
        super();
        this.creativeModeTabs = new HashSet<>();
        this.materials = new HashSet<>();
        this.totalMaterials = new HashSet<>();
    }

    public CategoryFilter(CategoryFilter category) {
        super(category);
        this.creativeModeTabs = new HashSet<>(category.creativeModeTabs);
        this.materials = new HashSet<>(category.materials);
        this.totalMaterials = new HashSet<>();
    }

    @JsonGetter
    public Set<Material> getMaterials() {
        return materials;
    }

    @JsonSetter
    public void setMaterials(Set<Material> materials) {
        this.materials = materials;
        this.totalMaterials.addAll(materials);
    }

    @JsonGetter
    public Set<CreativeModeTab> getCreativeModeTabs() {
        return creativeModeTabs;
    }

    @JsonSetter
    @JsonAlias({"itemCategories"})
    public void setCreativeModeTabs(Set<CreativeModeTab> itemCategories) {
        this.creativeModeTabs = itemCategories;
        this.totalMaterials.addAll(itemCategories.stream().flatMap(creativeModeTab -> creativeModeTab.getMaterials().stream()).collect(Collectors.toSet()));
    }

    public boolean filter(RecipeContainer container) {
        if (!groups.isEmpty() && container.getGroup() != null && !groups.contains(container.getGroup())) {
            return false;
        }
        if (container.getRecipe() != null && ((!recipes.isEmpty() && !recipes.contains(container.getRecipe())) || (!folders.isEmpty() && !folders.contains(container.getRecipe().getNamespace())))) {
            return false;
        }
        return container.isValid(totalMaterials);
    }

    @Override
    public void writeToByteBuf(MCByteBuf byteBuf) {
        super.writeToByteBuf(byteBuf);
        writeData(byteBuf);
        writeStringArray(totalMaterials.stream().map(material -> material.getKey().toString()).toList(), byteBuf);

    }
}
