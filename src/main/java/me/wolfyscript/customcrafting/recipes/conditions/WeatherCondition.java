package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;

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

    public void toggleWeather(){
        int index = weather.ordinal();
        if(index < Weather.values().length-1){
            index++;
        }else{
            index = 0;
        }
        weather = Weather.values()[index];
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        return true;
    }

    public enum Weather{
        STORM, THUNDER, STORM_THUNDER, NONE;

        private String display;

        Weather(){
            this.display = "$inventories.recipe_creator.conditions.items.weather.modes."+super.toString().toLowerCase(Locale.ROOT)+"$";
        }

        public String getDisplay(WolfyUtilities api) {
            return api.getLanguageAPI().getActiveLanguage().replaceKeys(display);
        }
    }
}
