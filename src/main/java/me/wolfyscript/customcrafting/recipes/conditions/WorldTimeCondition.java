package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;

public class WorldTimeCondition extends Condition {

    long time = 0;

    public WorldTimeCondition() {
        super("world_time");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        long currentTime = data.getBlock().getWorld().getTime();
        switch (option) {
            case IGNORE:
                return true;
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
        }
        return true;
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = (JsonObject) super.toJsonElement();
        jsonObject.addProperty("time", time);
        return jsonObject;
    }

    @Override
    public void fromJsonElement(JsonElement jsonElement) {
        this.time = ((JsonObject)jsonElement).getAsJsonPrimitive("time").getAsInt();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
