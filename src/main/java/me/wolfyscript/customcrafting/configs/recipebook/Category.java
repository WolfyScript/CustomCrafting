package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.ItemCategory;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
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
    private List<ItemCategory> itemCategories;

    public Category() {
        this.name = "";
        this.description = new ArrayList<>();
        this.recipes = new ArrayList<>();
        this.itemCategories = new ArrayList<>();
        this.materials = new ArrayList<>();
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

    public List<ItemCategory> getItemCategories() {
        return itemCategories;
    }

    public void setItemCategories(List<ItemCategory> itemCategories) {
        this.itemCategories = itemCategories;
    }

    public boolean isValid(ICustomRecipe recipe) {
        if (recipes.isEmpty()) return false;
        return recipes.contains(recipe.getNamespacedKey());
    }

    public boolean isValid(Material material) {
        if (itemCategories.stream().anyMatch(itemCategory -> {
            if (itemCategory.equals(ItemCategory.SEARCH)) return true;
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
        for (ItemCategory category : getItemCategories()) {
            gen.writeString(category.toString());
        }
        gen.writeEndArray();
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
        List<ItemCategory> itemCategories = new ArrayList<>();
        if (node.has("itemCategories")) {
            node.get("itemCategories").forEach(jsonElement -> {
                String materialNmn = jsonElement.asText();
                try {
                    ItemCategory itemCategory = ItemCategory.valueOf(materialNmn);
                    itemCategories.add(itemCategory);
                } catch (IllegalArgumentException ex) {
                    Bukkit.getLogger().warning("Failed to load ItemCategory for Category: must be BREWING, BUILDING_BLOCKS, DECORATIONS, COMBAT, TOOLS, REDSTONE,  FOOD, TRANSPORTATION, MISC, SEARCH! Got " + materialNmn);
                }
            });
        }
        category.setIcon(icon);
        category.setName(displayName);
        category.setDescription(description);
        category.setItemCategories(itemCategories);
        category.setMaterials(materials);
        category.setRecipes(recipes);
        return category;
    }
}
