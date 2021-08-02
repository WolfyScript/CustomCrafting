package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WorldTimeCondition extends Condition {

    long time = 0;

    public WorldTimeCondition() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_time"));
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        long currentTime = data.getBlock().getWorld().getTime();
        return switch (option) {
            case EXACT -> currentTime == time;
            case LOWER -> currentTime < time;
            case LOWER_EXACT -> currentTime <= time;
            case HIGHER -> currentTime > time;
            case HIGHER_EXACT -> currentTime >= time;
            case HIGHER_LOWER -> currentTime < time || currentTime > time;
            default -> true;
        };
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
