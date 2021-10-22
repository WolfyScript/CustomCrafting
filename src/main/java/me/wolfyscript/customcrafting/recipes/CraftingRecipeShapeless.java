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
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

public class CraftingRecipeShapeless extends AbstractRecipeShapeless<CraftingRecipeShapeless, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapelessRecipe> {

    public CraftingRecipeShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    public CraftingRecipeShapeless(NamespacedKey key) {
        super(key, 3, new AdvancedRecipeSettings());
    }

    public CraftingRecipeShapeless(CraftingRecipeShapeless craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public RecipeType<CraftingRecipeShapeless> getRecipeType() {
        return RecipeType.CRAFTING_SHAPELESS;
    }

    @Override
    public CraftingRecipeShapeless clone() {
        return new CraftingRecipeShapeless(this);
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe getVanillaRecipe() {
        if (!getSettings().isAllowVanillaRecipe() && !getResult().isEmpty()) {
            var shapelessRecipe = new org.bukkit.inventory.ShapelessRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            for (Ingredient value : ingredients) {
                shapelessRecipe.addIngredient(new RecipeChoice.MaterialChoice(value.getChoices().stream().map(customItem -> customItem.getItemStack().getType()).distinct().toList()));
            }
            shapelessRecipe.setGroup(getGroup());
            return shapelessRecipe;
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
