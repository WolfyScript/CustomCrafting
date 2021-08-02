package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategorySettings {

    protected Set<String> groups;
    protected Set<String> namespaces;
    protected Set<NamespacedKey> recipes;
    private String id = "";
    private Material icon;
    private String name;
    private List<String> description;

    public CategorySettings() {
        this.name = "";
        this.icon = Material.CHEST;
        this.groups = new HashSet<>();
        this.namespaces = new HashSet<>();
        this.description = new ArrayList<>();
        this.recipes = new HashSet<>();
    }

    public CategorySettings(CategorySettings category) {
        this.id = category.id;
        this.name = category.name;
        this.icon = category.getIcon();
        this.groups = new HashSet<>(category.groups);
        this.namespaces = new HashSet<>(category.namespaces);
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

    @JsonGetter
    public Material getIcon() {
        return icon;
    }

    @JsonSetter
    public void setIcon(Material icon) {
        this.icon = icon;
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

    public Set<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(Set<String> namespaces) {
        this.namespaces = namespaces;
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
        var categoryItem = new ItemStack(getIcon());
        var itemMeta = categoryItem.getItemMeta();
        var languageAPI = WolfyUtilities.get(customCrafting).getLanguageAPI();
        itemMeta.setDisplayName(languageAPI.replaceColoredKeys(getName()));
        itemMeta.setLore(languageAPI.replaceColoredKeys(getDescription()));
        categoryItem.setItemMeta(itemMeta);
        return categoryItem;
    }

    public boolean isValid(ICustomRecipe<?, ?> recipe) {
        if (recipes.isEmpty()) return false;
        return recipes.contains(recipe.getNamespacedKey());
    }

    @Override
    public String toString() {
        return "CategorySettings{" +
                "id='" + id + '\'' +
                ", groups=" + groups +
                ", namespaces=" + namespaces +
                ", recipes=" + recipes +
                '}';
    }

    public void writeToByteBuf(MCByteBuf byteBuf) {
        byteBuf.writeItemStack(new ItemStack(this.icon));
        byteBuf.writeUtf(this.name);
    }

    protected void writeData(MCByteBuf byteBuf) {
        writeStringArray(new ArrayList<>(this.groups), byteBuf);
        writeStringArray(new ArrayList<>(this.namespaces), byteBuf);
        writeStringArray(this.recipes.stream().map(NamespacedKey::toString).toList(), byteBuf);
    }

    protected void writeStringArray(List<String> values, MCByteBuf byteBuf) {
        byteBuf.writeVarInt(values.size());
        values.forEach(byteBuf::writeUtf);
    }
}
