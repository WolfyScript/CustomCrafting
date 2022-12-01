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
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import org.bukkit.Material;

public class AdvancedWorkbenchCondition extends Condition<AdvancedWorkbenchCondition> {

    public static final NamespacedKey KEY = new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "advanced_workbench");

    public AdvancedWorkbenchCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (recipe instanceof CraftingRecipe) {
            if (data.getBlock() != null) {
                var customItem = NamespacedKeyUtils.getCustomItem(data.getBlock());
                return customItem != null && (customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE) || customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_WORKBENCH));
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return RecipeType.Container.CRAFTING.isInstance(recipe);
    }

    public static class GUIComponent extends IconGUIComponent<AdvancedWorkbenchCondition> {

        public GUIComponent() {
            super(Material.CRAFTING_TABLE, getLangKey("advanced_crafting_table", "name"), List.of(getLangKey("advanced_crafting_table", "description")));
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type);
        }
    }
}
