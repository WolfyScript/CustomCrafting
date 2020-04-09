package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorldBiomeCondition extends Condition {

    private List<String> biomes;

    public WorldBiomeCondition() {
        super("world_biome");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT);
        this.biomes = new ArrayList<>();
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (data.getBlock() != null) {
            return biomes.contains(data.getBlock().getBiome().toString());
        }
        return false;
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = (JsonObject) super.toJsonElement();
        JsonArray jsonArray = new JsonArray();
        biomes.forEach(s -> jsonArray.add(s));
        jsonObject.add("biomes", jsonArray);
        return jsonObject;
    }

    @Override
    public void fromJsonElement(JsonElement jsonElement) {
        JsonObject jsonObject = (JsonObject) jsonElement;
        JsonArray jsonArray = jsonObject.getAsJsonArray("biomes");
        Iterator<JsonElement> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            if (element instanceof JsonPrimitive) {
                addBiome(element.getAsString());
            }
        }
    }

    public void addBiome(String biome) {
        if (!this.biomes.contains(biome)) {
            this.biomes.add(biome);
        }
    }

    public List<String> getBiomes() {
        return biomes;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (String eliteWorkbench : biomes) {
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }
}
