package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Category {

    private Material icon;
    private String name;
    private List<String> description;

    private List<NamespacedKey> recipes;
    private List<Material> materials;
    private List<CreativeModeTab> creativeModeTabs;

    public Category() {
        this.name = "";
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
    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<NamespacedKey> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<NamespacedKey> recipes) {
        this.recipes = recipes;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public static Category readFromJson(JsonNode node){
        Category category = new Category();
        String displayName = node.get("name").asText();
        Material icon = Material.matchMaterial(node.get("icon").asText());
        List<String> description = new ArrayList<>();
        if (node.has("description")) {
            node.get("description").forEach(jsonElement -> description.add(jsonElement.asText()));
        }
        List<NamespacedKey> recipes = new ArrayList<>();
        if (node.has("recipes")) {
            node.get("recipes").forEach(jsonElement -> {
                String[] recipe = jsonElement.asText().split(":");
                recipes.add(new NamespacedKey(recipe[0], recipe[1]));
            });
        }
        List<Material> materials = new ArrayList<>();
        if (node.has("materials")) {
            node.get("materials").forEach(jsonElement -> {
                Material material = Material.matchMaterial(jsonElement.asText());
                if (material != null) {
                    materials.add(material);
                }
            });
        }
        List<CreativeModeTab> itemCategories = new ArrayList<>();
        if (node.has("itemCategories")) {
            node.get("itemCategories").forEach(jsonElement -> {
                String materialNmn = jsonElement.asText();
                try {
                    CreativeModeTab itemCategory = CreativeModeTab.valueOf(materialNmn);
                    itemCategories.add(itemCategory);
                } catch (IllegalArgumentException ex) {
                    Bukkit.getLogger().warning("Failed to load ItemCategory for Category: must be BREWING, BUILDING_BLOCKS, DECORATIONS, COMBAT, TOOLS, REDSTONE,  FOOD, TRANSPORTATION, MISC, SEARCH! Got " + materialNmn);
                }
            });
        }
        category.setIcon(icon);
        category.setName(displayName);
        category.setDescription(description);
        category.setCreativeModeTabs(itemCategories);
        category.setMaterials(materials);
        category.setRecipes(recipes);
        return category;
    }

    public List<CreativeModeTab> getCreativeModeTabs() {
        return creativeModeTabs;
    }

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

    public void writeToJson(JsonGenerator gen) throws IOException {
        gen.writeStringField("icon", getIcon().name());
        gen.writeStringField("name", getName());
        gen.writeArrayFieldStart("description");
        for (String s : getDescription()) {
            gen.writeString(s);
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("materials");
        for (Material material : getMaterials()) {
            gen.writeString(material.toString());
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("recipes");
        for (NamespacedKey recipe : getRecipes()) {
            gen.writeString(recipe.toString());
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("itemCategories");
        for (CreativeModeTab creativeModeTab : getCreativeModeTabs()) {
            gen.writeString(creativeModeTab.toString());
        }
        gen.writeEndArray();
    }


}
