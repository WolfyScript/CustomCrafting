package me.wolfyscript.customcrafting.configs.recipebook;

import com.google.gson.*;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.utils.ItemCategory;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Type;
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

    public boolean isValid(CustomRecipe recipe) {
        if (recipes.isEmpty()) return false;
        return recipes.contains(new NamespacedKey(recipe.getNamespacedKey().getNamespace(), recipe.getNamespacedKey().getKey()));
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

    public static class Serializer implements JsonSerializer<Category>, JsonDeserializer<Category> {

        @Override
        public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            //This could be useful if I can't parse in the WolfyUtilies and CustomCrafting oject! -> WolfyUtilities api = WolfyUtilities.getAPI(Bukkit.getPluginManager().getPlugin("CustomCrafting"));

            JsonObject jsonObject = json.getAsJsonObject();
            Category category = new Category();

            String displayName = jsonObject.get("name").getAsString();
            Material icon = Material.matchMaterial(jsonObject.get("icon").getAsString());

            List<String> description = new ArrayList<>();
            if (jsonObject.has("description")) {
                jsonObject.getAsJsonArray("description").forEach(jsonElement -> description.add(jsonElement.getAsString()));
            }

            List<NamespacedKey> recipes = new ArrayList<>();
            if (jsonObject.has("recipes")) {
                jsonObject.getAsJsonArray("recipes").forEach(jsonElement -> {
                    String[] recipe = jsonElement.getAsString().split(":");
                    recipes.add(new NamespacedKey(recipe[0], recipe[1]));
                });
            }

            List<Material> materials = new ArrayList<>();
            if (jsonObject.has("materials")) {
                jsonObject.getAsJsonArray("materials").forEach(jsonElement -> {
                    Material material = Material.matchMaterial(jsonElement.getAsString());
                    if (material != null) {
                        materials.add(material);
                    }
                });
            }

            List<ItemCategory> itemCategories = new ArrayList<>();
            if (jsonObject.has("itemCategories")) {
                jsonObject.getAsJsonArray("itemCategories").forEach(jsonElement -> {
                    String materialNmn = jsonElement.getAsString();
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

        @Override
        public JsonElement serialize(Category category, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();

            root.addProperty("icon", category.getIcon().name());
            root.addProperty("name", category.getName());

            JsonArray description = new JsonArray();
            category.getDescription().forEach(s -> description.add(s));
            root.add("description", description);

            JsonArray materials = new JsonArray();
            category.getMaterials().forEach(material -> materials.add(material.name()));
            root.add("materials", materials);

            JsonArray recipes = new JsonArray();
            category.getRecipes().forEach(namespacedKey -> recipes.add(namespacedKey.toString()));
            root.add("recipes", recipes);

            JsonArray itemCategories = new JsonArray();
            category.getItemCategories().forEach(itemCategory -> itemCategories.add(itemCategory.name()));
            root.add("itemCategories", itemCategories);
            return root;
        }
    }
}
