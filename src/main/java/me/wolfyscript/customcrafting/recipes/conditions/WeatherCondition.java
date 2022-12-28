/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.recipes.conditions;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.util.Locale;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WeatherCondition extends Condition<WeatherCondition> {

    public static final NamespacedKey KEY = new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "weather");

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
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
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
            this.display = "$recipe_conditions.weather.modes." + super.toString().toLowerCase(Locale.ROOT) + "$";
        }

        public String getDisplay(WolfyUtilsBukkit api) {
            return api.getLanguageAPI().replaceKeys(display);
        }
    }

    public static class GUIComponent extends FunctionalGUIComponent<WeatherCondition> {

        public GUIComponent() {
            super(Material.WATER_BUCKET, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.getButtonBuilder().chatInput("conditions.weather.set").state(state -> state.icon(Material.WATER_BUCKET).action((holder, cache, btn, slot, details) -> {
                            cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WeatherCondition.class).toggleWeather();
                            return ButtonInteractionResult.cancel(true);
                        }).render((holder, cache, btn, slot, itemStack) -> {
                            return CallbackButtonRender.Result.of(Placeholder.parsed("weather", cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WeatherCondition.class).getWeather().getDisplay(api)));
                        })).register();
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(31, "conditions.weather.set");
                    });
        }
    }
}
