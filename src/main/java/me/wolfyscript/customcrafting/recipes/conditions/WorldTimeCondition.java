package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WorldTimeCondition extends Condition {

    long time = 0;

    public WorldTimeCondition() {
        super("world_time");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(ICustomRecipe<?, ?> recipe, Conditions.Data data) {
        long currentTime = data.getBlock().getWorld().getTime();
        switch (option) {
            case EXACT:
                return currentTime == time;
            case LOWER:
                return currentTime < time;
            case LOWER_EXACT:
                return currentTime <= time;
            case HIGHER:
                return currentTime > time;
            case HIGHER_EXACT:
                return currentTime >= time;
            case HIGHER_LOWER:
                return currentTime < time || currentTime > time;
            default:
                return true;
        }
    }

    @Override
    public void readFromJson(JsonNode node) {
        this.time = node.get("time").asInt();
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeNumberField("time", time);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
