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
 */me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

public class CraftingRecipeEliteShapeless extends AbstractRecipeShapeless<CraftingRecipeEliteShapeless, EliteRecipeSettings> {

    public CraftingRecipeEliteShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 6, EliteRecipeSettings.class);
    }

    public CraftingRecipeEliteShapeless(NamespacedKey key) {
        super(key, 6, new EliteRecipeSettings());
    }

    public CraftingRecipeEliteShapeless(CraftingRecipeEliteShapeless eliteCraftingRecipe) {
        super(eliteCraftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeEliteShapeless> getRecipeType() {
        return RecipeType.ELITE_CRAFTING_SHAPELESS;
    }

    @Override
    public CraftingRecipeEliteShapeless clone() {
        return new CraftingRecipeEliteShapeless(this);
    }
}
