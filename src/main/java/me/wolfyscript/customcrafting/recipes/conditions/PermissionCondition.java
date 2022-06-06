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
import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

public class PermissionCondition extends Condition<PermissionCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "permission");

    private String permission = "customcrafting.craft.%namespace%.%recipe_name%";

    public PermissionCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return ExperienceCondition.valid(recipe);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (recipe instanceof CustomRecipeCooking && data.getPlayer() == null) {
            return true;
        }
        if (data.getPlayer() == null) return false;
        var keyComponent = recipe.getNamespacedKey().getKeyComponent();
        return data.getPlayer().hasPermission(permission.replace("%namespace%", keyComponent.getFolder().replace("/", ".")).replace("%recipe_name%", keyComponent.getObject()));
    }

    public static class GUIComponent extends FunctionalGUIComponent<PermissionCondition> {

        public GUIComponent() {
            super(Material.REDSTONE, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, wolfyUtilities) -> {
                        menu.registerButton(new ChatInputButton<>("conditions.permission.set", Material.REDSTONE, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                            hashMap.put("%VALUE%", cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(PermissionCondition.class).getPermission());
                            return itemStack;
                        }, (guiHandler, player, s, strings) -> {
                            guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions().getByType(PermissionCondition.class).setPermission(s.trim());
                            return false;
                        }));
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(31, "conditions.permission.set");
                    });
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type) || type == RecipeType.BREWING_STAND || type == RecipeType.GRINDSTONE;
        }
    }
}
