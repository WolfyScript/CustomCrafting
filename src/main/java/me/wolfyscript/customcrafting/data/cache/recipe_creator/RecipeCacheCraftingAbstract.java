package me.wolfyscript.customcrafting.data.cache.recipe_creator;

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
    protected Map<Integer, Ingredient> ingredients;
    private S settings;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected RecipeCacheCraftingAbstract() {
        super();
        this.shapeless = false;
        this.ingredients = new HashMap<>();
    }

    protected RecipeCacheCraftingAbstract(CraftingRecipe<?, S> recipe) {
        super(recipe);
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
                        if (ingredient != null) {
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
        if (craftingRecipe instanceof AbstractRecipeShapeless<?, ?> shapelessRecipe) {
            shapelessRecipe.setIngredients(ingredients.values().stream());
        } else if (craftingRecipe instanceof AbstractRecipeShaped<?, ?> shaped) {
            shaped.setMirrorHorizontal(isMirrorHorizontal());
            shaped.setMirrorVertical(isMirrorVertical());
            shaped.setMirrorRotation(isMirrorRotation());
            Map<Character, Ingredient> ingredientMap = ingredients.entrySet().stream().collect(Collectors.toMap(entry -> CraftingRecipe.LETTERS.charAt(entry.getKey()), Map.Entry::getValue));
            shaped.generateMissingShape(List.copyOf(ingredientMap.keySet()));
            shaped.setIngredients(ingredientMap);
        }
        return craftingRecipe;
    }
}
