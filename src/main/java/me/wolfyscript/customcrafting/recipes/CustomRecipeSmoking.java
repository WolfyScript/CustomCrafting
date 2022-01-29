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

package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.SmokingRecipe;

public class CustomRecipeSmoking extends CustomRecipeCooking<CustomRecipeSmoking, SmokingRecipe> {

    public CustomRecipeSmoking(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.type = RecipeType.SMOKER;
    }

    public CustomRecipeSmoking(NamespacedKey key) {
        super(key);
        this.type = RecipeType.SMOKER;
    }

    public CustomRecipeSmoking(CustomRecipeSmoking customRecipeSmoking) {
        super(customRecipeSmoking);
        this.type = RecipeType.SMOKER;
    }

    @Override
    public CustomRecipeSmoking clone() {
        return new CustomRecipeSmoking(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.SMOKER);
    }

    @Override
    public SmokingRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            var recipe = new SmokingRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime());
            recipe.setGroup(group);
            return recipe;
        }
        return null;
    }
}
