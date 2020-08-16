package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldNameCondition extends Condition {

    private final List<String> worldNames;

    public WorldNameCondition() {
        super("world_name");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT);
        this.worldNames = new ArrayList<>();
    }

    @Override
    public boolean check(ICustomRecipe recipe, Conditions.Data data) {
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
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeArrayFieldStart("names");
        for (String s : worldNames) {
            gen.writeString(s);
        }
        gen.writeEndArray();
    }

    @Override
    public void readFromJson(JsonNode node) {
        JsonNode array = node.get("names");
        array.elements().forEachRemaining(element -> {
            if(element.isValueNode()){
                addWorldName(element.asText());
            }
        });
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
