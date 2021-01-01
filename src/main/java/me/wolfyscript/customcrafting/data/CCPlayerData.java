package me.wolfyscript.customcrafting.data;

import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.DeserializationContext;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.entity.CustomPlayerData;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CCPlayerData extends CustomPlayerData {

    private boolean darkMode;
    private int totalCrafts;
    private int advancedCrafts;
    private int normalCrafts;

    private Map<NamespacedKey, Integer> crafts;

    private CCPlayerData() {
        this.darkMode = false;
        this.totalCrafts = 0;
        this.advancedCrafts = 0;
        this.normalCrafts = 0;
        this.crafts = new HashMap<>();
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public int getTotalCrafts() {
        return totalCrafts;
    }

    public void setTotalCrafts(int totalCrafts) {
        this.totalCrafts = totalCrafts;
    }

    public int getAdvancedCrafts() {
        return advancedCrafts;
    }

    public void setAdvancedCrafts(int advancedCrafts) {
        this.advancedCrafts = advancedCrafts;
    }

    public int getNormalCrafts() {
        return normalCrafts;
    }

    public void setNormalCrafts(int normalCrafts) {
        this.normalCrafts = normalCrafts;
    }

    public void increaseTotalCrafts(int increase) {
        this.totalCrafts += increase;
    }

    public void increaseAdvancedCrafts(int increase) {
        this.advancedCrafts += increase;
    }

    public void increaseNormalCrafts(int increase) {
        this.normalCrafts += increase;
    }

    public int getRecipeCrafts(NamespacedKey recipeKey) {
        return crafts.getOrDefault(recipeKey, 0);
    }

    public void increaseRecipeCrafts(NamespacedKey recipeKey, int increase) {
        crafts.put(recipeKey, getRecipeCrafts(recipeKey) + increase);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeBooleanField("dark_mode", darkMode);
        gen.writeNumberField("total_crafts", totalCrafts);
        gen.writeNumberField("advanced_crafts", advancedCrafts);
        gen.writeNumberField("normal_crafts", normalCrafts);
        gen.writeObjectField("crafts", crafts);
    }

    @Override
    protected void readFromJson(JsonNode node, DeserializationContext deserializationContext) throws IOException {
        darkMode = node.path("dark_mode").asBoolean(false);
        totalCrafts = node.path("total_crafts").asInt(0);
        advancedCrafts = node.path("advanced_crafts").asInt(0);
        normalCrafts = node.path("normal_crafts").asInt(0);
        crafts = JacksonUtil.getObjectMapper().convertValue(node.path("crafts"), new TypeReference<Map<NamespacedKey, Integer>>() {
        });
    }

    @Override
    public String toString() {
        return "CCPlayerData{" +
                "darkMode=" + darkMode +
                ", totalCrafts=" + totalCrafts +
                ", advancedCrafts=" + advancedCrafts +
                ", normalCrafts=" + normalCrafts +
                ", crafts=" + crafts +
                "} ";
    }

    public static class Provider extends CustomPlayerData.Provider<CCPlayerData> {

        public Provider() {
            super(new NamespacedKey("customcrafting", "data"), CCPlayerData.class);
        }
    }
}
