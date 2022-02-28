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
import me.wolfyscript.utilities.util.context.EvalContext;
import me.wolfyscript.utilities.util.operators.BoolOperator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionScoreboard extends Condition<ConditionScoreboard> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "scoreboard");

    private BoolOperator check;

    public ConditionScoreboard() {
        super(KEY);
        this.check = null;
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
        EvalContext context = new EvalContext();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Player player = data.getPlayer();
        if (player != null) {
            for (Objective objective : scoreboard.getObjectives()) {
                String varName = objective.getName();
                context.setVariable(varName, objective.getScore(player));
            }
            if (check != null) {
                return check.evaluate(context);
            }
        } else {
            for (String entry : scoreboard.getEntries()) {
                for (Score score : scoreboard.getScores(entry)) {
                    context.setVariable(entry + "_" + score.getObjective().getName(), score.getScore());
                }
            }
            if (check != null) {
                return check.evaluate(context);
            }
        }
        return true;
    }

    public static class GUIComponent extends FunctionalGUIComponent<ConditionScoreboard> {

        public GUIComponent() {
            super(Material.EXPERIENCE_BOTTLE, getLangKey(KEY.getKey(), "name"), List.of(getLangKey(KEY.getKey(), "description")),
                    (menu, api) -> {},
                    (update, cache, condition, recipe) -> {});
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type) || type == RecipeType.BREWING_STAND || type == RecipeType.GRINDSTONE;
        }
    }
}
