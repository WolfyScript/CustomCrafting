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

import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContextPlayer;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class ConditionScoreboard extends Condition<ConditionScoreboard> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "player_scoreboard");

    private final Map<String, BoolOperator> objectiveChecks;
    private final boolean failOnMissingObjective;
    private final BoolOperator check;

    public ConditionScoreboard() {
        super(KEY);
        this.check = null;
        this.failOnMissingObjective = true;
        this.objectiveChecks = new HashMap<>();
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
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            //Create the eval context for the optional check
            EvalContextPlayer context = new EvalContextPlayer(player);
            for (Objective objective : scoreboard.getObjectives()) {
                String varName = objective.getName();
                context.setVariable(varName, objective.getScore(player).getScore());
            }
            if (check != null && !check.evaluate(context)) {
                //Directly return if the check fails.
                return false;
            }
            for (Map.Entry<String, BoolOperator> entry : objectiveChecks.entrySet()) {
                String key = entry.getKey();
                Objective objective = scoreboard.getObjective(key);
                if (objective != null) {
                    //Set an eval context with just the specified value of this objective
                    EvalContextPlayer valueContext = new EvalContextPlayer(player);
                    valueContext.setVariable("value", objective.getScore(player).getScore());
                    if (!entry.getValue().evaluate(valueContext)) {
                        return false;
                    }
                } else if (failOnMissingObjective) {
                    //Return and cancel any further checks if the objective is missing
                    return false;
                }
            }
        }
        return true;
    }

    public static class GUIComponent extends FunctionalGUIComponent<ConditionScoreboard> {

        public GUIComponent() {
            super(Material.COMMAND_BLOCK, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                    },
                    (update, cache, condition, recipe) -> {
                    });
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type) || type == RecipeType.BREWING_STAND || type == RecipeType.GRINDSTONE;
        }
    }
}
