package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonParser;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.DeserializationContext;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(using = Categories.Serializer.class)
@JsonDeserialize(using = Categories.Deserializer.class)
public class Categories {

    private List<String> sortedMainCategories;
    private List<String> sortedSwitchCategories;

    private final HashMap<String, Category> mainCategories = new HashMap<>();
    private final HashMap<String, Category> switchCategories = new HashMap<>();

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

    public void removeSwitchCategory(String key) {
        sortedSwitchCategories.remove(key);
        switchCategories.remove(key);
    }

    public void removeMainCategory(String key) {
        sortedMainCategories.remove(key);
        mainCategories.remove(key);
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

    @Override
    public String toString() {
        return "Categories{" +
                "sortedMainCategories=" + sortedMainCategories +
                ", sortedSwitchCategories=" + sortedSwitchCategories +
                ", mainCategories=" + mainCategories +
                ", switchCategories=" + switchCategories +
                '}';
    }

    public static class Serializer extends StdSerializer<Categories> {

        public Serializer() {
            super(Categories.class);
        }

        protected Serializer(Class<Categories> t) {
            super(t);
        }

        @Override
        public void serialize(Categories categories, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartObject();
            {
                gen.writeObjectFieldStart("main");
                {
                    gen.writeArrayFieldStart("sort");
                    for (String sortedMainCategory : categories.getSortedMainCategories()) {
                        gen.writeString(sortedMainCategory);
                    }
                    gen.writeEndArray();
                    gen.writeArrayFieldStart("options");
                    for (Map.Entry<String, Category> entry : categories.mainCategories.entrySet()) {
                        gen.writeStartObject();
                        entry.getValue().writeToJson(gen);
                        gen.writeStringField("id", entry.getKey());
                        gen.writeEndObject();
                    }
                    gen.writeEndArray();
                }
                gen.writeEndObject();

                gen.writeObjectFieldStart("switch");
                {
                    gen.writeArrayFieldStart("sort");
                    for (String sortedSwitchCategory : categories.getSortedSwitchCategories()) {
                        gen.writeString(sortedSwitchCategory);
                    }
                    gen.writeEndArray();
                    gen.writeArrayFieldStart("options");
                    for (Map.Entry<String, Category> entry : categories.switchCategories.entrySet()) {
                        gen.writeStartObject();
                        entry.getValue().writeToJson(gen);
                        gen.writeStringField("id", entry.getKey());
                        gen.writeEndObject();
                    }
                    gen.writeEndArray();
                }
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<Categories> {

        public Deserializer() {
            super(Categories.class);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Categories deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.readValueAsTree();
            Categories categories = new Categories();
            if (node.has("main")) {
                JsonNode mainCategories = node.path("main");
                ArrayList<String> sortedMainList = new ArrayList<>();
                if (mainCategories.has("sort")) {
                    JsonNode sortedMain = mainCategories.path("sort");
                    sortedMain.elements().forEachRemaining(element -> sortedMainList.add(element.asText()));
                }
                categories.setSortedMainCategories(sortedMainList);
                mainCategories.path("options").elements().forEachRemaining(element -> categories.registerMainCategory(element.path("id").asText(), Category.readFromJson(element)));
            }
            if (node.has("switch")) {
                JsonNode switchCategories = node.path("switch");
                ArrayList<String> sortedSwitchList = new ArrayList<>();
                if (switchCategories.has("sort")) {
                    JsonNode sortedSwitch = switchCategories.path("sort");
                    sortedSwitch.elements().forEachRemaining(jsonElement -> sortedSwitchList.add(jsonElement.asText()));
                }
                categories.setSortedSwitchCategories(sortedSwitchList);
                switchCategories.path("options").elements().forEachRemaining(element -> categories.registerSwitchCategory(element.path("id").asText(), Category.readFromJson(element)));
            }
            return categories;
        }
    }

}
