package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

public class WeatherCondition extends Condition {

    private Weather weather;

    public WeatherCondition() {
        super("weather");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT);
        this.weather = Weather.NONE;
    }

    public Weather getWeather() {
        return weather;
    }

    public void toggleWeather() {
        int index = weather.ordinal();
        if (index < Weather.values().length - 1) {
            index++;
        } else {
            index = 0;
        }
        weather = Weather.values()[index];
    }

    @Override
    public boolean check(ICustomRecipe<?, ?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        Block block = data.getBlock();
        if (block != null) {
            World world = block.getWorld();
            switch (weather) {
                case NONE:
                    return !world.isThundering() && !world.hasStorm();
                case STORM:
                    return world.hasStorm() && !world.isThundering();
                case THUNDER:
                    return world.isThundering() && !world.hasStorm();
                case STORM_THUNDER:
                    return world.isThundering() && world.hasStorm();
            }
        }
        return false;
    }

    @Override
    public void readFromJson(JsonNode node) {
        try {
            this.weather = Weather.valueOf(node.get("weather").asText());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeStringField("weather", weather.toString());

    }

    public enum Weather {
        STORM, THUNDER, STORM_THUNDER, NONE;

        private final String display;

        Weather() {
            this.display = "$inventories.recipe_creator.conditions.items.weather.modes." + super.toString().toLowerCase(Locale.ROOT) + "$";
        }

        public String getDisplay(WolfyUtilities api) {
            return api.getLanguageAPI().replaceKeys(display);
        }
    }
}
