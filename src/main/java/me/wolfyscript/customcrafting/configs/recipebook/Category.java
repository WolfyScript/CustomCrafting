package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonAlias;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    private String id = "";
    private Material icon;
    private String name;
    private List<String> description;

    private List<NamespacedKey> recipes;
    private List<Material> materials;
    @JsonAlias({"itemCategories"})
    private List<CreativeModeTab> creativeModeTabs;

    public Category() {
        this.name = "";
        this.icon = Material.CHEST;
        this.description = new ArrayList<>();
        this.recipes = new ArrayList<>();
        this.creativeModeTabs = new ArrayList<>();
        this.materials = new ArrayList<>();
    }

    public Category(Category category) {
        this.name = category.name;
        this.icon = category.getIcon();
        this.description = new ArrayList<>(category.getDescription());
        this.recipes = new ArrayList<>(category.getRecipes());
        this.creativeModeTabs = new ArrayList<>(category.getCreativeModeTabs());
        this.materials = new ArrayList<>(category.getMaterials());
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

    @JsonGetter
    public List<NamespacedKey> getRecipes() {
        return recipes;
    }

    @JsonSetter
    public void setRecipes(List<NamespacedKey> recipes) {
        this.recipes = recipes;
    }

    @JsonGetter
    public List<Material> getMaterials() {
        return materials;
    }

    @JsonSetter
    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    @JsonGetter
    public List<CreativeModeTab> getCreativeModeTabs() {
        return creativeModeTabs;
    }

    @JsonSetter
    public void setCreativeModeTabs(List<CreativeModeTab> itemCategories) {
        this.creativeModeTabs = itemCategories;
    }

    public boolean isValid(ICustomRecipe<?> recipe) {
        if (recipes.isEmpty()) return false;
        return recipes.contains(recipe.getNamespacedKey());
    }

    public boolean isValid(Material material) {
        if (creativeModeTabs.stream().anyMatch(itemCategory -> {
            if (itemCategory.equals(CreativeModeTab.SEARCH)) return true;
            return itemCategory.isValid(material);
        })) {
            return true;
        }
        return materials.contains(material);
    }

}
