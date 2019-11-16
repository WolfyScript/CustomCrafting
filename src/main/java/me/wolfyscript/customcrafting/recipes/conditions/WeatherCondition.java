package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.World;
import org.bukkit.block.Block;

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
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
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
    public String toString() {
        return option.toString() + ";" + weather.toString();
    }

    @Override
    public void fromString(String value) {
        String[] args = value.split(";");
        this.option = Conditions.Option.valueOf(args[0]);
        if (args.length > 1) {
            this.weather = Weather.valueOf(args[1]);
        }
    }

    public enum Weather {
        STORM, THUNDER, STORM_THUNDER, NONE;

        private String display;

        Weather() {
            this.display = "$inventories.recipe_creator.conditions.items.weather.modes." + super.toString().toLowerCase(Locale.ROOT) + "$";
        }

        public String getDisplay(WolfyUtilities api) {
            return api.getLanguageAPI().getActiveLanguage().replaceKeys(display);
        }
    }
}
