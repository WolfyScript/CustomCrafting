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

package me.wolfyscript.customcrafting.utils.cooking;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.data.CookingRecipeData;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

/**
 * Versions prior to 1.17 don't have the FurnaceStartSmeltEvent, so the recipe must be determined manually.
 */
public class BukkitSmeltAPIAdapter extends SmeltAPIAdapter {

    public BukkitSmeltAPIAdapter(CustomCrafting customCrafting, CookingManager manager) {
        super(customCrafting, manager);
    }

    @Override
    public Pair<CookingRecipeData<?>, Boolean> process(FurnaceSmeltEvent event, Block block, Furnace furnace) {
        Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(switch (furnace.getType()) {
            case BLAST_FURNACE -> RecipeType.BLASTING;
            case SMOKER -> RecipeType.SMOKING;
            default -> RecipeType.SMELTING;
        });
        boolean customRecipe = false;
        while (recipeIterator.hasNext()) {
            if (recipeIterator.next() instanceof CookingRecipe<?> recipe && ICustomVanillaRecipe.isPlaceholderRecipe(recipe.getKey()) && recipe.getResult().isSimilar(event.getResult())) {
                customRecipe = true;
                Pair<CookingRecipeData<?>, Boolean> data = processRecipe(event.getSource(), ICustomVanillaRecipe.toOriginalKey(recipe.getKey()), block);
                if (data.getKey() != null) {
                    return data;
                }
            }
        }
        return new Pair<>(null, customRecipe);
    }
}
