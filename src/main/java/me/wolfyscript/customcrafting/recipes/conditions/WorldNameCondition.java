package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorldNameCondition extends Condition {

    private List<String> worldNames;

    public WorldNameCondition() {
        super("world_name");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT);
        this.worldNames = new ArrayList<>();
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (data.getBlock() != null) {
            World world = data.getBlock().getLocation().getWorld();
            return worldNames.contains(world.getName());
        }
        return false;
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = (JsonObject) super.toJsonElement();
        JsonArray jsonArray = new JsonArray();
        worldNames.forEach(s -> jsonArray.add(s));
        jsonObject.add("names", jsonArray);
        return jsonObject;
    }

    @Override
    public void fromJsonElement(JsonElement jsonElement) {
        JsonObject jsonObject = (JsonObject) jsonElement;
        JsonArray jsonArray = jsonObject.getAsJsonArray("names");
        Iterator<JsonElement> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            if (element instanceof JsonPrimitive) {
                addWorldName(element.getAsString());
            }
        }
    }

    public void addWorldName(String worldName) {
        if (!this.worldNames.contains(worldName)) {
            this.worldNames.add(worldName);
        }
    }

    public List<String> getWorldNames() {
        return worldNames;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (String eliteWorkbench : worldNames) {
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }
}
