package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Locale;

public class WeatherCondition extends Condition<WeatherCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "weather");

    private Weather weather;

    public WeatherCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
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
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        Block block = data.getBlock();
        if (block != null) {
            World world = block.getWorld();
            return switch (weather) {
                case NONE -> !world.isThundering() && !world.hasStorm();
                case STORM -> world.hasStorm() && !world.isThundering();
                case THUNDER -> world.isThundering() && !world.hasStorm();
                case STORM_THUNDER -> world.isThundering() && world.hasStorm();
            };
        }
        return false;
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

    public static class GUIComponent extends FunctionalGUIComponent<WeatherCondition> {

        public GUIComponent() {
            super(Material.WATER_BUCKET, getLangKey(KEY.getKey(), "name"), List.of(getLangKey(KEY.getKey(), "description")),
                    (menu, api) -> {
                        menu.registerButton(new ActionButton<>("conditions.weather", Material.WATER_BUCKET, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                            cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WeatherCondition.class).toggleWeather();
                            return true;
                        }, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                            hashMap.put("%VALUE%", cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WeatherCondition.class).getWeather().getDisplay(api));
                            return itemStack;
                        }));
                    },
                    (update, cache, condition, recipe) -> {

                    });
        }
    }
}
