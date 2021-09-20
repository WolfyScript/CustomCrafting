package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeSmithing;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

public class RecipeCacheSmithing extends RecipeCache<CustomRecipeSmithing> {

    private Ingredient base;
    private Ingredient addition;

    private boolean preserveEnchants;
    private boolean onlyChangeMaterial;

    RecipeCacheSmithing() {
        super();
    }

    RecipeCacheSmithing(CustomRecipeSmithing recipe) {
        super(recipe);
        this.base = recipe.getBase().clone();
        this.addition = recipe.getAddition().clone();
        this.preserveEnchants = recipe.isPreserveEnchants();
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            setBase(ingredient);
        } else {
            setAddition(ingredient);
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getBase() : getAddition();
    }

    @Override
    protected CustomRecipeSmithing constructRecipe() {
        return create(new CustomRecipeSmithing(key));
    }

    @Override
    protected CustomRecipeSmithing create(CustomRecipeSmithing recipe) {
        CustomRecipeSmithing recipeSmithing = super.create(recipe);
        recipeSmithing.setBase(base);
        recipeSmithing.setAddition(addition);

        recipeSmithing.setPreserveEnchants(preserveEnchants);
        recipeSmithing.setOnlyChangeMaterial(onlyChangeMaterial);
        return recipeSmithing;
    }

    public Ingredient getBase() {
        return base;
    }

    public void setBase(Ingredient base) {
        this.base = base;
    }

    public Ingredient getAddition() {
        return addition;
    }

    public void setAddition(Ingredient addition) {
        this.addition = addition;
    }

    public boolean isPreserveEnchants() {
        return preserveEnchants;
    }

    public void setPreserveEnchants(boolean preserveEnchants) {
        this.preserveEnchants = preserveEnchants;
    }

    public void setOnlyChangeMaterial(boolean onlyChangeMaterial) {
        this.onlyChangeMaterial = onlyChangeMaterial;
    }

    public boolean isOnlyChangeMaterial() {
        return onlyChangeMaterial;
    }
}
