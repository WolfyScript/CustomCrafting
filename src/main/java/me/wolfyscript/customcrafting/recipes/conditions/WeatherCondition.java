package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    public void fromJsonElement(JsonElement jsonElement) {
        try {
            this.weather = Weather.valueOf(((JsonObject)jsonElement).getAsJsonPrimitive("weather").getAsString());
        }catch (IllegalArgumentException ex){
            //EMPTY CATCH
        }
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = (JsonObject) super.toJsonElement();
        jsonObject.addProperty("weather", weather.toString());
        return jsonObject;
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
