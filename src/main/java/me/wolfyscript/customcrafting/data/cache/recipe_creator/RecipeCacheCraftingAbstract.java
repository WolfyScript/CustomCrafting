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

package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.AbstractRecipeShaped;
import me.wolfyscript.customcrafting.recipes.AbstractRecipeShapeless;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class RecipeCacheCraftingAbstract<S extends CraftingRecipeSettings<S>> extends RecipeCache<CraftingRecipe<?, S>> {

    protected boolean shapeless;
    protected final Map<Integer, Ingredient> ingredients;
    private S settings;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected RecipeCacheCraftingAbstract(CustomCrafting customCrafting) {
        super(customCrafting);
        this.shapeless = false;
        this.ingredients = new HashMap<>();
        this.mirrorHorizontal = false;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    protected RecipeCacheCraftingAbstract(CustomCrafting customCrafting, CraftingRecipe<?, S> recipe) {
        super(customCrafting, recipe);
        this.settings = recipe.getSettings().clone();
        this.shapeless = RecipeType.CRAFTING_SHAPELESS.isInstance(recipe) || RecipeType.ELITE_CRAFTING_SHAPELESS.isInstance(recipe);
        if (recipe instanceof AbstractRecipeShaped<?, ?> shaped) {
            this.mirrorHorizontal = shaped.mirrorHorizontal();
            this.mirrorVertical = shaped.mirrorVertical();
            this.mirrorRotation = shaped.mirrorRotation();
            this.ingredients = new HashMap<>();
            int i = 0;
            int ingredientIndex = 0;
            for (int r = 0; r < shaped.getMaxGridDimension(); r++) {
                for (int c = 0; c < shaped.getMaxGridDimension(); c++) {
                    if (c < shaped.getInternalShape().getWidth() && r < shaped.getInternalShape().getHeight() && ingredientIndex < shaped.getIngredients().size()) {
                        var ingredient = shaped.getIngredients().get(ingredientIndex);
                        if (ingredient != null && !ingredient.isEmpty()) {
                            this.ingredients.put(i, ingredient.clone());
                        }
                        ingredientIndex++;
                    }
                    i++;
                }
            }
        } else {
            AtomicInteger index = new AtomicInteger();
            this.ingredients = recipe.getIngredients().stream().collect(Collectors.toMap(ingredient -> index.getAndIncrement(), ingredient -> ingredient));
        }
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredients) {
        if (ingredients != null) {
            ingredients.buildChoices();
        }
        if (ingredients == null || ingredients.isEmpty()) {
            this.ingredients.remove(slot);
        } else {
            this.ingredients.put(slot, ingredients);
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return ingredients.get(slot);
    }

    public Map<Integer, Ingredient> getIngredients() {
        return ingredients;
    }

    public S getSettings() {
        return settings;
    }

    public void setSettings(S settings) {
        this.settings = settings;
    }

    public boolean isShapeless() {
        return shapeless;
    }

    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    public boolean isMirrorHorizontal() {
        return mirrorHorizontal;
    }

    public void setMirrorHorizontal(boolean mirrorHorizontal) {
        this.mirrorHorizontal = mirrorHorizontal;
    }

    public boolean isMirrorVertical() {
        return mirrorVertical;
    }

    public void setMirrorVertical(boolean mirrorVertical) {
        this.mirrorVertical = mirrorVertical;
    }

    public boolean isMirrorRotation() {
        return mirrorRotation;
    }

    public void setMirrorRotation(boolean mirrorRotation) {
        this.mirrorRotation = mirrorRotation;
    }

    @Override
    protected CraftingRecipe<?, S> create(CraftingRecipe<?, S> recipe) {
        CraftingRecipe<?, S> craftingRecipe = super.create(recipe);
        if (craftingRecipe instanceof AbstractRecipeShapeless<?, ?> shapelessRecipe) {
            shapelessRecipe.setIngredients(ingredients.values().stream());
        } else if (craftingRecipe instanceof AbstractRecipeShaped<?, ?> shaped) {
            shaped.setMirrorHorizontal(isMirrorHorizontal());
            shaped.setMirrorVertical(isMirrorVertical());
            shaped.setMirrorRotation(isMirrorRotation());
            Map<Character, Ingredient> ingredientMap = ingredients.entrySet().stream().filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty()).collect(Collectors.toMap(entry -> CraftingRecipe.LETTERS.charAt(entry.getKey()), Map.Entry::getValue));
            shaped.generateMissingShape(List.copyOf(ingredientMap.keySet()));
            shaped.setIngredients(ingredientMap);
        }
        return craftingRecipe;
    }
}
