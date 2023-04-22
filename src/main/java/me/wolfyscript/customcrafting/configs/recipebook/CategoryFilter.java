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

import java.util.Locale;
import java.util.Objects;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAlias;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

@JsonPropertyOrder({"id", "icon", "name", "description"})
public class CategoryFilter extends CategorySettings {

    @JsonIgnore
    private final Set<Material> totalMaterials;
    protected Set<CreativeModeTab> creativeModeTabs;
    protected Set<Material> items;
    protected Set<Tag<Material>> tags;
    private ContentSortation sort;

    public CategoryFilter() {
        super();
        this.creativeModeTabs = new HashSet<>();
        this.items = new HashSet<>();
        this.totalMaterials = new HashSet<>();
        this.tags = new HashSet<>();
    }

    public CategoryFilter(CategoryFilter category) {
        super(category);
        this.creativeModeTabs = new HashSet<>(category.creativeModeTabs);
        this.items = new HashSet<>(category.items);
        this.totalMaterials = new HashSet<>();
        this.tags = new HashSet<>(category.tags);
    }

    public void setSort(ContentSortation sort) {
        this.sort = sort;
    }

    public ContentSortation getSort() {
        return sort;
    }

    @JsonGetter
    public Set<Material> getItems() {
        return items;
    }

    @JsonAlias("materials")
    @JsonSetter
    public void setItems(Set<String> materials) {
        this.items = materials.stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(Collectors.toSet());
        this.totalMaterials.addAll(this.items);
    }

    @JsonSetter
    public void setTags(Set<String> tags) {
        this.tags = tags.stream().map(s -> {
            NamespacedKey namespacedKey = NamespacedKey.fromString(s);
            return Bukkit.getTag("items", namespacedKey != null ? namespacedKey : NamespacedKey.minecraft(s.toLowerCase(Locale.ROOT).replace(" ", "_")), Material.class);
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @JsonGetter
    private Set<String> getTags() {
        return tags.stream().map(materialTag -> materialTag.getKey().toString()).collect(Collectors.toSet());
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
        return tags.stream().anyMatch(materialTag -> container.isValid(materialTag.getValues())) || container.isValid(totalMaterials);
    }

    @Override
    public void writeToByteBuf(MCByteBuf byteBuf) {
        super.writeToByteBuf(byteBuf);
        writeData(byteBuf);
        writeStringArray(totalMaterials.stream().map(material -> material.getKey().toString()).toList(), byteBuf);

    }

}
