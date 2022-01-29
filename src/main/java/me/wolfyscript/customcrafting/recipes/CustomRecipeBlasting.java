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
import org.bukkit.inventory.BlastingRecipe;

public class CustomRecipeBlasting extends CustomRecipeCooking<CustomRecipeBlasting, BlastingRecipe> {

    public CustomRecipeBlasting(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.type = RecipeType.BLAST_FURNACE;
    }

    public CustomRecipeBlasting(NamespacedKey key) {
        super(key);
        this.type = RecipeType.BLAST_FURNACE;
    }

    public CustomRecipeBlasting(CustomRecipeBlasting customRecipeBlasting) {
        super(customRecipeBlasting);
        this.type = RecipeType.BLAST_FURNACE;
    }

    @Override
    public BlastingRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            var recipe = new BlastingRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getChoices().get(0).create(), getRecipeChoice(), getExp(), getCookingTime());
            recipe.setGroup(group);
            return recipe;
        }
        return null;
    }

    @Override
    public CustomRecipeBlasting clone() {
        return new CustomRecipeBlasting(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.BLAST_FURNACE);
    }
}
