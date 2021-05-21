package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExperienceCondition extends Condition {

    int expLevel = 0;

    public ExperienceCondition() {
        super("player_experience");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(ICustomRecipe<?, ?> recipe, Conditions.Data data) {
        if (data.getPlayer() != null) {
            int currentExp = data.getPlayer().getLevel();
            switch (option) {
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
                default:
                    return true;
            }
        }
        return true;
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeNumberField("experience", expLevel);
    }

    @Override
    public void readFromJson(JsonNode node) {
        this.expLevel = node.get("experience").asInt();
    }

    public float getExpLevel() {
        return expLevel;
    }

    public void setExpLevel(int expLevel) {
        this.expLevel = expLevel;
    }
}
