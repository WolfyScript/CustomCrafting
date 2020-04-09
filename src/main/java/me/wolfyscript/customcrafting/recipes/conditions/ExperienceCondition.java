package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;

public class ExperienceCondition extends Condition {

    int expLevel = 0;

    public ExperienceCondition() {
        super("player_experience");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        int currentExp = data.getPlayer().getLevel();
        switch (option) {
            case IGNORE:
                return true;
            case EXACT:
                return currentExp == expLevel;
            case LOWER:
                return currentExp < expLevel;
            case LOWER_EXACT:
                return currentExp <= expLevel;
            case HIGHER:
                return currentExp > expLevel;
            case HIGHER_EXACT:
                return currentExp >= expLevel;
            case HIGHER_LOWER:
                return currentExp < expLevel || currentExp > expLevel;
        }
        return true;
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = (JsonObject) super.toJsonElement();
        jsonObject.addProperty("experience", expLevel);
        return jsonObject;
    }

    @Override
    public void fromJsonElement(JsonElement jsonElement) {
        this.expLevel = ((JsonObject) jsonElement).getAsJsonPrimitive("experience").getAsInt();
    }

    public float getExpLevel() {
        return expLevel;
    }

    public void setExpLevel(int expLevel) {
        this.expLevel = expLevel;
    }
}
