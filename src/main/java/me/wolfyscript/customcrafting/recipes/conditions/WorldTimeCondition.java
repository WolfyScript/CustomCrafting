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

import me.wolfyscript.customcrafting.gui.recipe_creator.MenuConditions;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

public class WorldTimeCondition extends Condition<WorldTimeCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_time");

    private long time = 0;

    public WorldTimeCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (data.getBlock() != null) {
            long currentTime = data.getBlock().getWorld().getTime();
            return switch (option) {
                case EXACT -> currentTime == time;
                case LOWER -> currentTime < time;
                case LOWER_EXACT -> currentTime <= time;
                case HIGHER -> currentTime > time;
                case HIGHER_EXACT -> currentTime >= time;
                case HIGHER_LOWER -> currentTime < time || currentTime > time;
                default -> true;
            };
        }
        return false;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public static class GUIComponent extends FunctionalGUIComponent<WorldTimeCondition> {

        public GUIComponent() {
            super(Material.CLOCK, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.registerButton(new ChatInputButton<>("conditions.world_time.set", Material.CLOCK, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                            hashMap.put("%VALUE%", cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WorldTimeCondition.class).getTime());
                            return itemStack;
                        }, (guiHandler, player, s, strings) -> {
                            try {
                                long value = Long.parseLong(s);
                                guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WorldTimeCondition.class).setTime(value);
                            } catch (NumberFormatException ex) {
                                api.getChat().sendKey(player, "recipe_creator", "valid_number");
                            }
                            return false;
                        }));
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(30, "conditions.world_time.set");
                        update.setButton(32, MenuConditions.TOGGLE_MODE);
                    });
        }
    }
}
