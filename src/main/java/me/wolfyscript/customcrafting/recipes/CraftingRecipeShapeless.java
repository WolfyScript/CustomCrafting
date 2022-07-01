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
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;

public class CraftingRecipeShapeless extends AbstractRecipeShapeless<CraftingRecipeShapeless, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapelessRecipe> {

    public CraftingRecipeShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    @JsonCreator
    public CraftingRecipeShapeless(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting, 3, new AdvancedRecipeSettings());
    }

    @Deprecated
    public CraftingRecipeShapeless(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    public CraftingRecipeShapeless(CraftingRecipeShapeless craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public CraftingRecipeShapeless clone() {
        return new CraftingRecipeShapeless(this);
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe getVanillaRecipe() {
        if (!getResult().isEmpty()) {
            var shapelessRecipe = new org.bukkit.inventory.ShapelessRecipe(new org.bukkit.NamespacedKey(getNamespacedKey().getNamespace(), getNamespacedKey().getKey()), getResult().getItemStack());
            for (Ingredient value : ingredients) {
                shapelessRecipe.addIngredient(new RecipeChoice.ExactChoice(value.getChoices().stream().map(CustomItem::getItemStack).distinct().toList()));
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
