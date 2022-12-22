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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import me.wolfyscript.customcrafting.gui.recipe_creator.MenuConditions;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;

public class ExperienceCondition extends Condition<ExperienceCondition> {

    public static final NamespacedKey KEY = new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "player_experience");

    static boolean valid(CustomRecipe<?> recipe) {
        return RecipeType.Container.CRAFTING.isInstance(recipe) || RecipeType.Container.ELITE_CRAFTING.isInstance(recipe) || switch (recipe.getRecipeType().getType()) {
            case BREWING_STAND, GRINDSTONE -> true;
            default -> false;
        };
    }

    @JsonProperty("experience")
    private int expLevel = 0;

    public ExperienceCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return valid(recipe);
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (data.getPlayer() != null) {
            int currentExp = data.getPlayer().getLevel();
            return switch (option) {
                case EXACT -> currentExp == expLevel;
                case LOWER -> currentExp < expLevel;
                case LOWER_EXACT -> currentExp <= expLevel;
                case HIGHER -> currentExp > expLevel;
                case HIGHER_EXACT -> currentExp >= expLevel;
                case HIGHER_LOWER -> currentExp < expLevel || currentExp > expLevel;
                default -> true;
            };
        }
        return true;
    }

    @JsonIgnore
    public float getExpLevel() {
        return expLevel;
    }

    @JsonIgnore
    public void setExpLevel(int expLevel) {
        this.expLevel = expLevel;
    }

    public static class GUIComponent extends FunctionalGUIComponent<ExperienceCondition> {

        public GUIComponent() {
            super(Material.EXPERIENCE_BOTTLE, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.getButtonBuilder().chatInput("conditions.player_experience.set").state(state -> state.icon(Material.EXPERIENCE_BOTTLE).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> {
                            return CallbackButtonRender.UpdateResult.of(Placeholder.parsed("value", String.valueOf(cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(ExperienceCondition.class).getExpLevel())));
                        })).inputAction((guiHandler, player, s, strings) -> {
                            try {
                                int value = Integer.parseInt(s);
                                guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions().getByType(ExperienceCondition.class).setExpLevel(value);
                            } catch (NumberFormatException ex) {
                                api.getChat().sendKey(player, "recipe_creator", "valid_number");
                            }
                            return false;
                        }).register();
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(30, "conditions.player_experience.set");
                        update.setButton(32, MenuConditions.TOGGLE_MODE);
                    });
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type) || type == RecipeType.BREWING_STAND || type == RecipeType.GRINDSTONE;
        }
    }
}
