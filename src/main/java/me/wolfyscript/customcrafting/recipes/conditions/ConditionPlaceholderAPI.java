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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.compatibility.plugins.PlaceholderAPIIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionPlaceholderAPI extends Condition<ConditionPlaceholderAPI> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "player_placeholderapi");

    private final String placeholder;
    private final BoolOperator check;
    @JsonIgnore
    private final CustomCrafting customCrafting;

    public ConditionPlaceholderAPI(@JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(KEY);
        this.customCrafting = customCrafting;
        this.check = null;
        this.placeholder = "";
        setAvailableOptions(Conditions.Option.EXACT);
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return RecipeType.Container.CRAFTING.isInstance(recipe) || RecipeType.Container.ELITE_CRAFTING.isInstance(recipe) || switch (recipe.getRecipeType().getType()) {
            case BREWING_STAND, GRINDSTONE -> true;
            default -> false;
        };
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        Player player = data.getPlayer();
        if (player != null) {
            var papi = customCrafting.getApi().getCore().getCompatibilityManager().getPlugins().getIntegration("PlaceholderAPI", PlaceholderAPIIntegration.class);
            if (papi == null) return false;
            String value = papi.setPlaceholders(player, placeholder);
            //Create the eval context for the check
            EvalContext context = new EvalContext();
            context.setVariable("value", value);

            //TODO: Maybe add options to check multiple different placeholders?

            return check == null || check.evaluate(context);
        }
        return true;
    }

    public static class GUIComponent extends FunctionalGUIComponent<ConditionPlaceholderAPI> {

        public GUIComponent() {
            super(Material.COMMAND_BLOCK, getLangKey(KEY.getKey(), "name"), List.of(getLangKey(KEY.getKey(), "description")),
                    (menu, api) -> {},
                    (update, cache, condition, recipe) -> {});
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type) || type == RecipeType.BREWING_STAND || type == RecipeType.GRINDSTONE;
        }
    }
}
