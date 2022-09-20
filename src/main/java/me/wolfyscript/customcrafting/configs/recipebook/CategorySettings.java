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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAlias;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CategorySettings {

    protected Set<String> groups;
    protected Set<String> folders;
    protected Set<NamespacedKey> recipes;
    private String id = "";
    private ItemStack icon;
    private String name;
    private List<String> description;

    protected CategorySettings() {
        this.name = "";
        this.icon = new ItemStack(Material.CHEST);
        this.groups = new HashSet<>();
        this.folders = new HashSet<>();
        this.description = new ArrayList<>();
        this.recipes = new HashSet<>();
    }

    protected CategorySettings(CategorySettings category) {
        this.id = category.id;
        this.name = category.name;
        this.icon = category.getIconStack();
        this.groups = new HashSet<>(category.groups);
        this.folders = new HashSet<>(category.folders);
        this.description = new ArrayList<>(category.getDescription());
        this.recipes = new HashSet<>(category.getRecipes());
    }

    @JsonGetter
    public String getId() {
        return id;
    }

    @JsonSetter
    void setId(String id) {
        this.id = id;
    }

    @JsonGetter("icon")
    private Object getJsonIconStack() {
        if (icon.hasItemMeta() || icon.getAmount() > 1) {
            return icon;
        }
        return icon.getType();
    }

    @JsonSetter("icon")
    private void setJsonIconStack(JsonNode icon) {
        if (icon.isTextual()) {
            this.icon = new ItemStack(Objects.requireNonNull(Material.matchMaterial(icon.asText())));
        } else if (icon.isObject()) {
            this.icon = CustomCrafting.inst().getApi().getJacksonMapperUtil().getGlobalMapper().convertValue(icon, ItemStack.class);
        } else {
            this.icon = new ItemStack(Material.CHEST);
        }
    }

    @JsonIgnore
    public ItemStack getIconStack() {
        return icon;
    }

    @JsonIgnore
    public void setIconStack(ItemStack icon) {
        this.icon = icon;
    }

    @JsonIgnore
    @Deprecated
    public void setIcon(Material icon) {
        this.icon = new ItemStack(icon);
    }

    @JsonIgnore
    @Deprecated
    public Material getIcon() {
        return icon.getType();
    }

    @JsonGetter
    public String getName() {
        return name;
    }

    @JsonSetter
    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter
    public List<String> getDescription() {
        return description;
    }

    @JsonSetter
    public void setDescription(List<String> description) {
        this.description = description;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    @JsonAlias("namespaces")
    @JsonGetter
    public Set<String> getFolders() {
        return folders;
    }

    @JsonAlias("namespaces")
    @JsonSetter
    public void setFolders(Set<String> folders) {
        this.folders = folders;
    }

    @JsonGetter
    public Set<NamespacedKey> getRecipes() {
        return recipes;
    }

    @JsonSetter
    public void setRecipes(Set<NamespacedKey> recipes) {
        this.recipes = recipes;
    }

    public ItemStack createItemStack(CustomCrafting customCrafting) {
        var categoryItem = getIconStack().clone();
        var itemMeta = categoryItem.getItemMeta();
        var languageAPI = customCrafting.getApi().getLanguageAPI();
        var miniMsg = customCrafting.getApi().getChat().getMiniMessage();
        if (getName().contains("§")) {
            itemMeta.setDisplayName(getName());
        } else {
            itemMeta.setDisplayName(BukkitComponentSerializer.legacy().serialize(miniMsg.deserialize(languageAPI.replaceKeys(getName()))));
        }
        itemMeta.setLore(languageAPI.replaceKeys(getDescription()).stream().map(s -> s.contains("§") ? s : BukkitComponentSerializer.legacy().serialize(miniMsg.deserialize(languageAPI.convertLegacyToMiniMessage(s)))).toList());
        categoryItem.setItemMeta(itemMeta);
        return categoryItem;
    }

    public boolean isValid(CustomRecipe<?> recipe) {
        if (recipes.isEmpty()) return false;
        return recipes.contains(recipe.getNamespacedKey());
    }

    @Override
    public String toString() {
        return "CategorySettings{" +
                "id='" + id + '\'' +
                ", groups=" + groups +
                ", namespaces=" + folders +
                ", recipes=" + recipes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategorySettings that = (CategorySettings) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void writeToByteBuf(MCByteBuf byteBuf) {
        byteBuf.writeItemStack(new ItemStack(this.icon));
        byteBuf.writeUtf(this.name);
    }

    protected void writeData(MCByteBuf byteBuf) {
        writeStringArray(new ArrayList<>(this.groups), byteBuf);
        writeStringArray(new ArrayList<>(this.folders), byteBuf);
        writeStringArray(this.recipes.stream().map(NamespacedKey::toString).toList(), byteBuf);
    }

    protected void writeStringArray(List<String> values, MCByteBuf byteBuf) {
        byteBuf.writeVarInt(values.size());
        values.forEach(byteBuf::writeUtf);
    }
}
