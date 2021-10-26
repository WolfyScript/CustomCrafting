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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

public class CraftingRecipeShaped extends AbstractRecipeShaped<CraftingRecipeShaped, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapedRecipe> {

    public CraftingRecipeShaped(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    public CraftingRecipeShaped(NamespacedKey key) {
        super(key, 3, new AdvancedRecipeSettings());
    }

    public CraftingRecipeShaped(CraftingRecipeShaped craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeShaped> getRecipeType() {
        return RecipeType.CRAFTING_SHAPED;
    }

    @Override
    public CraftingRecipeShaped clone() {
        return new CraftingRecipeShaped(this);
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe getVanillaRecipe() {
        if (!getResult().isEmpty() && !ingredients.isEmpty()) {
            var recipe = new org.bukkit.inventory.ShapedRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            recipe.shape(getShape());
            mappedIngredients.forEach((character, items) -> recipe.setIngredient(character, new RecipeChoice.ExactChoice(items.getChoices().stream().map(CustomItem::getItemStack).distinct().toList())));
            recipe.setGroup(getGroup());
            return recipe;
        }
        return null;
    }

    @Override
    public boolean isVisibleVanillaBook() {
        return vanillaBook;
    }

    @Override
    public void setVisibleVanillaBook(boolean vanillaBook) {
        this.vanillaBook = vanillaBook;
    }
}