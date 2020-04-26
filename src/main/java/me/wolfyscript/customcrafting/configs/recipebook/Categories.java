package me.wolfyscript.customcrafting.configs.recipebook;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Categories {

    private List<String> sortedMainCategories;
    private List<String> sortedSwitchCategories;

    private HashMap<String, Category> mainCategories = new HashMap<>();
    private HashMap<String, Category> switchCategories = new HashMap<>();

    public Categories(List<String> sortedMainCategories, List<String> sortedSwitchCategories) {
        this.sortedSwitchCategories = sortedSwitchCategories;
        this.sortedMainCategories = sortedMainCategories;
    }

    public Categories() {
        this.sortedSwitchCategories = new ArrayList<>();
        this.sortedMainCategories = new ArrayList<>();
    }

    public void registerMainCategory(String key, Category category) {
        if (!sortedMainCategories.contains(key)) {
            sortedMainCategories.add(key);
        }
        mainCategories.put(key, category);
    }

    public void registerSwitchCategory(String key, Category category) {
        if (!sortedSwitchCategories.contains(key)) {
            sortedSwitchCategories.add(key);
        }
        switchCategories.put(key, category);
    }

    public Category getSwitchCategory(String key) {
        return switchCategories.get(key);
    }

    public Category getSwitchCategory(int index) {
        if (getSortedSwitchCategories().isEmpty()) return null;
        return getSwitchCategory(getSortedSwitchCategories().get(index));
    }

    public List<String> getSortedMainCategories() {
        return sortedMainCategories;
    }

    public void setSortedMainCategories(List<String> sortedMainCategories) {
        this.sortedMainCategories = sortedMainCategories;
    }

    public Category getMainCategory(String key) {
        return mainCategories.get(key);
    }

    public Category getMainCategory(int index) {
        if (getSortedMainCategories().isEmpty()) return null;
        return getMainCategory(getSortedMainCategories().get(index));
    }

    public List<String> getSortedSwitchCategories() {
        return sortedSwitchCategories;
    }

    public void setSortedSwitchCategories(List<String> sortedSwitchCategories) {
        this.sortedSwitchCategories = sortedSwitchCategories;
    }

    public static class Serializer implements JsonSerializer<Categories>, JsonDeserializer<Categories> {

        @Override
        public Categories deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Categories categories = new Categories();
            if (jsonObject.has("main")) {
                JsonObject mainCategories = jsonObject.getAsJsonObject("main");
                ArrayList<String> sortedMainList = new ArrayList<>();
                if (mainCategories.has("sort")) {
                    JsonArray sortedMain = mainCategories.getAsJsonArray("sort");
                    sortedMain.forEach(jsonElement -> sortedMainList.add(jsonElement.getAsString()));
                }
                categories.setSortedMainCategories(sortedMainList);

                mainCategories.getAsJsonArray("options").forEach(entry -> {
                    JsonObject object = entry.getAsJsonObject();
                    categories.registerMainCategory(object.get("id").getAsString(), context.deserialize(object, Category.class));
                });
            }
            if (jsonObject.has("switch")) {
                JsonObject switchCategories = jsonObject.getAsJsonObject("switch");
                ArrayList<String> sortedSwitchList = new ArrayList<>();
                if (switchCategories.has("sort")) {
                    JsonArray sortedSwitch = switchCategories.getAsJsonArray("sort");
                    sortedSwitch.forEach(jsonElement -> sortedSwitchList.add(jsonElement.getAsString()));
                }
                categories.setSortedSwitchCategories(sortedSwitchList);

                switchCategories.getAsJsonArray("options").forEach(entry -> {
                    JsonObject object = entry.getAsJsonObject();
                    categories.registerSwitchCategory(object.get("id").getAsString(), context.deserialize(object, Category.class));
                });
            }
            return categories;
        }

        @Override
        public JsonElement serialize(Categories categories, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();

            JsonObject mainCategories = new JsonObject();
            JsonArray options = new JsonArray();
            categories.mainCategories.forEach((key, category) -> {
                JsonObject jsonObject = (JsonObject) context.serialize(category, Category.class);
                jsonObject.addProperty("id", key);
                options.add(jsonObject);
            });
            mainCategories.add("options", options);
            root.add("main", mainCategories);

            JsonObject switchCategories = new JsonObject();
            JsonArray options1 = new JsonArray();
            categories.switchCategories.forEach((key, category) -> {
                JsonObject jsonObject = (JsonObject) context.serialize(category, Category.class);
                jsonObject.addProperty("id", key);
                options1.add(jsonObject);
            });
            switchCategories.add("options", options1);
            root.add("switch", switchCategories);

            return root;
        }
    }

}
