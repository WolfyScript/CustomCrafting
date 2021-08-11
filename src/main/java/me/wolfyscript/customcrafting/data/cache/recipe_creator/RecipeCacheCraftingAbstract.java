package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.AbstractRecipeShaped;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ICraftingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class RecipeCacheCraftingAbstract<S extends CraftingRecipeSettings<S>> extends RecipeCache<CraftingRecipe<?, S>> {

    protected boolean shapeless;
    protected Map<Integer, Ingredient> ingredients;
    private S settings;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected RecipeCacheCraftingAbstract(RecipeCreatorCache creatorCache) {
        super(creatorCache);
        this.shapeless = false;
        this.ingredients = new HashMap<>();
    }

    protected RecipeCacheCraftingAbstract(RecipeCreatorCache creatorCache, CraftingRecipe<?, S> recipe) {
        super(creatorCache, recipe);
        this.settings = recipe.getSettings().clone();
        this.shapeless = RecipeType.WORKBENCH_SHAPED.isInstance(recipe) || RecipeType.ELITE_WORKBENCH_SHAPED.isInstance(recipe);
        this.ingredients = recipe.getIngredients().entrySet().stream().collect(Collectors.toMap(entry -> ICraftingRecipe.LETTERS.indexOf(entry.getKey()), Map.Entry::getValue));

        if (recipe instanceof AbstractRecipeShaped<?, ?> shaped) {
            this.mirrorHorizontal = shaped.mirrorHorizontal();
            this.mirrorVertical = shaped.mirrorVertical();
            this.mirrorRotation = shaped.mirrorRotation();
        }
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            this.ingredients.remove(slot);
        } else {
            ingredients.buildChoices();
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
        if (craftingRecipe instanceof AbstractRecipeShaped<?, ?> shaped) {
            shaped.setMirrorHorizontal(isMirrorHorizontal());
            shaped.setMirrorVertical(isMirrorVertical());
            shaped.setMirrorRotation(isMirrorRotation());
        }
        craftingRecipe.setIngredients(ingredients.entrySet().stream().collect(Collectors.toMap(entry -> ICraftingRecipe.LETTERS.charAt(entry.getKey()), Map.Entry::getValue)));
        return craftingRecipe;
    }
}
